package generator;

import java.io.File;
import java.util.List;

import org.springframework.util.FileSystemUtils;

public class Generator {

	public static void main(String[] args) throws Exception {
		ClassGenerator generator = new ClassGenerator();
		ClassWriter writer = new ClassWriter();
		
		FileSystemUtils.deleteRecursively(new File("src/main/gen/root"));
		List<GenClass> hierarchy = generator.generateClassHierarchy(100, 30, 0.5);
		writer.writeClasses(hierarchy);
		writer.writeFactory(hierarchy);
		
		
	}
}
