package simple;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TestB {

	@Inject
	TestA testAInstance;
	
	public void setTestAInstance(TestA testA){
		this.testAInstance = testA;
	}
}
