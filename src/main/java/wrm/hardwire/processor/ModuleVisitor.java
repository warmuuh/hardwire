package wrm.hardwire.processor;

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import wrm.hardwire.Module;
import wrm.hardwire.processor.model.GenClass;
import wrm.hardwire.processor.model.GenModelRoot;
import wrm.hardwire.processor.model.GenModule;
import wrm.hardwire.processor.model.GenModuleRef;

public class ModuleVisitor {


	private final Elements elementUtils;
	private GenModelRoot root;
	private Messager messager;

	public ModuleVisitor(ProcessingEnvironment processingEnv, GenModelRoot root) {
		this.root = root;
		elementUtils = processingEnv.getElementUtils();
		messager = processingEnv.getMessager();
	}

	public void visitModule(Element element) {
		try{
			PackageElement pkg = elementUtils.getPackageOf(element);
			String moduleName = element.getSimpleName() + "Base";
			GenModule genModule = new GenModule(moduleName, pkg.getQualifiedName().toString());
			List<? extends AnnotationMirror> allAnnotationMirrors = elementUtils.getAllAnnotationMirrors(element);
			
			Element actionElement = elementUtils.getTypeElement(
					Module.class.getName() );
			TypeMirror moduleType = actionElement.asType();
			
			for (AnnotationMirror annotationMirror : allAnnotationMirrors) {
				if (!annotationMirror.getAnnotationType().toString().equals(moduleType.toString()))
					continue;
				for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
					String keyName = entry.getKey().getSimpleName().toString();
					if (keyName.equals("imports")){
						List<AnnotationValue> values = (List<AnnotationValue>) entry.getValue().getValue();
						addModuleReferences(genModule, values);
					}
					if (keyName.equals("external")){
						List<AnnotationValue> values = (List<AnnotationValue>) entry.getValue().getValue();
						addModuleDynamic(genModule, values);
					}
				}
					
			}
			if (genModule.getReferences().size() > 0)
				genModule.getReferences().get(genModule.getReferences().size() -1).setLast(true);
			
			
			root.getRoots().add(genModule);
		} catch (Throwable t) {
			messager.printMessage(Kind.ERROR, t.getMessage(), element);
		}
	}

	private void addModuleReferences(GenModule genModule, List<AnnotationValue> values) {
		for(AnnotationValue imp : values){
			Element classElement = ((javax.lang.model.type.DeclaredType)imp.getValue()).asElement();
			GenModuleRef moduleRef = new GenModuleRef();
			String className = classElement.getSimpleName().toString();
			String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
			moduleRef.setPackageName(packageName);
			moduleRef.setClassName(className);
			moduleRef.setName("ref" + className);
			genModule.getReferences().add(moduleRef);
		}
	}

	private void addModuleDynamic(GenModule genModule, List<AnnotationValue> values) {
		for(AnnotationValue imp : values){
			Element classElement = ((javax.lang.model.type.DeclaredType)imp.getValue()).asElement();
			String className = classElement.getSimpleName().toString();
			String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
			GenClass classRef = new GenClass(null);
			classRef.setAbstr(true);
			classRef.setPackageName(packageName);
			classRef.setName(className);
			genModule.getClasses().add(classRef);
		}
	}
}
