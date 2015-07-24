package wrm.hardwire.processor;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class GenModule {

	final String packageName;
	List<GenClass> classes = new LinkedList<GenClass>();

	List<GenModuleRef> references = new LinkedList<GenModuleRef>();

	
	public boolean isAbstract(){
		for (GenClass genClass : classes) {
			if (genClass.isAbstr())
				return true;
		}
		return false;
	}
}
