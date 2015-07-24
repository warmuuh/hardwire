package tryout.test;

import wrm.hardwire.Module;

@Module(
		imports="tryout.test2.Test2Module"
)
public class TestModule extends TestModuleBase {

	@Override
	protected DynamicInstance createDynamicInstance() {
		return new DynamicInstanceImpl();
	}

}
