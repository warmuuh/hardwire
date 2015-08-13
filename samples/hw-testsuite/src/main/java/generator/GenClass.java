package generator;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class GenClass {
	
	final int id;
	String name;
	String packageName;
	List<GenField> fields = new LinkedList<GenField>();

}
