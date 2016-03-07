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
import java.util.ArrayList;
import java.util.List;

import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.runnable.DataAnalysisRunRunnable;
import de.clusteval.run.runnable.RunRunnable;
import de.wiwie.wiutils.file.FileUtils;

/**
 * A type of analysis run that conducts analyses of data inputs encapsulated by
 * data configurations.
 * 
 * <p>
 * A data analysis run has a list of data configurations in {@link #dataConfigs}
 * , that should be assessed during execution of the run. Additionally they
 * inherit a list of data statistics in {@link AnalysisRun#statistics} that
 * should be assessed for every data configuration.
 * 
 * @author Christian Wiwie
 * 
 */
public class DataAnalysisRun extends AnalysisRun<DataStatistic> {

	/**
	 * A list of data configurations, that should be assessed during execution
	 * of the run.
	 */
	protected List<DataConfig> dataConfigs;

	/**
	 * @param repository
	 *            The repository this run should be registered at.
	 * @param context
	 * @param changeDate
	 *            The date this run was performed.
	 * @param absPath
	 *            The absolute path to the file on the filesystem that
	 *            corresponds to this run.
	 * @param dataConfigs
	 *            The list of data configurations, that should be assessed
	 *            during execution of the run.
	 * @param statistics
	 *            The statistics that should be assessed for the objects of
	 *            analysis.
	 * @throws RegisterException
	 */
	public DataAnalysisRun(Repository repository, final Context context,
			long changeDate, File absPath, List<DataConfig> dataConfigs,
			List<DataStatistic> statistics) throws RegisterException {
		super(repository, context, changeDate, absPath, statistics);
		this.dataConfigs = dataConfigs;

		if (this.register()) {
			// register this Run at all dataconfigs
			for (DataConfig dataConfig : this.dataConfigs) {
				dataConfig.addListener(this);
			}

			for (DataStatistic statistic : this.statistics) {
				// added 21.03.2013
				statistic.register();
				statistic.addListener(this);
			}
		}
	}

