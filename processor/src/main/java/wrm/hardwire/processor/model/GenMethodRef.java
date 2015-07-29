package wrm.hardwire.processor.model;

public class GenMethodRef {

	String name;
	GenClass type;
	
	
	
	
	public GenMethodRef(String name, GenClass type) {
		super();
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public GenClass getType() {
		return type;
	}
	public void setType(GenClass type) {
		this.type = type;
	}
	
	
}
