package org.codehaus.groovy.groosh;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Aliases {
	String[] value();
}
