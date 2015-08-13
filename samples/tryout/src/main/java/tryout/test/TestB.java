package tryout.test;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Data;
import tryout.test2.TestC;

@Singleton
@Data
public class TestB {

	@Inject
	TestA testA;
	
	@Inject
	DynamicInstance dynInst;
	
	
	@Inject
	TestC testC;
	
	public void doSomething(){
		testA.doSomething();
		System.out.println("Test b is doing something");
		System.out.println("dyninst: " + dynInst);

		System.out.println("testC: " + testC);
	}
	
}

