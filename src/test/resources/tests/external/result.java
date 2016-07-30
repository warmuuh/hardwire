package simple;

import javax.annotation.Generated;


@Generated("wrm.hardwire.processor.AnnotationProcessor")
public abstract class TestModuleBase {
	private java.util.Properties _Properties;	
	protected abstract java.util.Properties createProperties();
	protected void wireProperties(java.util.Properties vProperties){
	}
	public java.util.Properties getProperties(){
		if (_Properties == null) {
			_Properties = createProperties();
			wireProperties(_Properties);
		}
		return _Properties;
	}
	
	
	
    public void reference()
    {
    }
	
	public void start()	{
		
        
		onStart();
		getProperties();
		
		onPostConstruct();
	
		onInitialized();
	}
	
	protected void onStart(){};
	protected void onPostConstruct(){};
	protected void onInitialized(){};
}