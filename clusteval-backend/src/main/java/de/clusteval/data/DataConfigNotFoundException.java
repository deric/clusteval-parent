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
package de.clusteval.data;

import de.clusteval.api.exceptions.DataException;

/**
 * @author Christian Wiwie
 * 
 */
public class DataConfigNotFoundException extends DataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8998517345370407507L;

	/**
	 * @param message
	 */
	public DataConfigNotFoundException(String message) {
		super(message);
	}
}
