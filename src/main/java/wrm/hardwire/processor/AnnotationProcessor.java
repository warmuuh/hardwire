package wrm.hardwire.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import wrm.hardwire.Module;
import wrm.hardwire.processor.model.GenModelRoot;


@SupportedAnnotationTypes({"javax.inject.Singleton", "wrm.hardwire.Module"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AnnotationProcessor extends AbstractProcessor {

	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;

	GenModelRoot root;
	private ModuleBaseWriter writer;
	private ModuleVisitor moduleVisitor;
	private SingletonVisitor singletonVisitor;
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		writer = new ModuleBaseWriter(processingEnv.getFiler());
		messager = processingEnv.getMessager();
		root = new GenModelRoot(processingEnv.getTypeUtils());
		moduleVisitor = new ModuleVisitor(processingEnv, root);
		singletonVisitor = new SingletonVisitor(processingEnv, root);
		warn(null, "initialization");
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
		try{
			extractModules(env);
			extractSingletons(env);
			root.postProcess();

			if (elements.size() == 0 || env.processingOver()){
				try{
					writer.writeFactories(root.getRoots());
				} catch (Throwable e){
					messager.printMessage(Kind.ERROR, e.getMessage());
				}
			}
		} catch (Throwable e){
			messager.printMessage(Kind.ERROR, e.getMessage());
		}
		return false;
	}


	private void extractModules(RoundEnvironment env) {
		for (Element element : env.getElementsAnnotatedWith(Module.class)) {
			moduleVisitor.visitModule(element);
		}
	}
	
	private void extractSingletons(RoundEnvironment env) {
		for (Element element : env.getElementsAnnotatedWith(Singleton.class)) {
			singletonVisitor.visitClass(element);
		}
	}
	

	private void error(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}
	private void warn(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
	}
}
