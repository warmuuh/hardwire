package wrm.hardwire.processor.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;

import com.github.mustachejava.util.DecoratedCollection;

public class GenClass {
	
	Element element;
	String name;
	String packageName;
	List<GenMethodRef> postConstructMethods = new LinkedList<>();
	
	boolean abstr;
	List<GenField> fields = new LinkedList<GenField>();
	List<GenParam> constructorArguments = new LinkedList<GenParam>();
	
	
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
	
	public List<GenMethodRef> getPostConstructMethods() {
		return postConstructMethods;
	}
	public void addPostConstructMethod(GenMethodRef postConstructMethod) {
		this.postConstructMethods.add(postConstructMethod);
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
	public List<GenParam> getConstructorArguments() {
		return constructorArguments;
	}
	public DecoratedCollection<GenParam> getDecoratedConstructorArguments(){
		return new DecoratedCollection<>(constructorArguments);
	}
	
	public void setConstructorArguments(List<GenParam> constructorArguments) {
		this.constructorArguments = constructorArguments;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (abstr ? 1231 : 1237);
		result = prime * result + ((constructorArguments == null) ? 0 : constructorArguments.hashCode());
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((postConstructMethods == null) ? 0 : postConstructMethods.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenClass other = (GenClass) obj;
		if (abstr != other.abstr)
			return false;
		if (constructorArguments == null) {
			if (other.constructorArguments != null)
				return false;
		} else if (!constructorArguments.equals(other.constructorArguments))
			return false;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (postConstructMethods == null) {
			if (other.postConstructMethods != null)
				return false;
		} else if (!postConstructMethods.equals(other.postConstructMethods))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "GenClass [element=" + element + ", name=" + name + ", packageName=" + packageName
				+ ", postConstructMethods=" + postConstructMethods + ", abstr=" + abstr + ", fields=" + fields
				+ ", constructorArguments=" + constructorArguments + "]";
	}
	
	
	

	
	
	
}
