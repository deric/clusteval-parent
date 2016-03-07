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

import java.io.File;

import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;

/**
 * @author Christian Wiwie
 * 
 */
public class RunResultDataSetConfig extends DataSetConfig {

	/**
	 * @param repository
	 * @param changeDate
	 * @param absPath
	 * @param ds
	 * @param configInputToStandard
	 * @param configStandardToInput
	 * @throws RegisterException
	 */
	public RunResultDataSetConfig(Repository repository, long changeDate,
			File absPath, DataSet ds,
			ConversionInputToStandardConfiguration configInputToStandard,
			ConversionStandardToInputConfiguration configStandardToInput)
			throws RegisterException {
		super(repository, changeDate, absPath, ds, configInputToStandard,
				configStandardToInput);
	}

	/**
	 * @param datasetConfig
	 * @throws RegisterException
	 */
	public RunResultDataSetConfig(DataSetConfig datasetConfig)
			throws RegisterException {
		super(datasetConfig);
	}

}
