package wrm.hardwire.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

import org.apache.commons.lang3.exception.ExceptionUtils;

import wrm.hardwire.Module;
import wrm.hardwire.processor.model.GenModelRoot;


@SupportedAnnotationTypes({"javax.inject.Singleton", "wrm.hardwire.Module"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"jdkOverwrite", "disableIterativeCheck"})
public class AnnotationProcessor extends AbstractProcessor {

	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;

	GenModelRoot root;
	private ModuleBaseWriter writer;
	private ModuleVisitor moduleVisitor;
	private SingletonVisitor singletonVisitor;
	private boolean outputWritten = false;
	private boolean foundElements = false;
	private static Long jdkOverwrite;
	private boolean disableIterativeCheck;

	public static Long getJdkOverwrite() {
		return jdkOverwrite;
	}
	
	
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		writer = new ModuleBaseWriter(processingEnv.getFiler());
		messager = processingEnv.getMessager();
		root = new GenModelRoot(processingEnv.getTypeUtils());
		moduleVisitor = new ModuleVisitor(processingEnv, root);
		singletonVisitor = new SingletonVisitor(processingEnv, root);
		String jdkOverwriteOption = processingEnv.getOptions().get("jdkOverwrite");
		jdkOverwrite = jdkOverwriteOption == null ? null : Long.parseLong(jdkOverwriteOption);
		disableIterativeCheck = Boolean.parseBoolean(processingEnv.getOptions().get("disableIterativeCheck"));
		
		info(null, "initialization");
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
		try{
			if (!foundElements && !validateNonIterativeBuild(env)) {
				return false;
			}
			boolean hasModules = extractModules(env);
			boolean hasSingletons = extractSingletons(env);
			foundElements |= hasModules;
			foundElements |= hasSingletons;
			
			boolean foundElementsInThisRound = hasModules || hasSingletons;
			
			root.postProcess();
			info(null, "round stats: hasModules: %s hasSingletons: %s", hasModules, hasSingletons);
			info(null, "round stats: foundElements: %s foundElementsInThisRound: %s", foundElements, foundElementsInThisRound);
			if (foundElements && !foundElementsInThisRound && !outputWritten) {
				try{
					info(null, "writing modules");

					writer.writeFactories(root.getRoots());
					outputWritten = true;
				} catch (Throwable e){
					messager.printMessage(Kind.ERROR, e.getMessage());
				}
			}
		} catch (Throwable e){
			String stackTrace = ExceptionUtils.getStackTrace(e);
			messager.printMessage(Kind.ERROR, stackTrace);
		}
		return false;
	}


	private boolean validateNonIterativeBuild(RoundEnvironment env) {
		if (disableIterativeCheck)
			return true;
		
		Set<? extends Element> modules = env.getElementsAnnotatedWith(Module.class);
		Set<? extends Element> singletons = env.getElementsAnnotatedWith(Singleton.class);
		int moduleCount = modules.size();
		int singletonCount = singletons.size();
		
		//user modified only one file, log warning and skip
		if (moduleCount + singletonCount == 1) {
			Element singleElement = moduleCount > 0 ? modules.iterator().next() : singletons.iterator().next();
			error(singleElement, "Only one element found to process. "
					+ "Hardwire does not support iterative compilation. "
					+ "Please do a full clean\n"
					+ "Note: this could be false alarm if you only have a single file yet.");
			return false;
		}
		
		return true;
	}

	private boolean extractModules(RoundEnvironment env) {
		Set<? extends Element> moduleElements = env.getElementsAnnotatedWith(Module.class);
		info(null, "found modules: %s", moduleElements.toString());

		for (Element element : moduleElements) {
			moduleVisitor.visitModule(element);
		}
		return !moduleElements.isEmpty();
	}
	
	private boolean extractSingletons(RoundEnvironment env) {
		Set<? extends Element> singletonElements = env.getElementsAnnotatedWith(Singleton.class);
		info(null, "found singletons: %s", singletonElements.toString());
		for (Element element : singletonElements) {
			singletonVisitor.visitClass(element);
		}
		return !singletonElements.isEmpty();
	}
	

	private void error(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}
	private void warn(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
	}
	private void info(Element e, String msg, Object... args) {
//		messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e);
	}
}
