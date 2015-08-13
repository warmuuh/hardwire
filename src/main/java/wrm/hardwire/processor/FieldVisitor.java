package wrm.hardwire.processor;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementKindVisitor8;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import wrm.hardwire.processor.model.GenClass;
import wrm.hardwire.processor.model.GenField;
import wrm.hardwire.processor.model.GenModelRoot;

public class FieldVisitor extends ElementKindVisitor8<GenField, GenClass> {

	private GenModelRoot root;
	private Types typeUtils;
	private Elements elementUtils;
	private Messager messager;
	
	
	public FieldVisitor(GenModelRoot root, ProcessingEnvironment processingEnv) {
		this.elementUtils = processingEnv.getElementUtils();
		this.typeUtils = processingEnv.getTypeUtils();
		this.messager = processingEnv.getMessager();
		this.root = root;
	}

	public void analizeFields(GenClass gc) {
		gc.getFields().clear(); //for now, just reset fields
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
	public GenField visitVariableAsField(VariableElement e, GenClass gc) {
		if (e.getAnnotation(Inject.class) == null)
			return null;
		
		GenClass fGenClass = root.getAssignableClass(e);
		
		if (fGenClass == null){
			//no matching type found for field, add it as "dynamic" type:
			fGenClass = createAbstractClass(e);
			root.getClasses().add(fGenClass);
		}
		GenField genfield = new GenField(e.getSimpleName().toString(), fGenClass);
		gc.getFields().add(genfield);
		return genfield;
	}
	
}
