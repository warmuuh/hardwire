package wrm.hardwire.processor.model;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;

import lombok.Data;

@Data
public class GenClass {
	
	Element element;
	String name;
	String packageName;
	String postConstructMethod;
	boolean abstr;
	List<GenField> fields = new LinkedList<GenField>();
	public GenClass(Element element) {
		super();
		this.element = element;
	}

	
	
}
