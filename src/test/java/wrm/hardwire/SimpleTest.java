package wrm.hardwire;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.google.testing.compile.JavaFileObjects;

import wrm.hardwire.HardwireTestRunner.HardwireTest;
import wrm.hardwire.processor.AnnotationProcessor;

@RunWith(HardwireTestRunner.class)
public class SimpleTest implements HardwireTest{
	
	@Override
	public void run(File[] files) throws Exception {
		
		List<JavaFileObject> sources = createSourcesList(files);
		JavaFileObject result = getResult(files);
		
		assert_().about(javaSources())
		.that(sources)
		.withCompilerOptions("-AjdkOverwrite=8", "-AdisableIterativeCheck=true")
		.processedWith(new AnnotationProcessor())
		.compilesWithoutError()
		.and()
		.generatesSources(result, getAdditionalResult(files));
	}

	private JavaFileObject getResult(File[] files) throws Exception {
		for (File file : files) {
			if (file.getName().equals("result.java"))
				return JavaFileObjects.forResource(file.toURL());
		}
		return null;
	}

	private JavaFileObject[] getAdditionalResult(File[] files) throws Exception {
		List<JavaFileObject> addResults = new LinkedList<>();
		for (File file : files) {
			if (file.getName().startsWith("result") && !file.getName().equals("result.java"))
				addResults.add(JavaFileObjects.forResource(file.toURL()));
		}
		return addResults.toArray(new JavaFileObject[]{});
	}
	
	private List<JavaFileObject> createSourcesList(File[] files) throws Exception{
		List<JavaFileObject> sources = new LinkedList<>();
		for (File file : files) {
			if (!file.getName().startsWith("result") && file.getName().endsWith(".java"))
				sources.add(JavaFileObjects.forResource(file.toURL()));
		}
		return sources;
	}
	
	
}
