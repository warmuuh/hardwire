package wrm.hardwire.processor.model;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;

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
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() { 
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getPostConstructMethod() {
		return postConstructMethod;
	}
	public void setPostConstructMethod(String postConstructMethod) {
		this.postConstructMethod = postConstructMethod;
	}
	public boolean isAbstr() {
		return abstr;
	}
	public void setAbstr(boolean abstr) {
		this.abstr = abstr;
	}
	public List<GenField> getFields() {
		return fields;
	}
	public void setFields(List<GenField> fields) {
		this.fields = fields;
	}

	
	
	
}
