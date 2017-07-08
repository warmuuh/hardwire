package simple2;

import javax.inject.Inject;
import javax.inject.Singleton;
import simple.Test1;

@Singleton
public class Test2 {
	@Inject
	Test1 test1;
	
	@Inject
	public Test2(Test1 test1) {
	}
	
	public void setTest1(Test1 test1){
		this.test1 = test1;
	}
	
}
