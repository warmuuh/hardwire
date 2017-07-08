package wrm.hardwire.processor.model;


public class GenField extends GenParam {
	
	final String fieldName;
	public GenField(String fieldName, GenClass type) {
		super(type);
		this.fieldName = fieldName;
	}

	public String getSetterName(){
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String toString() {
		return "GenField [fieldName=" + fieldName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenField other = (GenField) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}

	
		
	
}