	/**
	 * Copy constructor of data analysis runs.
	 * 
	 * @param other
	 *            The data analysis run to be cloned.
	 * @throws RegisterException
	 */
	protected DataAnalysisRun(final DataAnalysisRun other)
			throws RegisterException {
		super(other);
		this.dataConfigs = DataConfig
				.cloneDataConfigurations(other.dataConfigs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.AnalysisRun#cloneStatistics(java.util.List)
	 */
	@Override
	protected List<DataStatistic> cloneStatistics(List<DataStatistic> statistics) {
		final List<DataStatistic> result = new ArrayList<DataStatistic>();

		for (DataStatistic st : statistics)
			result.add(st.clone());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#clone()
	 */
	@Override
	public DataAnalysisRun clone() {
		try {
			return new DataAnalysisRun(this);
		} catch (RegisterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return A list containing all data configurations to be assessed by this
	 *         run.
	 */
	public List<DataConfig> getDataConfigs() {
		return dataConfigs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#terminate()
	 */
	@Override
	public boolean terminate() {
		return true;
	}

	@Override
	protected RunRunnable createAndScheduleRunnableForRunPair(
			RunSchedulerThread runScheduler, int p) {

		File movedConfigsDir = getMovedConfigsDir();

		/*
		 * We only operate on this copy, in order to avoid multithreading
		 * problems.
		 */
		// changed 13.02.2013
		// DataAnalysisRun runCopy = this.clone();
		DataAnalysisRun runCopy = this;

		DataConfig dataConfig = this.getDataConfigs().get(p);

		/*
		 * Copy to results directory
		 */
		dataConfig.copyToFolder(movedConfigsDir);
		dataConfig.getDatasetConfig().copyToFolder(movedConfigsDir);
		if (dataConfig.hasGoldStandardConfig())
			dataConfig.getGoldstandardConfig().copyToFolder(movedConfigsDir);

		String input = dataConfig.getDatasetConfig().getDataSet()
				.getAbsolutePath();

		/*
		 * To avoid overwriting of the input or conversion files, we copy it to
		 * the results directory (which is unique for this run).
		 */

		String movedInput = FileUtils.buildPath(new File(runCopy
				.getRepository().getClusterResultsBasePath()).getParentFile()
				.getAbsolutePath().replace("%RUNIDENTSTRING", runIdentString),
				"inputs", dataConfig.getName(), new File(input).getParentFile()
						.getName(), new File(input).getName());
		if (!(new File(movedInput).exists()))
			dataConfig.getDatasetConfig().getDataSet()
					.copyTo(new File(movedInput));

		/*
		 * Change the path to the input in the DataSetConfig.
		 */
		dataConfig.getDatasetConfig().getDataSet()
				.setAbsolutePath(new File(movedInput));

		/*
		 * Copy gold standard
		 */
		if (dataConfig.hasGoldStandardConfig()) {
			String goldStandard = dataConfig.getGoldstandardConfig()
					.getGoldstandard().getAbsolutePath();

			String movedGoldStandard = FileUtils.buildPath(
					new File(runCopy.getRepository()
							.getClusterResultsBasePath()).getParentFile()
							.getAbsolutePath()
							.replace("%RUNIDENTSTRING", runIdentString),
					"goldstandards", new File(goldStandard).getParentFile()
							.getName(), new File(goldStandard).getName());
			if (!(new File(movedGoldStandard).exists()))
				dataConfig.getGoldstandardConfig().getGoldstandard()
						.copyTo(new File(movedGoldStandard), false);

			/*
			 * Change the path to the goldstandard in the GoldstandardConfig.
			 */
			dataConfig.getGoldstandardConfig().getGoldstandard()
					.setAbsolutePath(new File(movedGoldStandard));
		}

		/*
		 * Start a thread with the invocation line and a path to the log file.
		 * The RunThread redirects all the output of the program into the
		 * logFile.
		 */
		final RunRunnable t = new DataAnalysisRunRunnable(runScheduler,
				runCopy, runIdentString, false, dataConfig,
				runCopy.getStatistics());
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#getUpperLimitProgress()
	 */
	@Override
	protected long getUpperLimitProgress() {
		return this.statistics.size() * 100;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#getNumberOfRunRunnables()
	 */
	@Override
	protected int getNumberOfRunRunnables() {
		return this.getDataConfigs().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#createAndAddRunnableForResumePair(framework.RunScheduler,
	 * int)
	 */
	@Override
	protected RunRunnable createAndScheduleRunnableForResumePair(
			RunSchedulerThread runScheduler, int p) {

		File movedConfigsDir = getMovedConfigsDir();

		/*
		 * We only operate on this copy, in order to avoid multithreading
		 * problems.
		 */
		// changed 13.02.2013
		// DataAnalysisRun runCopy = this.clone();
		DataAnalysisRun runCopy = this;

		DataConfig dataConfig = this.getDataConfigs().get(p);

		/*
		 * Copy to results directory
		 */
		dataConfig.copyToFolder(movedConfigsDir, false);
		dataConfig.getDatasetConfig().copyToFolder(movedConfigsDir, false);
		if (dataConfig.hasGoldStandardConfig())
			dataConfig.getGoldstandardConfig().copyToFolder(movedConfigsDir,
					false);

		String input = dataConfig.getDatasetConfig().getDataSet()
				.getAbsolutePath();

		/*
		 * To avoid overwriting of the input or conversion files, we copy it to
		 * the results directory (which is unique for this run).
		 */

		String movedInput = FileUtils.buildPath(
				new File(runCopy.getRepository().getParent()
						.getClusterResultsBasePath()).getParentFile()
						.getAbsolutePath()
						.replace("%RUNIDENTSTRING", runIdentString), "inputs",
				dataConfig.getName(),
				new File(input).getParentFile().getName(),
				new File(input).getName());
		if (!(new File(movedInput).exists()))
			dataConfig.getDatasetConfig().getDataSet()
					.copyTo(new File(movedInput), false);

		/*
		 * Change the path to the input in the DataSetConfig.
		 */
		dataConfig.getDatasetConfig().getDataSet()
				.setAbsolutePath(new File(movedInput));

		/*
		 * Copy gold standard
		 */
		if (dataConfig.hasGoldStandardConfig()) {
			String goldStandard = dataConfig.getGoldstandardConfig()
					.getGoldstandard().getAbsolutePath();

			String movedGoldStandard = FileUtils.buildPath(
					new File(runCopy.getRepository().getParent()
							.getClusterResultsBasePath()).getParentFile()
							.getAbsolutePath()
							.replace("%RUNIDENTSTRING", runIdentString),
					"goldstandards", new File(goldStandard).getParentFile()
							.getName(), new File(goldStandard).getName());
			if (!(new File(movedGoldStandard).exists()))
				dataConfig.getGoldstandardConfig().getGoldstandard()
						.copyTo(new File(movedGoldStandard), false);

			/*
			 * Change the path to the goldstandard in the GoldstandardConfig.
			 */
			dataConfig.getGoldstandardConfig().getGoldstandard()
					.setAbsolutePath(new File(movedGoldStandard));
		}

		/*
		 * Start a thread with the invocation line and a path to the log file.
		 * The RunThread redirects all the output of the program into the
		 * logFile.
		 */
		final RunRunnable t = new DataAnalysisRunRunnable(runScheduler,
				runCopy, runIdentString, true, dataConfig,
				runCopy.getStatistics());
		return t;
	}
}
