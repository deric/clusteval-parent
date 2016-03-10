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
package de.clusteval.data.statistics;

import java.io.File;

import de.clusteval.api.repository.RegisterException;
import de.clusteval.framework.repository.Repository;


/**
 * @author Christian Wiwie
 * 
 */
public class MatrixRankRDataStatistic extends DoubleValueDataStatistic {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 * 
	 */
	public MatrixRankRDataStatistic(final Repository repository,
			final boolean register, final long changeDate, final File absPath)
			throws RegisterException {
		super(repository, register, changeDate, absPath, 0.0);
	}

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @param value
	 * @throws RegisterException
	 */
	public MatrixRankRDataStatistic(final Repository repository,
			final boolean register, final long changeDate, final File absPath,
			final double value) throws RegisterException {
		super(repository, register, changeDate, absPath, value);
	}

	/**
	 * The copy constructor for this statistic.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public MatrixRankRDataStatistic(final MatrixRankRDataStatistic other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.Statistic#getAlias()
	 */
	@Override
	public String getAlias() {
		return "Matrix Rank";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.statistics.DataStatistic#requiresGoldStandard()
	 */
	@Override
	public boolean requiresGoldStandard() {
		return false;
	}
}
