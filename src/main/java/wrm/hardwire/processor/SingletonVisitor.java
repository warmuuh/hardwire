package wrm.hardwire.processor;

import javax.annotation.PostConstruct;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import wrm.hardwire.processor.model.GenClass;
import wrm.hardwire.processor.model.GenMethodRef;
import wrm.hardwire.processor.model.GenModelRoot;

public class SingletonVisitor {

	private final Elements elementUtils;
	private final GenModelRoot root;
	private final Types typeUtils;
	private final Messager messager;
	private FieldVisitor fieldVisitor;
	private ConstructorVisitor constructorVisitor;
	
	public SingletonVisitor(ProcessingEnvironment processingEnv, GenModelRoot root) {
		super();
		this.root = root;
		this.elementUtils = processingEnv.getElementUtils();
		this.typeUtils = processingEnv.getTypeUtils();
		this.messager = processingEnv.getMessager();
		fieldVisitor = new FieldVisitor(root, processingEnv);
		constructorVisitor = new ConstructorVisitor(root, processingEnv);
	}
	
	
	public void visitClass(Element element) {
		String simpleName = element.getSimpleName().toString();
		String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
		
		GenClass gc = root.getClassByName(simpleName, packageName);
		if (gc != null){
			gc.setElement(element);
			gc.setAbstr(false);
		} else {
			gc = createNewClass(element, simpleName, packageName);
			root.getClasses().add(gc);
		}
		analizePostConstructMethods(gc);
		fieldVisitor.analizeFields(gc);
		constructorVisitor.analizeConstructor(gc);
	}


	private GenClass createNewClass(Element element, String simpleName,
			String packageName) {
		GenClass gc = new GenClass(element);
		gc.setName(simpleName);
		gc.setPackageName(packageName);
		return gc;
	}
	

	private void analizePostConstructMethods(GenClass gc) {
		Element element = gc.getElement();
		for (Element methodElement : element.getEnclosedElements()) {
			if (methodElement.getKind() != ElementKind.METHOD) continue;
			if (!hasPostConstructAnnotation(methodElement)) continue;
			String methodName = methodElement.getSimpleName().toString();
			gc.addPostConstructMethod(new GenMethodRef(methodName, gc));
		}
	}


	private boolean hasPostConstructAnnotation(Element methodElement) {
		//we cannot use PostConstruct directly, as it got removed in jdk9
		for (AnnotationMirror annMirror : methodElement.getAnnotationMirrors()) {
			if (annMirror.getAnnotationType().toString().equals("javax.annotation.PostConstruct"))
				return true;
		}
		return false;
	}


	
}
