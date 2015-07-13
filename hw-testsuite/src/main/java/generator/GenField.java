package generator;

import java.util.LinkedList;

import java.util.List;

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
