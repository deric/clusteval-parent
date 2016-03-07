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
package de.clusteval.data.dataset.format;

import java.io.File;
import java.io.IOException;

import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class RelativeDataSetFormat extends DataSetFormat {

	/**
	 * Instantiates a new relative data set format.
	 * 
	 * @param repo
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * 
	 * @param version
	 * @throws RegisterException
	 * 
	 */
	public RelativeDataSetFormat(final Repository repo, final boolean register,
			final long changeDate, final File absPath, final int version)
			throws RegisterException {
		super(repo, register, changeDate, absPath, version);
	}

	/**
	 * The copy constructor for this format.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public RelativeDataSetFormat(final RelativeDataSetFormat other)
			throws RegisterException {
		super(other);
	}

	@Override
	public final SimilarityMatrix parse(final DataSet dataSet,
			NUMBER_PRECISION precision) throws IllegalArgumentException,
			IOException, InvalidDataSetFormatVersionException {
		return (SimilarityMatrix) super.parse(dataSet, precision);
	}
}
