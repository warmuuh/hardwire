package simple;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TestB {

	TestA testAInstance;
	
	@Inject	
	public TestB(TestA testA) {
		this.testAInstance = testA;
	}
	
}
