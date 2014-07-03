package org.cdr;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Helps you to enforce your design pattern rules by throwing errors if some packages's dependencies are disallowed. 
 */
@Target(value=ElementType.PACKAGE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface CheckDependencies {
	/**
	 * List of denied imports using syntax of {@link java.util.regex.Pattern} 
	 */
	String[] deny() default "";

	/**
	 * List of allowed imports using syntax of {@link java.util.regex.Pattern} even if any {@link #deny()} pattern matched it. 
	 */
	String[] allow() default "";
}
