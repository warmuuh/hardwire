package wrm.hardwire;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class HardwireTestRunner extends Runner {
	
	
	private HardwireTest instance;

	public interface HardwireTest {
		public void run(File[] files) throws Exception;
	}
	
	public HardwireTestRunner(Class klass) throws InitializationError {
		if (!(HardwireTest.class.isAssignableFrom(klass)))
			throw new InitializationError("Class needs to implement HardwireTest class");
		try {
			instance = (HardwireTest) klass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InitializationError("Cannot create instance of class " + klass.getSimpleName());
		}
	}
	
	@Override
	public void run(RunNotifier notifier) {
		try {
			File testsDir = new File(getClass().getClassLoader().getResource("tests").toURI());
			File[] testDirectories = testsDir.listFiles();
			for (File file : testDirectories) {
				if (file.isDirectory() && !file.getName().startsWith("X")){
					Description desc = Description.createTestDescription(instance.getClass(), file.getName());
					notifier.fireTestRunStarted(desc);
					try {
						instance.run(file.listFiles());
						notifier.fireTestFinished(desc);
					} catch (Exception e) {
						notifier.fireTestFailure(new Failure(desc, e));
					}
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription(instance.getClass());
	}
	
	
}
