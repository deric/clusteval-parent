/**
 * 
 */
package de.clusteval.run.runnable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.clusteval.framework.repository.Repository;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.run.statistics.RunStatisticCalculator;
import de.clusteval.utils.StatisticCalculator;
import de.wiwie.wiutils.file.FileUtils;

/**
 * @author Christian Wiwie
 *
 */
public class RunAnalysisIterationRunnable
		extends
			AnalysisIterationRunnable<RunStatistic, RunAnalysisIterationWrapper> {

	/**
	 * @param iterationWrapper
	 */
	public RunAnalysisIterationRunnable(
			RunAnalysisIterationWrapper iterationWrapper) {
		super(iterationWrapper);
	}

	public String getRunIdentifier() {
		return this.iterationWrapper.getRunIdentifier();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.AnalysisIterationRunnable#beforeStatisticCalculate
	 * ()
	 */
	protected void beforeStatisticCalculate() {
		this.log.info("Run " + this.getRun() + " - (" + this.getRunIdentifier()
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
	protected StatisticCalculator<RunStatistic> getStatisticCalculator()
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Class<? extends RunStatisticCalculator> calcClass = getRun()
				.getRepository().getRunStatisticCalculator(
						getStatistic().getClass().getName());
		Constructor<? extends RunStatisticCalculator> constr = calcClass
				.getConstructor(Repository.class, long.class, File.class,
						String.class);
		RunStatisticCalculator calc = constr.newInstance(this.getRun()
				.getRepository(), calcFile.lastModified(), calcFile, this
				.getRunIdentifier());
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
				this.getRunIdentifier() + "_"
						+ this.getStatistic().getIdentifier() + ".txt");
	}
}
