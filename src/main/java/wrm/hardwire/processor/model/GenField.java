package wrm.hardwire.processor.model;


public class GenField {
	
	final String fieldName;
	final GenClass type;
	String moduleRef;
	public String getSetterName(){
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	public GenField(String fieldName, GenClass type) {
		super();
		this.fieldName = fieldName;
		this.type = type;
	}
	public String getModuleRef() {
		return moduleRef;
	}
	public void setModuleRef(String moduleRef) {
		this.moduleRef = moduleRef;
	}
	public String getFieldName() {
		return fieldName;
	}
	public GenClass getType() {
		return type;
	}
		
	
}
