package org.cdr;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.junit.Assert;
import org.junit.Test;

import com.qrmedia.commons.test.annotation.processing.AbstractAnnotationProcessorTest;

public class SerializableClassesTest extends AbstractAnnotationProcessorTest {

	@Override
	protected Collection<Processor> getProcessors() {
		return Arrays.<Processor> asList(new SerializableClassesProcessor());
	}

	@Test
	public void test() {
		List<Diagnostic<? extends JavaFileObject>> diag = compileTestCase("org/cdr/tests/serializableclasses/sample1/package-info.java");
		Assert.assertEquals(3, diag.size());
		// FIXME: Order may vary depending on OS and JVM, we should handle this.
		Assert.assertEquals(Kind.NOTE, diag.get(0).getKind());
		Assert.assertEquals(Kind.ERROR, diag.get(1).getKind());
		Assert.assertEquals(Kind.ERROR, diag.get(2).getKind());
		Assert.assertEquals("org.cdr.tests.serializableclasses.sample1.AbstractNotSerializable must implements Serializable since it's required in this package by SerializableClasses annotation", diag.get(1).getMessage(null));
		Assert.assertEquals("org.cdr.tests.serializableclasses.sample1.NotSerializable must implements Serializable since it's required in this package by SerializableClasses annotation", diag.get(2).getMessage(null));
	}

}
