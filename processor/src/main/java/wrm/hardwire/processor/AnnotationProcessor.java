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
import wrm.hardwire.processor.model.GenModule;
import wrm.hardwire.processor.model.GenModuleRef;


@SupportedAnnotationTypes({"javax.inject.Singleton", "wrm.hardwire.Module"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AnnotationProcessor extends AbstractProcessor {

	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;

	List<GenClass> classes = new LinkedList<>();
	
	List<GenModule> roots = new LinkedList<>();
	private ModuleBaseWriter writer;
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		typeUtils = processingEnv.getTypeUtils();
		elementUtils = processingEnv.getElementUtils();
		writer = new ModuleBaseWriter(processingEnv.getFiler());
		messager = processingEnv.getMessager();
		warn(null, "initialization");
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {

		
		extractModules(env);
		
		extractSingletons(env);
		analizeFields();
		analizePostConstructMethods();
		
		sortClassesToModules();

		if (elements.size() == 0 || env.processingOver()){
			writer.writeFactories(roots);
		}
		
		return false;
	}

	private void analizePostConstructMethods() {
		for(GenClass gc : classes){
			Element element = gc.getElement();
				for (Element methodElement : element.getEnclosedElements()) {
					if (methodElement.getKind() != ElementKind.METHOD) continue;
					if (methodElement.getAnnotation(PostConstruct.class) == null) continue;
					gc.setPostConstructMethod(methodElement.getSimpleName().toString());
				}
		}
	}

	private void sortClassesToModules() {
		for (GenClass genClass : classes) {
			for (GenModule module : roots) {
				boolean isPackage = genClass.getPackageName().equals(module.getPackageName())
						|| genClass.getPackageName().startsWith(module.getPackageName()+".");
				if (isPackage && !module.getClasses().contains(genClass)){
					module.getClasses().add(genClass);
				}
			}
		}
		
		
		
		for (GenModule module : roots) {
			for (GenClass genClass : module.getClasses()) {
				for (GenField genfield : genClass.getFields()) {
					GenModule rootForClass = getRootForClass(genfield.getType());
					if (rootForClass != null && !rootForClass.equals(module)){
						for (GenModuleRef moduleRef : module.getReferences()) {
							boolean inPackage = genfield.getType().getPackageName().equals(moduleRef.getPackageName()) 
									|| genfield.getType().getPackageName().startsWith(moduleRef.getPackageName()+".");
							if(inPackage)
								genfield.setModuleRef(moduleRef.getName());
						}
					}
						
				}
			
			}
		}
	}

	public GenModule getRootForClass(GenClass gc){
		for (GenModule module : roots) {
			for (GenClass genClass : module.getClasses()) {
				if (genClass.equals(gc))
					return module;
			}
		}
		return null;
	}
	
	
	private void extractModules(RoundEnvironment env) {
		Element actionElement = processingEnv.getElementUtils().getTypeElement(
				Module.class.getName() );
		TypeMirror moduleType = actionElement.asType();
		
		for (Element element : env.getElementsAnnotatedWith(Module.class)) {
			
			PackageElement pkg = elementUtils.getPackageOf(element);
			String moduleName = element.getSimpleName() + "Base";
			GenModule genModule = new GenModule(moduleName, pkg.getQualifiedName().toString());
			List<? extends AnnotationMirror> allAnnotationMirrors = elementUtils.getAllAnnotationMirrors(element);
			
			for (AnnotationMirror annotationMirror : allAnnotationMirrors) {
				if (!annotationMirror.getAnnotationType().toString().equals(moduleType.toString()))
					continue;
				for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
					String keyName = entry.getKey().getSimpleName().toString();
					if (keyName.equals("imports")){
						List<AnnotationValue> values = (List<AnnotationValue>) entry.getValue().getValue();
						addModuleReferences(genModule, values);
					}
					if (keyName.equals("external")){
						List<AnnotationValue> values = (List<AnnotationValue>) entry.getValue().getValue();
						addModuleDynamic(genModule, values);
					}
				}
					
			}
			if (genModule.getReferences().size() > 0)
				genModule.getReferences().get(genModule.getReferences().size() -1).setLast(true);
			roots.add(genModule);
		}
		
		
	}

	private void addModuleReferences(GenModule genModule, List<AnnotationValue> values) {
		for(AnnotationValue imp : values){
			Element classElement = ((javax.lang.model.type.DeclaredType)imp.getValue()).asElement();
			GenModuleRef moduleRef = new GenModuleRef();
			String className = classElement.getSimpleName().toString();
			String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
			moduleRef.setPackageName(packageName);
			moduleRef.setClassName(className);
			moduleRef.setName("ref" + className);
			genModule.getReferences().add(moduleRef);
		}
	}

	private void addModuleDynamic(GenModule genModule, List<AnnotationValue> values) {
		for(AnnotationValue imp : values){
			Element classElement = ((javax.lang.model.type.DeclaredType)imp.getValue()).asElement();
			String className = classElement.getSimpleName().toString();
			String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
			GenClass classRef = new GenClass(null);
			classRef.setAbstr(true);
			classRef.setPackageName(packageName);
			classRef.setName(className);
			genModule.getClasses().add(classRef);
		}
	}
	private void analizeFields() {
		List<GenClass> abstrClasses = new LinkedList<>();
		for(GenClass gc : classes){
			gc.getFields().clear(); //for now, just reset fields
			Element element = gc.getElement();
			try{
				for (Element fieldEle : element.getEnclosedElements()) {
					if (fieldEle.getKind() != ElementKind.FIELD)
						continue;
					
					if (fieldEle.getAnnotation(Inject.class) == null)
						continue;
					
				
					GenClass fGenClass = null;
					for(GenClass ftype : classes){
						boolean isAssignable = typeUtils.isAssignable(ftype.getElement().asType(), fieldEle.asType());
						if (isAssignable){
							fGenClass = ftype;
							break;
						}
					}
					if (fGenClass == null){
						//no matching type found for field, add it as "dynamic" type:
						
						Element fieldType = typeUtils.asElement(fieldEle.asType());
						PackageElement packageOf = elementUtils.getPackageOf(fieldType);
						GenClass fieldClass = new GenClass(fieldType);
						fieldClass.setName(fieldType.getSimpleName().toString());
						fieldClass.setPackageName(packageOf.getQualifiedName().toString());
						fieldClass.setAbstr(true);
					
						abstrClasses.add(fieldClass);
					}
					GenField genfield = new GenField(fieldEle.getSimpleName().toString(), fGenClass);
					gc.getFields().add(genfield);
				}	
			} catch (NullPointerException npe){
				messager.printMessage(Kind.WARNING, "NullPointer catched");
			}
		}
		classes.addAll(abstrClasses);
	}

	private void extractSingletons(RoundEnvironment env) {
		for (Element element : env.getElementsAnnotatedWith(Singleton.class)) {
			if (element.getKind() != ElementKind.CLASS) {
				error(element, "Only classes are supported for  @Singleton");
				continue;
			}
			GenClass genClass = readGenClass(element);
			classes.add(genClass);
		}
	}
	
	
	private GenClass readGenClass(Element element) {
		GenClass gc = new GenClass(element);
		gc.setName(element.getSimpleName().toString());
		gc.setPackageName(elementUtils.getPackageOf(element).getQualifiedName().toString());
		return gc;
	}

	private void error(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}
	private void warn(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
	}
}
