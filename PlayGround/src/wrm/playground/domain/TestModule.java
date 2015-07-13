package wrm.playground.domain;

public interface TestModule {

	
	default TestA createTestA(){
		return new TestA();
	};
	
	default void wireTestA(TestA testA){
	}
	
	default TestA getTestA(){
		TestA testA = createTestA();
		wireTestA(testA);
		return testA;
	}
	
	
	default TestB createTestB(){
		return new TestB();
	};
	
	default void wireTestB(TestB testB){
		testB.setTestA(getTestA());
	}
	
	default TestB getTestB(){
		TestB testB = createTestB();
		wireTestB(testB);
		return testB;
	}
	
	default void start(){
		
	}
	
}
