/**
 * 
 */
package de.clusteval.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Christian Wiwie
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RLibraryRequirement {

	public String[] requiredRLibraries();
}
