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
package de.clusteval.program.r;

import java.io.File;
import java.util.Map;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.AbsoluteDataSet;
import de.clusteval.data.dataset.DataMatrix;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.framework.RLibraryNotLoadedException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.program.ProgramConfig;
import de.clusteval.utils.RNotAvailableException;

/**
 * This class represents R programs, which are compatible to relative and
 * absolute datasets.
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class AbsoluteAndRelativeDataRProgram extends RProgram {

	/**
	 * @param repository
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public AbsoluteAndRelativeDataRProgram(Repository repository,
			long changeDate, File absPath) throws RegisterException {
		super(repository, changeDate, absPath);
	}

	/**
	 * 
	 * @param other
	 *            The object to clone.
	 * 
	 * @throws RegisterException
	 */
	public AbsoluteAndRelativeDataRProgram(AbsoluteAndRelativeDataRProgram other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.program.r.RProgram#extractDataSetContent(de.clusteval.data
	 * .DataConfig)
	 */
	@Override
	protected Object extractDataSetContent(DataConfig dataConfig) {
		boolean absoluteData = dataConfig.getDatasetConfig().getDataSet()
				.getOriginalDataSet() instanceof AbsoluteDataSet;
		Object content;
		if (absoluteData) {
			AbsoluteDataSet dataSet = (AbsoluteDataSet) (dataConfig
					.getDatasetConfig().getDataSet().getOriginalDataSet());

			DataMatrix dataMatrix = dataSet.getDataSetContent();
			this.ids = dataMatrix.getIds();
			this.x = dataMatrix.getData();
			content = dataMatrix;
		} else {
			RelativeDataSet dataSet = (RelativeDataSet) (dataConfig
					.getDatasetConfig().getDataSet().getInStandardFormat());
			SimilarityMatrix simMatrix = dataSet.getDataSetContent();
			this.ids = dataSet.getIds().toArray(new String[0]);
			this.x = simMatrix.toArray();
			content = simMatrix;
		}
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.program.r.RProgram#beforeExec(de.clusteval.data.DataConfig,
	 * de.clusteval.program.ProgramConfig, java.lang.String[], java.util.Map,
	 * java.util.Map)
	 */
	@Override
	protected void beforeExec(DataConfig dataConfig,
			ProgramConfig programConfig, String[] invocationLine,
			Map<String, String> effectiveParams,
			Map<String, String> internalParams)
			throws RLibraryNotLoadedException, REngineException,
			RNotAvailableException, InterruptedException {
		super.beforeExec(dataConfig, programConfig, invocationLine,
				effectiveParams, internalParams);

		boolean absoluteData = dataConfig.getDatasetConfig().getDataSet()
				.getOriginalDataSet() instanceof AbsoluteDataSet;
		if (absoluteData) {
			rEngine.assign("x", x);
			rEngine.eval("rownames(x) <- ids");
		} else {
			rEngine.assign("x", x);
			rEngine.eval("x <- max(x)-x");
			this.convertDistancesToAppropriateDatastructure();
		}
	}

	protected void convertDistancesToAppropriateDatastructure()
			throws RserveException, InterruptedException {
		rEngine.eval("x <- as.dist(x)");
	}
}
