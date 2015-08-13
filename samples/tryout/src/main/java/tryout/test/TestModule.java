package tryout.test;

import tryout.test2.Test2Module;
import wrm.hardwire.Module;

@Module(
		imports=Test2Module.class
)
public class TestModule extends TestModuleBase {

	@Override
	protected DynamicInstance createDynamicInstance() {
		return new DynamicInstanceImpl();
	}

}
