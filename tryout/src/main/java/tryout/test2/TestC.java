package tryout.test2;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Data;
import tryout.test.TestA;

@Singleton
@Data
public class TestC {

	@Inject
	TestA testA;
	
	
}
