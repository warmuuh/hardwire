package wrm.hardwire.processor;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import wrm.hardwire.processor.model.GenModule;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class ModuleBaseWriter {

	final Filer filer;
	
	
	public ModuleBaseWriter(Filer filer) {
		super();
		this.filer = filer;
	}


	public void writeFactories(List<GenModule> module) {
		for (GenModule genModule : module) {
			try{
				writeFactory(genModule);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	private void writeFactory(GenModule module) throws Exception {
		  Handlebars handlebars = new Handlebars();
		  
		  Template template = handlebars.compile("factoryTemplate");
		  
		  JavaFileObject fileObject = filer.createSourceFile(module.getPackageName() + "." + module.getClassName());
		  OutputStream outputStream = fileObject.openOutputStream();
		  try(Writer writer = new PrintWriter(outputStream)){
			  template.apply(module, writer);
			  writer.flush();  
		  };
	}
	
	public Filer getFiler() {
		return filer;
	}
	
}
