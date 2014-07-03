package org.cdr;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("org.cdr.SerializableClasses")
public class SerializableClassesProcessor extends AbstractProcessor {

	Types typeUtils;
	Elements elementUtils;
	Messager messager;
	TypeMirror serializable;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		// Utils
		elementUtils = processingEnv.getElementUtils();
		typeUtils = processingEnv.getTypeUtils();
		messager = processingEnv.getMessager();
		// The Serializable interface - used for comparison
		serializable = elementUtils.getTypeElement(Serializable.class.getCanonicalName()).asType();

		messager.printMessage(Kind.NOTE, "SerializableClassesProcessor initialized.");
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			// We're not interested in the postprocessing round.
			return false;
		}

		Set<? extends Element> rootElements = roundEnv.getRootElements();
		for (Element element : rootElements) {

			// We're only interested in packages
			if (element.getKind() != ElementKind.PACKAGE) {
				continue;
			}

			// Get some infos on the annotated package
			PackageElement thePackage = elementUtils.getPackageOf(element);

			// Test each class in the package for "serializability"
			List<? extends Element> classes = thePackage.getEnclosedElements();
			for (Element theClass : classes) {
				// We're not interested in interfaces
				if (theClass.getKind() == ElementKind.INTERFACE) {
					continue;
				}

				// Check if the class is actually Serializable
				boolean isSerializable = typeUtils.isAssignable(theClass.asType(), serializable);
				if (!isSerializable) {
					messager.printMessage(
							Kind.ERROR, 
							thePackage.getQualifiedName() + "." + theClass.getSimpleName() 
							+ " must implements Serializable since it's required in this package by SerializableClasses annotation",
							theClass);
				}
			}
		}

		// Prevent other processors from processing this annotation
		return true;
	}

}
