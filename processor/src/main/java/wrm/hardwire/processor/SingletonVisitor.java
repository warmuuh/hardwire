package wrm.hardwire.processor;

import javax.annotation.PostConstruct;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.util.Elements;

import lombok.RequiredArgsConstructor;
import wrm.hardwire.processor.model.GenClass;

@RequiredArgsConstructor
public class SingletonVisitor {

	private final Elements elementUtils;

	
	
	public GenClass readGenClass(Element element) {
		GenClass gc = new GenClass(element);
		gc.setName(element.getSimpleName().toString());
		gc.setPackageName(elementUtils.getPackageOf(element).getQualifiedName().toString());
		analizePostConstructMethods(gc);
		return gc;
	}
	

	private void analizePostConstructMethods(GenClass gc) {
		Element element = gc.getElement();
		for (Element methodElement : element.getEnclosedElements()) {
			if (methodElement.getKind() != ElementKind.METHOD) continue;
			if (methodElement.getAnnotation(PostConstruct.class) == null) continue;
			gc.setPostConstructMethod(methodElement.getSimpleName().toString());
		}
	}
}
