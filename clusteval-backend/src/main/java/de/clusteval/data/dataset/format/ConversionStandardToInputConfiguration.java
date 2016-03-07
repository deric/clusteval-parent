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
package de.clusteval.data.dataset.format;

/**
 * @author Christian Wiwie
 * 
 */
public class ConversionStandardToInputConfiguration
		extends
			ConversionConfiguration {

	/**
	 * 
	 */
	public ConversionStandardToInputConfiguration() {
		super();
	}

	/**
	 * The copy constructof of this class.
	 * 
	 * @param other
	 *            The object to clone.
	 */
	@SuppressWarnings("unused")
	public ConversionStandardToInputConfiguration(
			final ConversionStandardToInputConfiguration other) {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ConversionStandardToInputConfiguration clone() {
		return new ConversionStandardToInputConfiguration(this);
	}

}
