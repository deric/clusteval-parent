/**
 * 
 */
package de.clusteval.run.runnable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.clusteval.data.DataConfig;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.data.statistics.DataStatisticCalculator;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.StatisticCalculator;
import de.wiwie.wiutils.file.FileUtils;

/**
 * @author Christian Wiwie
 *
 */
public class DataAnalysisIterationRunnable
		extends
			AnalysisIterationRunnable<DataStatistic, DataAnalysisIterationWrapper> {

	/**
	 * @param iterationWrapper
	 */
	public DataAnalysisIterationRunnable(
			DataAnalysisIterationWrapper iterationWrapper) {
		super(iterationWrapper);
	}

	public DataConfig getDataConfig() {
		return this.iterationWrapper.getDataConfig();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.AnalysisIterationRunnable#beforeStatisticCalculate
	 * ()
	 */
	protected void beforeStatisticCalculate() {
		this.log.info("Run " + this.getRun() + " - (" + this.getDataConfig()
				+ ") Analysing " + getStatistic().getIdentifier());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * run.runnable.AnalysisRunRunnable#getStatisticCalculator(java.lang.Class)
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected StatisticCalculator<DataStatistic> getStatisticCalculator()
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {

		Class<? extends DataStatisticCalculator> calcClass = getRun()
				.getRepository().getDataStatisticCalculator(
						getStatistic().getClass().getName());
		Constructor<? extends DataStatisticCalculator> constr = calcClass
				.getConstructor(Repository.class, long.class, File.class,
						DataConfig.class);
		DataStatisticCalculator calc = constr.newInstance(this.getRun()
				.getRepository(), calcFile.lastModified(), calcFile, this
				.getDataConfig());
		return calc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.AnalysisIterationRunnable#getOutputPath()
	 */
	@Override
	protected String getOutputPath() {
		return FileUtils.buildPath(this.iterationWrapper.getAnalysesFolder(),
				this.getDataConfig() + "_"
						+ this.getStatistic().getClass().getSimpleName()
						+ ".txt");
	}
}
