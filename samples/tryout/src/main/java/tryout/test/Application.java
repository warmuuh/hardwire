package tryout.test;

import tryout.test2.Test2Module;

public class Application {

	public static void main(String[] args) {
		Test2Module t2 = new Test2Module();
		
		TestModule t1 = new TestModule();
		t1.reference(t2);
		t2.reference(t1);
		t1.start();
		t2.start();
		
		t1.getTestB().doSomething();
		
	}
}
