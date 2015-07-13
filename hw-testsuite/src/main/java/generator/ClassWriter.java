package generator;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class ClassWriter {

	public void writeClasses(List<GenClass> classes) throws Exception {
		  MustacheFactory mf = new DefaultMustacheFactory();
		  Mustache mustache = mf.compile("classTemplate.mustache");

		  for(GenClass c : classes){
			  Path targetDir = Paths.get("src/main/gen", c.getPackageName().split("\\."));
			  targetDir.toFile().mkdirs();
			  File targetFile = Paths.get(targetDir.toString(), c.getName() + ".java").toFile();
			  try(FileWriter writer = new FileWriter(targetFile)){
				  mustache.execute(writer, c);
				  writer.flush();  
			  };
		  }
	}


	public void writeFactory(List<GenClass> classes) throws Exception {
		  MustacheFactory mf = new DefaultMustacheFactory();
		  Mustache mustache = mf.compile("factoryTemplate.mustache");
		  
		  Map<String, Object> model = new HashMap<>();
		  model.put("classes", classes);
		  try(FileWriter writer = new FileWriter("src/main/gen/root/ContainerFactory.java")){
			  
			  mustache.execute(writer, model);
			  writer.flush();  
		  };
	}
}
