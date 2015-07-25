package root;

import org.springframework.util.StopWatch;

import wrm.hardwire.Module;

@Module
public class HardWireModule extends HardWireModuleBase {

	
	public static void main(String[] args) {
		StopWatch watch = new StopWatch("hardwire");
		watch.start();
		
		new HardWireModule().start();
		
		watch.stop();
		System.out.println(watch.shortSummary());
	}
}
