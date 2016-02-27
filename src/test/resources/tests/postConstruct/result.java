package simple;

import javax.annotation.Generated;


@Generated("wrm.hardwire.processor.AnnotationProcessor")
public  class TestModuleBase {
	private simple.Test _Test;	
	protected simple.Test createTest(){
		return new simple.Test();
	}
	protected void wireTest(simple.Test vTest){
	}
	public simple.Test getTest(){
		if (_Test == null) {
			_Test = createTest();
			wireTest(_Test);
		}
		return _Test;
	}
	
	
	
    public void reference()
    {
    }
	
	public void start()	{
		
        
		onStart();
		getTest();
		
		onPostConstruct();
		getTest().init();
	
		onInitialized();
	}
	
	protected void onStart(){};
	protected void onPostConstruct(){};
	protected void onInitialized(){};
}
