package wrm.hardwire.processor.model;


public class GenParam {
	
	final GenClass type;
	String moduleRef;
	public GenParam(GenClass type) {
		super();
		this.type = type;
	}
	public String getModuleRef() {
		return moduleRef;
	}
	public void setModuleRef(String moduleRef) {
		this.moduleRef = moduleRef;
	}
	public GenClass getType() {
		return type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		GenParam other = (GenParam) obj;
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
	@Override
	public String toString() {
		return "GenParam [type=" + type + ", moduleRef=" + moduleRef + "]";
	}
}
