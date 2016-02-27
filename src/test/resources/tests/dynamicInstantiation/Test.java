package simple;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Test {

	@Inject
	TestInterface testImpl;
	
	
	public void setTestImpl(TestInterface testImpl){
		this.testImpl = testImpl;
	}
}
