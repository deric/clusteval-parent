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
package de.clusteval.context;

import de.clusteval.utils.ClustEvalException;

/**
 * @author Christian Wiwie
 * 
 */
public class UnknownContextException extends ClustEvalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4796234082667551507L;

	/**
	 * @param message
	 */
	public UnknownContextException(final String message) {
		super(message);
	}
}
