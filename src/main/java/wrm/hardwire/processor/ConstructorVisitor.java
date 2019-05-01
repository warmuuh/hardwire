package wrm.hardwire.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementKindVisitor8;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import wrm.hardwire.processor.model.GenClass;
import wrm.hardwire.processor.model.GenModelRoot;
import wrm.hardwire.processor.model.GenParam;

public class ConstructorVisitor extends ElementKindVisitor8<GenClass, GenClass> {

	private GenModelRoot root;
	private Types typeUtils;
	private Elements elementUtils;
	private Messager messager;
	
	
	public ConstructorVisitor(GenModelRoot root, ProcessingEnvironment processingEnv) {
		this.elementUtils = processingEnv.getElementUtils();
		this.typeUtils = processingEnv.getTypeUtils();
		this.messager = processingEnv.getMessager();
		this.root = root;
	}

	public void analizeConstructor(GenClass gc) {
		gc.getConstructorArguments().clear(); //for now, just reset arguments
		Element element = gc.getElement();
		try{
			for(Element field : element.getEnclosedElements())
				field.accept(this, gc);
		} catch (NullPointerException npe){
			messager.printMessage(Kind.WARNING, "NullPointer catched");
		}
	}

	private GenClass createAbstractClass(Element fieldEle) {
		Element fieldType = typeUtils.asElement(fieldEle.asType());
		PackageElement packageOf = elementUtils.getPackageOf(fieldType);
		GenClass fieldClass = new GenClass(fieldType);
		fieldClass.setName(fieldType.getSimpleName().toString());
		fieldClass.setPackageName(packageOf.getQualifiedName().toString());
		fieldClass.setAbstr(true);
		return fieldClass;
	}

	@Override
	public GenClass visitExecutableAsConstructor(ExecutableElement e, GenClass gc) {
//		if (e.getAnnotation(Inject.class) == null)
//			return null;
		for (VariableElement param : e.getParameters()) {
			GenClass fGenClass = root.getAssignableClass(param);
			
			if (fGenClass == null){
				//no matching type found for field, add it as "dynamic" type:
				fGenClass = createAbstractClass(param);
				root.getClasses().add(fGenClass);
			}
			GenParam genParam =  new GenParam(fGenClass);
			gc.getConstructorArguments().add(genParam);
		}

		return DEFAULT_VALUE;
	}
	
}
