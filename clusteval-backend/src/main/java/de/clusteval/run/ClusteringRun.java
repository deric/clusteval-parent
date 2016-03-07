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
package de.clusteval.run;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.runnable.ClusteringRunRunnable;
import de.clusteval.run.runnable.ExecutionRunRunnable;

/**
 * A type of execution run that performs exactly one clustering with one
 * parameter set for every pair of program and data configuration.
 * 
 * @author Christian Wiwie
 * 
 */
public class ClusteringRun extends ExecutionRun {

	/**
	 * New objects of this type are automatically registered at the repository.
	 * 
	 * @param repository
	 *            the repository
	 * @param context
	 * @param changeDate
	 *            The date this run was performed.
	 * @param absPath
	 *            The absolute path to the file on the filesystem that
	 *            corresponds to this run.
	 * @param programConfigs
	 *            The program configurations of the new run.
	 * @param dataConfigs
	 *            The data configurations of the new run.
	 * @param qualityMeasures
	 *            The clustering quality measures of the new run.
	 * @param parameterValues
	 *            The parameter values of this run.
	 * @param postProcessorParameters
	 * @throws RegisterException
	 */
	public ClusteringRun(Repository repository, final Context context,
			long changeDate, File absPath, List<ProgramConfig> programConfigs,
			List<DataConfig> dataConfigs,
			List<ClusteringQualityMeasure> qualityMeasures,
			List<Map<ProgramParameter<?>, String>> parameterValues,
			final List<RunResultPostprocessor> postProcessors,
			final Map<String, Integer> maxExecutionTimes)
			throws RegisterException {
		super(repository, context, true, changeDate, absPath, programConfigs,
				dataConfigs, qualityMeasures, parameterValues, postProcessors,
				maxExecutionTimes);

		if (this.register()) {
			// register this Run at all dataconfigs and programconfigs
			for (DataConfig dataConfig : this.dataConfigs) {
				dataConfig.addListener(this);
			}
			for (ProgramConfig programConfig : this.programConfigs) {
				programConfig.addListener(this);
			}

			for (ClusteringQualityMeasure measure : this.qualityMeasures) {
				// added 21.03.2013: measures are only registered here, if this
				// run has been registered
				measure.register();
				measure.addListener(this);
			}
		}
	}

	/**
	 * Copy constructor of clustering runs.
	 * 
	 * @param clusteringRun
	 *            The clustering run to be cloned.
	 * @throws RegisterException
	 */
	public ClusteringRun(final ClusteringRun clusteringRun)
			throws RegisterException {
		super(clusteringRun);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.ExecutionRun#clone()
	 */
	@Override
	public ClusteringRun clone() {
		try {
			return new ClusteringRun(this);
		} catch (RegisterException e) {
			// should not occur
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.ExecutionRun#createRunRunnableFor(framework.RunScheduler,
	 * run.Run, program.ProgramConfig, data.DataConfig, java.lang.String,
	 * boolean)
	 */
	@Override
	protected ExecutionRunRunnable createRunRunnableFor(
			RunSchedulerThread runScheduler, Run run,
			ProgramConfig programConfig, DataConfig dataConfig,
			String runIdentString, boolean isResume,
			Map<ProgramParameter<?>, String> runParams) {
		ClusteringRunRunnable r = new ClusteringRunRunnable(runScheduler, run,
				programConfig, dataConfig, runIdentString, isResume, runParams);
		run.progress.addSubProgress(r.getProgressPrinter(), 10000);
		return r;
	}
}
