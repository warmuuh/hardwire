package nativeWiring;

import org.springframework.util.StopWatch;

import root.ContainerFactory;

public class NativeWiringContext {

	public static void main(String[] args) {
		StopWatch watch = new StopWatch("native");
		watch.start();
		
		new ContainerFactory().start();
		
		watch.stop();
		System.out.println(watch.shortSummary());
	}
}
