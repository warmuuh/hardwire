package simple;

import javax.annotation.Generated;


@Generated("wrm.hardwire.processor.AnnotationProcessor")
public  class TestModuleBase {
	private simple.TestA _TestA;	
	protected simple.TestA createTestA(){
		return new simple.TestA();
	}
	protected void wireTestA(simple.TestA vTestA){
	}
	public simple.TestA getTestA(){
		if (_TestA == null) {
			_TestA = createTestA();
			wireTestA(_TestA);
		}
		return _TestA;
	}
	private simple.TestB _TestB;	
	protected simple.TestB createTestB(){
		return new simple.TestB(getTestA());
	}
	protected void wireTestB(simple.TestB vTestB){
	}
	public simple.TestB getTestB(){
		if (_TestB == null) {
			_TestB = createTestB();
			wireTestB(_TestB);
		}
		return _TestB;
	}
	
	
	
    public void reference()
    {
    }
	
	public void start()	{
		
        
		onStart();
		getTestA();
		getTestB();
		
		onPostConstruct();
	
		onInitialized();
	}
	
	protected void onStart(){};
	protected void onPostConstruct(){};
	protected void onInitialized(){};
}