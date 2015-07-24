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


@SupportedAnnotationTypes({"javax.inject.Singleton", "wrm.hardwire.Module"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AnnotationProcessor extends AbstractProcessor {

	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;

	List<GenClass> classes = new LinkedList<>();
	
	List<GenModule> roots = new LinkedList<>();
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		typeUtils = processingEnv.getTypeUtils();
		elementUtils = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
		warn(null, "initialization");
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {

		
		extractRoots(env);
		
		extractClasses(env);
		analizeFields();
		analizePostConstructMethods();
		
		sortClassesToRoots();

		try {
			if (elements.size() == 0 || env.processingOver()){
				for (GenModule genModule : roots) {
					try{
						writeFactory(genModule);
					} catch (FilerException e){
						//
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	private void sortClassesToRoots() {
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
	
	
	private void extractRoots(RoundEnvironment env) {
		Element actionElement = processingEnv.getElementUtils().getTypeElement(
				Module.class.getName() );
		TypeMirror moduleType = actionElement.asType();
		
		for (Element element : env.getElementsAnnotatedWith(Module.class)) {
			
			
			String qualifiedName = ((QualifiedNameable)element).getQualifiedName().toString();
			GenModule genModule = new GenModule(qualifiedName);
			List<? extends AnnotationMirror> allAnnotationMirrors = elementUtils.getAllAnnotationMirrors(element);
			
			int i = 0; 
			for (AnnotationMirror annotationMirror : allAnnotationMirrors) {
				if (!annotationMirror.getAnnotationType().toString().equals(moduleType.toString()))
					continue;
				for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
					if (!entry.getKey().toString().contains("imports()")) continue;
					List values = (List) entry.getValue().getValue();
					for(Object imp : values){
						GenModuleRef moduleRef = new GenModuleRef();
						String name= imp.toString().substring(1, imp.toString().length() - 1);
						moduleRef.setPackageName(name);
						moduleRef.setClassName("Container");
						moduleRef.setName("ref" + i);
						genModule.getReferences().add(moduleRef);
						i++;
					}
				}
					
			}
			if (genModule.getReferences().size() > 0)
				genModule.getReferences().get(genModule.getReferences().size() -1).setLast(true);
			roots.add(genModule);
		}
		
		
	}

	private void analizeFields() {
		List<GenClass> abstrClasses = new LinkedList<>();
		for(GenClass gc : classes){
			gc.fields.clear(); //for now, just reset fields
			Element element = gc.getElement();
			try{
				for (Element fieldEle : element.getEnclosedElements()) {
					if (fieldEle.getKind() != ElementKind.FIELD)
						continue;
					
					if (fieldEle.getAnnotation(Inject.class) == null)
						continue;
					
					String qualifiedType = fieldEle.asType().toString();
					GenClass fGenClass = null;
					for(GenClass ftype : classes){
						if (ftype.getElement().asType().toString().equals(qualifiedType)){
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

	private void extractClasses(RoundEnvironment env) {
		for (Element element : env.getElementsAnnotatedWith(Singleton.class)) {
			if (element.getKind() != ElementKind.CLASS) {
				error(element, "Only classes are supported for  @Singleton");
				continue;
			}
			GenClass genClass = readGenClass(element);
			classes.add(genClass);
		}
	}
	
	public void writeFactory(GenModule module) throws Exception {
		  Handlebars handlebars = new Handlebars();
		  
		  Template template = handlebars.compile("factoryTemplate");
		  
		  JavaFileObject fileObject = filer.createSourceFile(module.getPackageName() + ".Container");
		  OutputStream outputStream = fileObject.openOutputStream();
		  try(Writer writer = new PrintWriter(outputStream)){
			  template.apply(module, writer);
			  writer.flush();  
		  };
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
