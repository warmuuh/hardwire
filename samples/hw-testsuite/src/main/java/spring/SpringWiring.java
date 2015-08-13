package spring;

import java.util.Arrays;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StopWatch;

public class SpringWiring {

	public static void main(String[] args) {
		StopWatch watch = new StopWatch("Spring Wiring");
		watch.start();
		
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DIConfiguration.class);
		
		watch.stop();
		System.out.println(watch.shortSummary());
		
		System.out.println(Arrays.deepToString(context.getBeanDefinitionNames()));
		
		// close the context
		context.close();
	}
}
