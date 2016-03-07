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
package de.clusteval.run;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualitySet;
import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.AbsoluteDataSetFormat;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryEvent;
import de.clusteval.framework.repository.RepositoryRemoveEvent;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunResultParseException;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.runnable.ExecutionRunRunnable;
import de.clusteval.run.runnable.ParameterOptimizationRunRunnable;
import de.clusteval.run.runnable.RunRunnable;
import de.wiwie.wiutils.utils.Pair;

/**
 * A type of execution run that performs several clusterings with different
 * parameter sets determined in an automatized way for every pair of program and
 * data configuration.
 * 
 * <p>
 * The evaluated parameter sets during a parameter optimization for one pair of
 * program and data configuration are determined by the corresponding
 * {@link ParameterOptimizationMethod} stored in {@link #optimizationMethods}.
 * 
 * <p>
 * Every evaluated parameter set is stored in the
 * {@link #optimizationParameters} attribute, such that evaluation of the
 * results is possible after termination of the run.
 * 
 * <p>
 * The results of the clusterings evaluated for every parameter set are also
 * stored in the {@link ParameterOptimizationMethod} object.
 * 
 * @author Christian Wiwie
 * 
 */
public class ParameterOptimizationRun extends ExecutionRun {

	/**
	 * This method verifies compatibility between a parameter optimization
	 * method, the data input format and the program configuration.
	 * 
	 * <p>
	 * Some parameter optimization do only work for certain programs, e.g.
	 * {@link GapStatisticParameterOptimizationMethod } works only for
	 * {@link KMeansClusteringRProgram} and {@link AbsoluteDataSetFormat}.
	 * 
	 * @param dataConfigs
	 * @param programConfigs
	 * @param optimizationMethods
	 * @throws IncompatibleParameterOptimizationMethodException
	 * 
	 */
	public static void checkCompatibilityParameterOptimizationMethod(
			final List<ParameterOptimizationMethod> optimizationMethods, final List<ProgramConfig> programConfigs,
			final List<DataConfig> dataConfigs) throws IncompatibleParameterOptimizationMethodException {
		for (ParameterOptimizationMethod method : optimizationMethods) {
			if (!method.getCompatibleDataSetFormatBaseClasses().isEmpty()) {
				// for every datasetformat we check, whether it class is
				// compatible
				for (DataConfig dataConfig : dataConfigs) {
					Class<? extends DataSetFormat> dataSetFormatClass = dataConfig.getDatasetConfig().getDataSet()
							.getDataSetFormat().getClass();
					boolean compatible = false;
					for (Class<? extends DataSetFormat> parentClass : method.getCompatibleDataSetFormatBaseClasses()) {
						if (parentClass.isAssignableFrom(dataSetFormatClass)) {
							compatible = true;
							break;
						}
					}
					if (!compatible) {
						throw new IncompatibleParameterOptimizationMethodException("The ParameterOptimizationMethod "
								+ method.getClass().getSimpleName() + " cannot be applied to the dataset "
								+ dataConfig.getDatasetConfig().getDataSet() + " with the format "
								+ dataSetFormatClass.getSimpleName());
					}
				}
			}

			if (!method.getCompatibleProgramNames().isEmpty()) {
				// for every program we check, whether it class is
				// compatible
				for (ProgramConfig programConfig : programConfigs) {
					String programName = programConfig.getProgram().getMajorName();
					boolean compatible = method.getCompatibleProgramNames().contains(programName);
					if (!compatible) {
						throw new IncompatibleParameterOptimizationMethodException(
								"The ParameterOptimizationMethod " + method.getClass().getSimpleName()
										+ " cannot be applied to the program " + programName);
					}
				}
			}
		}
	}

	/**
	 * This list holds another list of optimization parameters for every program
	 * configuration. These optimization parameters are to be optimized by this
	 * run.
	 */
	protected List<List<ProgramParameter<?>>> optimizationParameters;

	/**
	 * This list holds the parameter optimization methods for every pair of
	 * program and data configuration. These method objects control and keep
	 * track of the parameter sets and the results.
	 */
	protected List<ParameterOptimizationMethod> optimizationMethods;

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
	 * @param optimizationParameters
	 *            The parameters that are to be optimized during this run.
	 * @param optimizationMethods
	 *            The parameter optimization methods determines which parameter
	 *            sets are to be evaluated and stores the results.
	 * @throws RegisterException
	 */
	public ParameterOptimizationRun(final Repository repository, final Context context, final long changeDate,
			final File absPath, final List<ProgramConfig> programConfigs, final List<DataConfig> dataConfigs,
			final List<ClusteringQualityMeasure> qualityMeasures,
			final List<Map<ProgramParameter<?>, String>> parameterValues,
			final List<List<ProgramParameter<?>>> optimizationParameters,
			final List<ParameterOptimizationMethod> optimizationMethods,
			final List<RunResultPostprocessor> postProcessors, final Map<String, Integer> maxExecutionTimes)
					throws RegisterException {
		super(repository, context, false, changeDate, absPath, programConfigs, dataConfigs, qualityMeasures,
				parameterValues, postProcessors, maxExecutionTimes);

		this.optimizationParameters = optimizationParameters;
		this.optimizationMethods = optimizationMethods;

		if (this.register()) {
			// register this Run at all dataconfigs and programconfigs
			for (DataConfig dataConfig : this.dataConfigs) {
				dataConfig.addListener(this);
			}
			for (ProgramConfig programConfig : this.programConfigs) {
				programConfig.addListener(this);
			}
			for (ParameterOptimizationMethod method : this.optimizationMethods)
				method.addListener(this);

			for (ClusteringQualityMeasure measure : this.qualityMeasures) {
				// added 21.03.2013: measures are only registered here, if this
				// run has been registered
				measure.register();
				measure.addListener(this);
			}
		}
	}

