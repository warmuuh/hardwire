package tryout.test;

import tryout.test2.Container;

public class Application {

	public static void main(String[] args) {
		
		
		tryout.test2.Container c2 = new Container() {
		};
		
		tryout.test.Container container = new tryout.test.Container(){

			@Override
			protected DynamicInstance createDynamicInstance() {
				return new DynamicInstanceImpl();
			}
			
		};
		container.start(c2);
		
		container.getTestB().doSomething();
	}
}
