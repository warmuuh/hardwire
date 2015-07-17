package wrm.hardwire.processor;

import lombok.Data;

@Data
public class GenModuleRef {

	String name;
	String packageName;
	String className;
	boolean last;
}
