package wrm.hardwire.processor.model;

import java.util.LinkedList;
import java.util.List;


public class GenModule {

	final String className;
	final String packageName;
	List<GenClass> classes = new LinkedList<GenClass>();

	List<GenModuleRef> references = new LinkedList<GenModuleRef>();

	
	public GenModule(String className, String packageName) {
		super();
		this.className = className;
		this.packageName = packageName;
	}


	public boolean isAbstract(){
		for (GenClass genClass : classes) {
			if (genClass.isAbstr())
				return true;
		}
		return false;
	}


	public List<GenClass> getClasses() {
		return classes;
	}


	public void setClasses(List<GenClass> classes) {
		this.classes = classes;
	}


	public List<GenModuleRef> getReferences() {
		return references;
	}


	public void setReferences(List<GenModuleRef> references) {
		this.references = references;
	}


	public String getClassName() {
		return className;
	}


	public String getPackageName() {
		return packageName;
	}


	@Override
	public String toString() {
		return "GenModule [className=" + className + ", packageName=" + packageName + ", classes=" + classes
				+ ", references=" + references + "]";
	}

	public String generatedAnnotation() {
	    return isBeforeJava9() 
	    		? "javax.annotation.Generated" 
	    		: "javax.annotation.processing.Generated";
	}
	
	private static boolean isBeforeJava9() {
	    try {
	      Class.forName("java.lang.Module");
	      return false;
	    } catch (ClassNotFoundException e) {
	      return true;
	    }
	  }
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((classes == null) ? 0 : classes.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((references == null) ? 0 : references.hashCode());
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
		GenModule other = (GenModule) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (classes == null) {
			if (other.classes != null)
				return false;
		} else if (!classes.equals(other.classes))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (references == null) {
			if (other.references != null)
				return false;
		} else if (!references.equals(other.references))
			return false;
		return true;
	}
	
	
}
