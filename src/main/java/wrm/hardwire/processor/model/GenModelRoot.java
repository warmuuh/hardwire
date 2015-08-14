package wrm.hardwire.processor.model;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.util.Types;

public class GenModelRoot {

	List<GenClass> classes = new LinkedList<>();
	List<GenModule> roots = new LinkedList<>();
	private Types typeUtils;
	
	public GenClass getAssignableClass(Element fieldEle) {
		for(GenClass ftype : classes){
			boolean isAssignable = typeUtils.isAssignable(ftype.getElement().asType(), fieldEle.asType());
			if (isAssignable){
				return ftype;
			}
		}
		return null;
	}

	public GenModelRoot(Types typeUtils) {
		this.typeUtils = typeUtils;
	}
	
	public GenClass getClassByName(String simpleName, String packageName){
		for(GenClass gc : classes){
			if (simpleName.equals(gc.getName()) && packageName.equals(gc.getPackageName()))
				return gc;
		}
		return null;
	}
	
	public void postProcess(){
		sortClassesToModules();
		setModuleReferencesOfFields();
	}
	private void sortClassesToModules() {
		for (GenClass genClass : classes) {
			for (GenModule module : roots) {
				boolean isPackage = genClass.getPackageName().equals(module.getPackageName())
						|| genClass.getPackageName().startsWith(module.getPackageName()+".");
				if (isPackage && !module.getClasses().contains(genClass)){
					module.getClasses().add(genClass);
				}
			}
		}
	}

	private void setModuleReferencesOfFields() {
		for (GenModule module : roots) {
			for (GenClass genClass : module.getClasses()) {
				for (GenField genfield : genClass.getFields()) {
					GenModule rootForClass = getRootForClass(genfield.getType());
					if (rootForClass != null && !rootForClass.equals(module)){
						for (GenModuleRef moduleRef : module.getReferences()) {
							boolean inPackage = rootForClass.getPackageName().equals(moduleRef.getPackageName()) ;
							if(inPackage)
								genfield.setModuleRef(moduleRef.getName());
						}
					}
				}
			}
		}
	}

	public GenModule getRootForClass(GenClass gc){
		for (GenModule module : roots) {
			for (GenClass genClass : module.getClasses()) {
				if (genClass.equals(gc))
					return module;
			}
		}
		return null;
	}

	public List<GenClass> getClasses() {
		return classes;
	}

	public void setClasses(List<GenClass> classes) {
		this.classes = classes;
	}

	public List<GenModule> getRoots() {
		return roots;
	}

	public void setRoots(List<GenModule> roots) {
		this.roots = roots;
	}
	
	
}
