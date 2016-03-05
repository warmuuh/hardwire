package simple2;

import javax.annotation.Generated;


@Generated("wrm.hardwire.processor.AnnotationProcessor")
public  class TestModule2Base {
	protected simple.TestModule refTestModule;
	private simple2.Test2 _Test2;	
	protected simple2.Test2 createTest2(){
		return new simple2.Test2();
	}
	protected void wireTest2(simple2.Test2 vTest2){
		vTest2.setTest1(refTestModule.getTest1());
	}
	public simple2.Test2 getTest2(){
		if (_Test2 == null) {
			_Test2 = createTest2();
			wireTest2(_Test2);
		}
		return _Test2;
	}
	
	
	
    public void reference(        simple.TestModule refTestModule
)
    {
        this.refTestModule = refTestModule;
    }
	
	public void start()	{
		
        assert this.refTestModule != null : "Reference to simple.TestModule is missing. Did you forget to call reference(...)?";
        
		onStart();
		getTest2();
		
		onPostConstruct();
	
		onInitialized();
	}
	
	protected void onStart(){};
	protected void onPostConstruct(){};
	protected void onInitialized(){};
}