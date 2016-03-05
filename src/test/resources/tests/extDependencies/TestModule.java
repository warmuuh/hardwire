package simple;

import wrm.hardwire.Module;


@Module()
public class TestModule {

	/**
	 * for testing, i have to add this method by hand instead of extending TestModuleBase 
	 * because compile-tools does not support multi-pass-compilation, so i cannot depend on
	 * generated results
	 * @return
	 */
	public Test1 getTest1(){
		return null;
	}
}
