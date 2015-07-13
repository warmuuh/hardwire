package tryout;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TestB {

	@Inject
	TestA testA;
}
