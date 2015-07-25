package wrm.hardwire.processor.model;

import lombok.Data;

@Data
public class GenModuleRef {

	String name;
	String packageName;
	String className;
	boolean last;
}
