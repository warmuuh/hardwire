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
	@Override
	public String toString() {
		return "GenField [fieldName=" + fieldName + ", type=" + type + ", moduleRef=" + moduleRef + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((moduleRef == null) ? 0 : moduleRef.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		GenField other = (GenField) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (moduleRef == null) {
			if (other.moduleRef != null)
				return false;
		} else if (!moduleRef.equals(other.moduleRef))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
		
	
}
