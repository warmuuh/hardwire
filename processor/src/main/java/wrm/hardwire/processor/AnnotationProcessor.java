package wrm.hardwire.processor;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import wrm.hardwire.Module;
import wrm.hardwire.processor.model.GenClass;
import wrm.hardwire.processor.model.GenField;
import wrm.hardwire.processor.model.GenModelRoot;
import wrm.hardwire.processor.model.GenModule;
import wrm.hardwire.processor.model.GenModuleRef;


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
		root = new GenModelRoot(typeUtils);
		moduleVisitor = new ModuleVisitor(processingEnv, root);
		singletonVisitor = new SingletonVisitor(processingEnv, root);
		warn(null, "initialization");
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
		extractModules(env);
		extractSingletons(env);
		root.postProcess();

		if (elements.size() == 0 || env.processingOver()){
			writer.writeFactories(root.getRoots());
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
