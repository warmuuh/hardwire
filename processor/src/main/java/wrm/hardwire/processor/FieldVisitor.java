package wrm.hardwire.processor;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.AbstractElementVisitor8;
import javax.lang.model.util.ElementKindVisitor8;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import wrm.hardwire.processor.model.GenClass;
import wrm.hardwire.processor.model.GenField;
import wrm.hardwire.processor.model.GenModelRoot;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

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
		List<GenClass> abstrClasses = new LinkedList<>();
		gc.getFields().clear(); //for now, just reset fields
		Element element = gc.getElement();
		try{
			element.accept(this, gc);
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
		if (e.getAnnotation(Inject.class) != null)
			return null;
		
		GenClass fGenClass = root.getAssignableClass(e);
		
		if (fGenClass == null){
			//no matching type found for field, add it as "dynamic" type:
			GenClass fieldClass = createAbstractClass(e);
			root.getClasses().add(fieldClass);
		}
		GenField genfield = new GenField(e.getSimpleName().toString(), fGenClass);
		gc.getFields().add(genfield);
		return genfield;
	}
	
}
