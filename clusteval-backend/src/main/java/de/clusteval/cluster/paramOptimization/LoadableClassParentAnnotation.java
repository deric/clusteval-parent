/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.cluster.paramOptimization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Christian Wiwie
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadableClassParentAnnotation {

	/**
	 * 
	 * @return The name of the parent class required for an annotated class to
	 *         be loaded.
	 */
	String parent();
}
