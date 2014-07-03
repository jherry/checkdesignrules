package org.cdr;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes("org.cdr.CheckDependencies")
public class CheckDependenciesProcessor extends AbstractProcessor {

	Elements elementUtils;
	Messager messager;
	DependenciesScanner scanner;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		// Utils
		elementUtils = processingEnv.getElementUtils();
		messager = processingEnv.getMessager();
		scanner = new DependenciesScanner();

		messager.printMessage(Kind.NOTE, "CheckDependenciesProcessor initialized.");
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

			List<? extends Element> classes = thePackage.getEnclosedElements();
			for (Element theClass : classes) {

				scanner.clear();
				scanner.scan(theClass, null);

				CheckDependencies rules = thePackage.getAnnotation(CheckDependencies.class);
				if (rules != null) {
					Set<String> importedTypes = scanner.getImportedTypes();
					for (String importedType : importedTypes) {
						boolean denied = false, allowed = false;
						String denyingPattern = null;
						for (String denyPattern : rules.deny()) {
							Pattern deny = Pattern.compile(denyPattern);
							Matcher denyMatcher = deny.matcher(importedType);
							denied = denyMatcher.matches();
							if (denied) {
								// One pattern is matching so import is denied
								denyingPattern = denyPattern;
								break;
							}
						}
						if (denied) {
							// Only check allowed patterns if import has been denied
							for (String allowPattern : rules.allow()) {
								Pattern allow = Pattern.compile(allowPattern);
								Matcher allowMatcher = allow.matcher(importedType);
								allowed = allowMatcher.matches();
								if (allowed) {
									// One pattern is matching so import is allowed
									break;
								}
							}
						}
						if (denied && !allowed) {
							messager.printMessage(
									Kind.ERROR, 
									theClass.getSimpleName() 
									+ " must not use " + importedType
									+ " since it's denied in package " + thePackage.getQualifiedName()
									+ " by CheckDependencies annotation with '" + denyingPattern + "'",
									theClass);
						}
					}
				}
			}
		}

		// Prevent other processors from processing this annotation
		return true;
	}
}
