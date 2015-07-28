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
	
	
}
