package simple;

import javax.annotation.Generated;


@Generated("wrm.hardwire.processor.AnnotationProcessor")
public abstract class TestModuleBase {
	private simple.Test _Test;	
	protected simple.Test createTest(){
		return new simple.Test();
	}
	protected void wireTest(simple.Test vTest){
		vTest.setTestImpl(getTestInterface());
	}
	public simple.Test getTest(){
		if (_Test == null) {
			_Test = createTest();
			wireTest(_Test);
		}
		return _Test;
	}
	private simple.TestInterface _TestInterface;	
	protected abstract simple.TestInterface createTestInterface();
	protected void wireTestInterface(simple.TestInterface vTestInterface){
	}
	public simple.TestInterface getTestInterface(){
		if (_TestInterface == null) {
			_TestInterface = createTestInterface();
			wireTestInterface(_TestInterface);
		}
		return _TestInterface;
	}
	
	
	
    public void reference()
    {
    }
	
	public void start()	{
		
        
		onStart();
		getTest();
		getTestInterface();
		
		onPostConstruct();
	
		onInitialized();
	}
	
	protected void onStart(){};
	protected void onPostConstruct(){};
	protected void onInitialized(){};
}