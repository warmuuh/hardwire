package simple;

import javax.annotation.Generated;


@Generated("wrm.hardwire.processor.AnnotationProcessor")
public  class TestModuleBase {
	private simple.Test1 _Test1;	
	protected simple.Test1 createTest1(){
		return new simple.Test1();
	}
	protected void wireTest1(simple.Test1 vTest1){
	}
	public simple.Test1 getTest1(){
		if (_Test1 == null) {
			_Test1 = createTest1();
			wireTest1(_Test1);
		}
		return _Test1;
	}
	
	
	
    public void reference()
    {
    }
	
	public void start()	{
		
        
		onStart();
		getTest1();
		
		onPostConstruct();
	
		onInitialized();
	}
	
	protected void onStart(){};
	protected void onPostConstruct(){};
	protected void onInitialized(){};
}

