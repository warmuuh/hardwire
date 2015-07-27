package wrm.hardwire.processor;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import wrm.hardwire.processor.model.GenClass;
import wrm.hardwire.processor.model.GenField;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class FieldVisitor {

	private List<GenClass> classes;
	private Types typeUtils;
	private Elements elementUtils;
	private Messager messager;
	
	public void analizeFields() {
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
						GenClass fieldClass = createAbstractClass(fieldEle);
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

	private GenClass createAbstractClass(Element fieldEle) {
		Element fieldType = typeUtils.asElement(fieldEle.asType());
		PackageElement packageOf = elementUtils.getPackageOf(fieldType);
		GenClass fieldClass = new GenClass(fieldType);
		fieldClass.setName(fieldType.getSimpleName().toString());
		fieldClass.setPackageName(packageOf.getQualifiedName().toString());
		return fieldClass;
	}
}
