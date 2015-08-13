package generator;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class GenField {
	
	final String fieldName;
	final GenClass type;

	public String getSetterName(){
		return StringUtils.capitalize(fieldName);
	}
		
}
