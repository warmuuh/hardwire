package wrm.hardwire.processor;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;


@SupportedAnnotationTypes("javax.inject.Singleton")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class AnnotationProcessor extends AbstractProcessor {

	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;

	List<GenClass> classes = new LinkedList<>();
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		typeUtils = processingEnv.getTypeUtils();
		elementUtils = processingEnv.getElementUtils();
		filer = processingEnv.getFiler();
		messager = processingEnv.getMessager();
	}

	@Override
	public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {

		extractClasses(env);
		analizeFields();
		

		try {
			if (env.processingOver())
				writeFactory(classes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void analizeFields() {
		for(GenClass gc : classes){
			gc.fields.clear(); //for now, just reset fields
			for (VariableElement fieldEle : ElementFilter.fieldsIn(gc.getElement().getEnclosedElements())) {
				if (fieldEle.getAnnotation(Inject.class) == null)
					continue;
				
				String qualifiedType = fieldEle.asType().toString();
				for(GenClass ftype : classes){
					if (ftype.getElement().asType().toString().equals(qualifiedType)){
						GenField genfield = new GenField(fieldEle.getSimpleName().toString(), ftype);
						gc.getFields().add(genfield);
					}
				}
			}			
		}
	}

	private void extractClasses(RoundEnvironment env) {
		for (Element element : env.getElementsAnnotatedWith(Singleton.class)) {
			if (element.getKind() != ElementKind.CLASS) {
				error(element, "Only classes are supported for  @Singleton");
				continue;
			}
			GenClass genClass = readGenClass(element);
			classes.add(genClass);
		}
	}
	
	public void writeFactory(List<GenClass> classes) throws Exception {
		  MustacheFactory mf = new DefaultMustacheFactory();
		  Mustache mustache = mf.compile("factoryTemplate.mustache");
		  
		  Map<String, Object> model = new HashMap<>();
		  model.put("classes", classes);
		  model.put("package", "root");
		  JavaFileObject fileObject = filer.createSourceFile("root.ContainerFactory");
		  OutputStream outputStream = fileObject.openOutputStream();
		  try(Writer writer = new PrintWriter(outputStream)){
			  mustache.execute(writer, model);
			  writer.flush();  
		  };
	}

	private GenClass readGenClass(Element element) {
		GenClass gc = new GenClass(element);
		gc.setName(element.getSimpleName().toString());
		gc.setPackageName(elementUtils.getPackageOf(element).getQualifiedName().toString());
		return gc;
	}

	private void error(Element e, String msg, Object... args) {
		messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
	}
}
