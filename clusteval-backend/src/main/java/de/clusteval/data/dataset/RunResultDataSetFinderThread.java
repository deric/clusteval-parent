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
package de.clusteval.data.dataset;

import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.DataSetFormatFinderThread;
import de.clusteval.data.dataset.type.DataSetType;
import de.clusteval.data.dataset.type.DataSetTypeFinderThread;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.SupervisorThread;

/**
 * @author Christian Wiwie
 * 
 */
public class RunResultDataSetFinderThread extends DataSetFinderThread {

	/**
	 * @param supervisorThread
	 * @param framework
	 * @param checkOnce
	 * 
	 */
	public RunResultDataSetFinderThread(
			final SupervisorThread supervisorThread,
			final Repository framework, final boolean checkOnce) {
		super(supervisorThread, framework, 30000, checkOnce);
	}

	/**
	 * @param supervisorThread
	 * @param framework
	 * @param sleepTime
	 * @param checkOnce
	 * 
	 */
	public RunResultDataSetFinderThread(
			final SupervisorThread supervisorThread,
			final Repository framework, final long sleepTime,
			final boolean checkOnce) {
		super(supervisorThread, framework, sleepTime, checkOnce);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
	 */
	@Override
	protected RunResultDataSetFinder getFinder() throws RegisterException {
		return new RunResultDataSetFinder(repository);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#afterFind()
	 */
	@Override
	protected void afterFind() {
		this.repository.setInitialized(DataSet.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
	 */
	@Override
	protected void beforeFind() {

		if (!this.repository.isInitialized(DataSetFormat.class))
			this.supervisorThread.getThread(DataSetFormatFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(DataSetType.class))
			this.supervisorThread.getThread(DataSetTypeFinderThread.class)
					.waitFor();

		this.log.debug("Checking for Datasets...");
	}
}
