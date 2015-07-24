package tryout.test;

import tryout.test2.Test2Module;

public class Application {

	public static void main(String[] args) {
		Test2Module t2 = new Test2Module();
		t2.start();
		
		TestModule t1 = new TestModule();
		t1.start(t2);
		
		t1.getTestB().doSomething();
	}
}
