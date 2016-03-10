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

import java.io.File;

import de.clusteval.api.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.api.FormatVersion;

/**
 * @author Christian Wiwie
 * 
 */
@FormatVersion(version = 1)
public class MatrixDataSetFormat extends AbsoluteDataSetFormat {

	/**
	 * @param repo
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @param version
	 * @throws RegisterException
	 */
	public MatrixDataSetFormat(final Repository repo, final boolean register,
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
	public MatrixDataSetFormat(final MatrixDataSetFormat other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.dataset.format.DataSetFormat#getAlias()
	 */
	@Override
	public String getAlias() {
		return "Data Matrix";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.data.dataset.format.DataSetFormat#getDataSetFormatParser()
	 */
	@Override
	protected DataSetFormatParser getDataSetFormatParser() {
		return new MatrixDataSetFormatParser();
	}
}
