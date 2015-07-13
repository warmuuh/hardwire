package wrm.hardwire.processor;

import lombok.Data;

@Data
public class GenField {
	
	final String fieldName;
	final GenClass type;

	public String getSetterName(){
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}
		
}
