package tryout.test;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class TestA {

 
	int x;
  
	
	public void doSomething(){
		System.out.println("Test a is doing something");
	}
	
	@PostConstruct
	public void init(){
		System.out.println("A initing");
	}
}