	/**
	 * Copy constructor of parameter optimization runs.
	 * 
	 * @param otherRun
	 *            The parameter optimization run to be cloned.
	 * @throws RegisterException
	 */
	protected ParameterOptimizationRun(final ParameterOptimizationRun otherRun) throws RegisterException {
		super(otherRun);
		this.optimizationMethods = ParameterOptimizationMethod.cloneOptimizationMethods(otherRun.optimizationMethods);
		this.optimizationParameters = ProgramParameter.cloneParameterListList(otherRun.optimizationParameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.ExecutionRun#createRunRunnableFor(framework.RunScheduler,
	 * run.Run, program.ProgramConfig, data.DataConfig, java.lang.String,
	 * boolean)
	 */
	@Override
	protected ExecutionRunRunnable createRunRunnableFor(RunSchedulerThread runScheduler, Run run,
			ProgramConfig programConfig, DataConfig dataConfig, String runIdentString, boolean isResume,
			Map<ProgramParameter<?>, String> runParams) {

		// 06.04.2013: changed from indexOf to this manual search, because at
		// this point the passed programConfig and dataConfig are moved clones
		// of the originals in #runPairs
		int p = -1;
		for (int i = 0; i < ((ParameterOptimizationRun) run).getRunPairs().size(); i++) {
			Pair<ProgramConfig, DataConfig> pair = ((ParameterOptimizationRun) run).getRunPairs().get(i);
			if (pair.getFirst().getName().equals(programConfig.getName())
					&& pair.getSecond().getName().equals(dataConfig.getName())) {
				p = i;
				break;
			}
		}

		ParameterOptimizationMethod optimizationMethod = ((ParameterOptimizationRun) run).getOptimizationMethods()
				.get(p);
		ParameterOptimizationRunRunnable t = new ParameterOptimizationRunRunnable(runScheduler, run, programConfig,
				dataConfig, optimizationMethod, runIdentString, isResume, runParams);
		run.progress.addSubProgress(t.getProgressPrinter(), 10000);
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#clone()
	 */
	@Override
	public ParameterOptimizationRun clone() {
		try {
			return new ParameterOptimizationRun(this);
		} catch (RegisterException e) {
			// should not occur
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.ExecutionRun#notify(framework.repository.RepositoryEvent)
	 */
	@Override
	public void notify(RepositoryEvent e) throws RegisterException {
		super.notify(e);
		if (e instanceof RepositoryRemoveEvent) {
			RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
			if (optimizationMethods.contains(event.getRemovedObject())) {
				event.getRemovedObject().removeListener(this);
				this.log.info("Run " + this + ": Removed, because ParameterOptimizationMethod "
						+ event.getRemovedObject() + " has changed.");
				RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(this);
				this.unregister();
				this.notify(newEvent);
			}
		}
	}

	/**
	 * @return A list of parameter lists for every program configuration, that
	 *         are to be optimized.
	 * @see #optimizationParameters
	 */
	public List<List<ProgramParameter<?>>> getOptimizationParameters() {
		return this.optimizationParameters;
	}

	/**
	 * @return A list with optimization methods. One method for every program.
	 * @see #optimizationMethods
	 */
	public List<ParameterOptimizationMethod> getOptimizationMethods() {
		return this.optimizationMethods;
	}

	@Override
	public Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>> getOptimizationStatus() {
		Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>> result = new HashMap<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>>();
		try {
			for (RunRunnable t : this.runnables) {
				ParameterOptimizationRunRunnable thread = (ParameterOptimizationRunRunnable) t;
				Pair<String, String> configs = Pair.getPair(thread.getProgramConfig().toString(),
						thread.getDataConfig().toString());

				ParameterOptimizationResult paramOptRes = thread.getOptimizationMethod().getResult();

				boolean isInMemory = paramOptRes.isInMemory();
				if (!isInMemory)
					try {
						paramOptRes.loadIntoMemory();
						isInMemory = paramOptRes.isInMemory();
					} catch (RunResultParseException e) {
						isInMemory = false;
					}
				try {

					// measure -> best qualities
					Map<String, Pair<Map<String, String>, String>> qualities = new HashMap<String, Pair<Map<String, String>, String>>();
					// has the runnable already initialized the optimization
					// method
					// and result?
					if (paramOptRes != null && isInMemory) {
						// get the best achieved qualities
						ClusteringQualitySet bestQuals = thread.getOptimizationMethod().getResult()
								.getOptimalCriterionValue();
						// get the optimal parameter values
						Map<ClusteringQualityMeasure, ParameterSet> bestParams = thread.getOptimizationMethod()
								.getResult().getOptimalParameterSets();

						// measure -> best parameters
						Map<ClusteringQualityMeasure, Map<String, String>> bestParamsMap = new HashMap<ClusteringQualityMeasure, Map<String, String>>();
						for (ClusteringQualityMeasure measure : bestParams.keySet()) {
							ParameterSet pSet = bestParams.get(measure);
							Map<String, String> tmp = new HashMap<String, String>();
							for (String p : pSet.keySet())
								tmp.put(p.toString(), pSet.get(p));

							bestParamsMap.put(measure, tmp);
						}

						for (ClusteringQualityMeasure measure : bestQuals.keySet())
							qualities.put(measure.getAlias(),
									Pair.getPair(bestParamsMap.get(measure), bestQuals.get(measure).toString()));
					}

					result.put(configs, new Pair<Double, Map<String, Pair<Map<String, String>, String>>>(
							(double) t.getProgressPrinter().getPercent(), qualities));
				} finally {
					if (!isInMemory)
						paramOptRes.unloadFromMemory();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
