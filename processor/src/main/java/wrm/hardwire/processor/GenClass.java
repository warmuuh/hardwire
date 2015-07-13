package wrm.hardwire.processor;

import java.util.LinkedList;

import java.util.List;

import javax.lang.model.element.Element;

import lombok.Data;

@Data
public class GenClass {
	
	final Element element;
	String name;
	String packageName;
	List<GenField> fields = new LinkedList<GenField>();

	
	
}
