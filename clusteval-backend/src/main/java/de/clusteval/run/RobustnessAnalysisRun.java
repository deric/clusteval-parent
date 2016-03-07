/**
 * 
 */
package de.clusteval.run;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.Options;
import org.apache.commons.configuration.ConfigurationException;

import de.wiwie.wiutils.utils.Triple;
import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualityMeasureValue;
import de.clusteval.cluster.quality.ClusteringQualitySet;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.Context;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.DataSetNotFoundException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.NoDataSetException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.GoldStandardConfigNotFoundException;
import de.clusteval.data.goldstandard.GoldStandardConfigurationException;
import de.clusteval.data.goldstandard.GoldStandardNotFoundException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.DataRandomizeException;
import de.clusteval.data.randomizer.DataRandomizer;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.program.NoOptimizableProgramParameterException;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.program.UnknownParameterType;
import de.clusteval.program.UnknownProgramParameterException;
import de.clusteval.program.UnknownProgramTypeException;
import de.clusteval.program.r.UnknownRProgramException;
import de.clusteval.run.result.ClusteringRunResult;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.result.RunResultParseException;
import de.clusteval.run.result.format.UnknownRunResultFormatException;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.result.postprocessing.UnknownRunResultPostprocessorException;
import de.clusteval.run.runnable.ExecutionRunRunnable;
import de.clusteval.run.runnable.RobustnessAnalysisRunRunnable;
import de.clusteval.run.runnable.RunRunnable;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.InvalidConfigurationFileException;
import de.wiwie.wiutils.file.FileUtils;

/**
 * @author Christian Wiwie
 *
 */
public class RobustnessAnalysisRun extends ClusteringRun {

	protected List<DataConfig> originalDataConfigs;

	/**
	 * A list of unique run identifiers, that should be assessed during
	 * execution of the run
	 */
	protected List<String> uniqueRunAnalysisRunIdentifiers;

	protected DataRandomizer randomizer;

	protected List<ParameterSet> distortionParams;

	protected int numberOfDistortedDataSets;

	/**
	 * @param repository
	 *            The repository this run should be registered at.
	 * @param context
	 * @param changeDate
	 *            The date this run was performed.
	 * @param absPath
	 *            The absolute path to the file on the filesystem that
	 *            corresponds to this run.
	 * @param uniqueRunIdentifiers
	 *            The list of unique run identifiers, that should be assessed
	 *            during execution of the run.
	 * @throws RegisterException
	 */
	public RobustnessAnalysisRun(Repository repository, final Context context,
			long changeDate, File absPath, List<String> uniqueRunIdentifiers,
			List<ProgramConfig> programConfigs, List<DataConfig> dataConfigs,
			List<DataConfig> originalDataConfigs,
			List<ClusteringQualityMeasure> qualityMeasures,
			List<Map<ProgramParameter<?>, String>> parameterValues,
			final List<RunResultPostprocessor> postProcessors,
			final DataRandomizer randomizer,
			final List<ParameterSet> randomizerParams,
			final int numberOfRandomizedDataSets,
			final Map<String, Integer> maxExecutionTimes)
			throws RegisterException {
		// the parameter values are just dummies; they will be parsed from the
		// uniqueRunIdentifiers later and override these values here;
		super(repository, context, changeDate, absPath, programConfigs,
				dataConfigs, qualityMeasures, parameterValues, postProcessors,
				maxExecutionTimes);
		this.uniqueRunAnalysisRunIdentifiers = uniqueRunIdentifiers;

		this.randomizer = randomizer;
		this.distortionParams = randomizerParams;
		this.numberOfDistortedDataSets = numberOfRandomizedDataSets;
		this.originalDataConfigs = originalDataConfigs;

		if (this.register()) {
			// for (RunStatistic statistic : this.statistics) {
			// // added 21.03.2013
			// statistic.register();
			// statistic.addListener(this);
			// }
		}
	}

	/**
	 * Copy constructor of run analysis runs.
	 * 
	 * @param other
	 *            The run analysis run to be cloned.
	 * @throws RegisterException
	 */
	protected RobustnessAnalysisRun(final RobustnessAnalysisRun other)
			throws RegisterException {
		super(other);

		this.uniqueRunAnalysisRunIdentifiers = new ArrayList<String>();

		for (String s : other.uniqueRunAnalysisRunIdentifiers)
			this.uniqueRunAnalysisRunIdentifiers.add(s);

		this.randomizer = other.randomizer.clone();
		this.distortionParams = new ArrayList<ParameterSet>();
		for (ParameterSet paramSet : other.distortionParams)
			this.distortionParams.add(paramSet.clone());
		this.numberOfDistortedDataSets = other.numberOfDistortedDataSets;
		this.dataConfigs = new ArrayList<DataConfig>(other.dataConfigs);
		this.originalDataConfigs = new ArrayList<DataConfig>(
				other.originalDataConfigs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.ClusteringRun#clone()
	 */
	@Override
	public RobustnessAnalysisRun clone() {
		try {
			return new RobustnessAnalysisRun(this);
		} catch (RegisterException e) {
			// e.printStackTrace();
			// should not happen
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.Run#beforeResume(java.lang.String)
	 */
	@Override
	protected void beforeResume(String runIdentString)
			throws RunInitializationException {
		super.beforeResume(runIdentString);

		this.log.info("Finding best parameters in run results");
		// find best parameters in run results
		try {
			this.findBestParamsAndInitParameterValues(this.getRepository()
					.getParent());

			// generate randomized data sets
			// the directory, the new data sets will be stored in
			String dataSetBasePath = this.getRepository().getParent()
					.getBasePath(DataSet.class);
			File newDataSetDir = new File(FileUtils.buildPath(dataSetBasePath,
					this.getRunIdentificationString()));
			newDataSetDir.mkdir();
			// the directory, the new gold standards will be stored in
			String goldStandardBasePath = this.getRepository().getParent()
					.getBasePath(GoldStandard.class);
			File newGoldStandardDir = new File(FileUtils.buildPath(
					goldStandardBasePath, this.getRunIdentificationString()));
			newGoldStandardDir.mkdir();

			Map<DataConfig, List<DataConfig>> newDataConfigs = new HashMap<DataConfig, List<DataConfig>>();

			Options options = this.randomizer.getAllOptions();

			// generate those randomized data sets which are not generated yet
			for (DataConfig dataConfig : this.dataConfigs) {
				this.log.info("... for data config '" + dataConfig.getName()
						+ "'");
				newDataConfigs.put(dataConfig, new ArrayList<DataConfig>());
				for (ParameterSet paramSet : this.distortionParams) {

					for (int i = 1; i <= this.numberOfDistortedDataSets; i++) {
						List<String> params = new ArrayList<String>();
						for (String param : paramSet.keySet()) {
							if (options.hasOption(param)) {
								params.add("-" + param);
								params.add(paramSet.get(param));
							}
						}
						params.add("-dataConfig");
						params.add(dataConfig.getName());
						params.add("-uniqueId");
						params.add(this.runIdentString + "_" + i);
						try {
							DataConfig newDataConfig = this.randomizer
									.randomize(params.toArray(new String[0]),
											true);

							File targetDataSetFile = new File(
									FileUtils.buildPath(
											newDataSetDir.getAbsolutePath(),
											newDataConfig.getDatasetConfig()
													.getDataSet()
													.getMinorName()));
							File targetGoldStandardFile = new File(
									FileUtils.buildPath(newGoldStandardDir
											.getAbsolutePath(), newDataConfig
											.getGoldstandardConfig()
											.getGoldstandard().getMinorName()));

							if (!targetDataSetFile.exists()
									|| !targetGoldStandardFile.exists()) {
								newDataConfig.getDatasetConfig().getDataSet()
										.moveTo(targetDataSetFile);
								newDataConfig.getGoldstandardConfig()
										.getGoldstandard()
										.moveTo(targetGoldStandardFile);
								newDataConfigs.get(dataConfig).add(
										newDataConfig);
							} else {
								this.log.info("Randomized data config existed; using old data");
								new File(newDataConfig.getDatasetConfig()
										.getDataSet().getAbsolutePath())
										.delete();
								new File(newDataConfig.getGoldstandardConfig()
										.getGoldstandard().getAbsolutePath())
										.delete();

								DataSet ds = this
										.getRepository()
										.getParent()
										.getStaticObjectWithName(
												DataSet.class,
												targetDataSetFile
														.getParentFile()
														.getName()
														+ "/"
														+ targetDataSetFile
																.getName());

								newDataConfig.getDatasetConfig().setDataSet(ds);
								// newDataConfig.getDatasetConfig().dumpToFile();
								newDataConfig
										.getGoldstandardConfig()
										.setGoldStandard(
												GoldStandard
														.parseFromFile(targetGoldStandardFile));
								// newDataConfig.getGoldstandardConfig()
								// .dumpToFile();
								newDataConfigs
										.get(dataConfig)
										.add(this
												.getRepository()
												.getParent()
												.getStaticObjectWithName(
														DataConfig.class,
														newDataConfig.getName()));
							}

						} catch (DataRandomizeException e) {
							throw new RunInitializationException(e);
						}
					}
				}
			}

			this.originalDataConfigs = this.dataConfigs;
			this.dataConfigs = new ArrayList<DataConfig>();
			for (DataConfig dc : this.originalDataConfigs)
				this.dataConfigs.addAll(newDataConfigs.get(dc));

			this.log.info("Get dataconfigs corresponding to original data configs");

			// override the old run pairs from constructor
			initRunPairs(programConfigs, dataConfigs);
		} catch (Exception e) {
			throw new RunInitializationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.Run#afterResume(java.lang.String)
	 */
	@Override
	protected void afterResume(String runIdentString) {
		this.createAnalysesDirectory();

		super.afterResume(runIdentString);
	}

	/**
	 * 
	 */
	protected void createAnalysesDirectory() {
		FileUtils.delete(new File(FileUtils.buildPath(
				this.repository.getBasePath(RunResult.class),
				this.getRunIdentificationString(), "analyses")));
		new File(FileUtils.buildPath(
				this.repository.getBasePath(RunResult.class),
				this.getRunIdentificationString(), "analyses")).mkdir();

		Set<String> paths = new HashSet<String>();

		for (int i = 0; i < this.results.size(); i++) {
			ClusteringRunResult result = (ClusteringRunResult) this.results
					.get(i);
			try {
				result.loadIntoMemory();
				ClusteringQualitySet quals = result.getClustering().getSecond()
						.getQualities();
				result.unloadFromMemory();

				ProgramConfig programConfig = result.getProgramConfig();
				DataConfig dataConfig = result.getDataConfig();

				String resultPath = FileUtils.buildPath(
						this.repository.getBasePath(RunResult.class),
						this.getRunIdentificationString(), "analyses",
						programConfig.getName() + ".robustness");

				// new File(resultPath).delete();

				StringBuilder sb = new StringBuilder();
				if (!paths.contains(resultPath)) {
					sb.append("DataConfig");
					sb.append("\t");
					// first time we write into this file
					for (ClusteringQualityMeasure measure : this.qualityMeasures) {
						sb.append(measure.getClass().getSimpleName());
						sb.append("\t");
					}
					sb.deleteCharAt(sb.length() - 1);
					sb.append(System.getProperty("line.separator"));
				}

				sb.append(dataConfig.getName());
				sb.append("\t");
				for (ClusteringQualityMeasure measure : this.qualityMeasures) {
					sb.append(quals.get(measure));
					sb.append("\t");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(System.getProperty("line.separator"));
				FileUtils.appendStringToFile(resultPath, sb.toString());

				paths.add(resultPath);
			} catch (Exception e) {
				// just skip that run result when
				System.out.println(result.getAbsolutePath());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.Run#beforePerform()
	 */
	@Override
	protected void beforePerform() throws IOException,
			RunInitializationException {
		super.beforePerform();

		this.log.info("Finding best parameters in run results");
		// find best parameters in run results
		try {
			this.findBestParamsAndInitParameterValues(this.getRepository());

			this.log.info("Generate randomized data sets...");
			// generate randomized data sets
			// the directory, the new data sets will be stored in
			String dataSetBasePath = this.getRepository().getBasePath(
					DataSet.class);
			File newDataSetDir = new File(FileUtils.buildPath(dataSetBasePath,
					this.getRunIdentificationString()));
			newDataSetDir.mkdir();
			// the directory, the new gold standards will be stored in
			String goldStandardBasePath = this.getRepository().getBasePath(
					GoldStandard.class);
			File newGoldStandardDir = new File(FileUtils.buildPath(
					goldStandardBasePath, this.getRunIdentificationString()));
			newGoldStandardDir.mkdir();

			Map<DataConfig, List<DataConfig>> newDataConfigs = new HashMap<DataConfig, List<DataConfig>>();

			Options options = this.randomizer.getAllOptions();

			// generate randomized data sets
			for (DataConfig dataConfig : this.dataConfigs) {
				this.log.info("... for data config '" + dataConfig.getName()
						+ "'");
				newDataConfigs.put(dataConfig, new ArrayList<DataConfig>());
				for (ParameterSet paramSet : this.distortionParams) {

					for (int i = 1; i <= this.numberOfDistortedDataSets; i++) {
						List<String> params = new ArrayList<String>();
						for (String param : paramSet.keySet()) {
							if (options.hasOption(param)) {
								params.add("-" + param);
								params.add(paramSet.get(param));
							}
						}
						params.add("-dataConfig");
						params.add(dataConfig.getName());
						params.add("-uniqueId");
						params.add(this.runIdentString + "_" + i);
						try {
							DataConfig newDataConfig = this.randomizer
									.randomize(params.toArray(new String[0]));

							File targetDataSetFile = new File(
									FileUtils.buildPath(
											newDataSetDir.getAbsolutePath(),
											newDataConfig.getDatasetConfig()
													.getDataSet()
													.getMinorName()));
							File targetGoldStandardFile = new File(
									FileUtils.buildPath(newGoldStandardDir
											.getAbsolutePath(), newDataConfig
											.getGoldstandardConfig()
											.getGoldstandard().getMinorName()));

							newDataConfig.getDatasetConfig().getDataSet()
									.moveTo(targetDataSetFile);
							newDataConfig.getGoldstandardConfig()
									.getGoldstandard()
									.moveTo(targetGoldStandardFile);

							newDataConfigs.get(dataConfig).add(newDataConfig);

						} catch (DataRandomizeException e) {
							throw new RunInitializationException(e);
						}
					}
				}
			}

			this.originalDataConfigs = this.dataConfigs;
			this.dataConfigs = new ArrayList<DataConfig>();
			for (DataConfig dc : this.originalDataConfigs)
				this.dataConfigs.addAll(newDataConfigs.get(dc));

			// override the old run pairs
			initRunPairs(programConfigs, dataConfigs);
		} catch (Exception e) {
			throw new RunInitializationException(e);
		}
	}

	/**
	 * @throws UnknownDataRandomizerException
	 * @throws UnknownRunResultPostprocessorException
	 * @throws InterruptedException
	 * @throws IncompatibleContextException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownDataSetTypeException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDistanceMeasureException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownDataStatisticException
	 * @throws RunException
	 * @throws InvalidOptimizationParameterException
	 * @throws NoRepositoryFoundException
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws InvalidConfigurationFileException
	 * @throws UnknownProgramParameterException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws InvalidRunModeException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownRunResultFormatException
	 * @throws IOException
	 * @throws UnknownParameterType
	 * @throws UnknownContextException
	 * @throws RegisterException
	 * @throws ConfigurationException
	 * @throws RunResultParseException
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws NoDataSetException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws UnknownGoldStandardFormatException
	 * @throws UnknownDataSetFormatException
	 * 
	 */
	protected void findBestParamsAndInitParameterValues(
			final Repository repository) throws UnknownDataSetFormatException,
			UnknownGoldStandardFormatException, GoldStandardNotFoundException,
			GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, RunResultParseException,
			ConfigurationException, RegisterException, UnknownContextException,
			UnknownParameterType, IOException, UnknownRunResultFormatException,
			UnknownClusteringQualityMeasureException, InvalidRunModeException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownProgramParameterException,
			InvalidConfigurationFileException,
			RepositoryAlreadyExistsException, InvalidRepositoryException,
			NoRepositoryFoundException, InvalidOptimizationParameterException,
			RunException, UnknownDataStatisticException,
			UnknownProgramTypeException, UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownDistanceMeasureException, UnknownRunStatisticException,
			RepositoryConfigNotFoundException,
			RepositoryConfigurationException, UnknownDataSetTypeException,
			UnknownRunDataStatisticException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleContextException, InterruptedException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {

		List<String> programConfigNames = new ArrayList<String>();
		for (ProgramConfig programConfig : programConfigs)
			programConfigNames.add(programConfig.getName());
		List<String> dataConfigNames = new ArrayList<String>();
		for (DataConfig dataConfig : this.originalDataConfigs)
			dataConfigNames.add(dataConfig.getName());

		Map<String, Map<String, Triple<List<ParameterSet>, ClusteringQualityMeasure, ClusteringQualityMeasureValue>>> bestParams = new HashMap<String, Map<String, Triple<List<ParameterSet>, ClusteringQualityMeasure, ClusteringQualityMeasureValue>>>();

		// get best parameters for each pair of program and dataset
		// from run results
		for (String runIdentifier : this.uniqueRunAnalysisRunIdentifiers) {
			this.log.info("... parsing run result '" + runIdentifier + "'");
			List<RunResult> results = new ArrayList<RunResult>();
			RunResult.parseFromRunResultFolder(
					repository,
					new File(FileUtils.buildPath(
							repository.getBasePath(RunResult.class),
							runIdentifier)), results, false, false, false);
			for (RunResult runResult : results) {
				if (runResult instanceof ParameterOptimizationResult) {
					this.log.info("...... " + runResult.getAbsolutePath());
					ParameterOptimizationResult paramOptResult = (ParameterOptimizationResult) runResult;
					paramOptResult.loadIntoMemory();
					ProgramConfig pc = paramOptResult.getProgramConfig();
					DataConfig dc = paramOptResult.getDataConfig();
					ClusteringQualityMeasure measure = paramOptResult
							.getMethod().getOptimizationCriterion();
					ClusteringQualityMeasureValue min = ClusteringQualityMeasureValue
							.getForDouble(measure.getMinimum());
					ClusteringQualityMeasureValue max = ClusteringQualityMeasureValue
							.getForDouble(measure.getMaximum());
					ClusteringQualityMeasureValue def;
					if (measure.isBetterThan(max, min))
						def = min;
					else
						def = max;

					if (programConfigNames.contains(pc.getName())
							&& dataConfigNames.contains(dc.getName())) {
						if (!bestParams.containsKey(pc.getName()))
							bestParams
									.put(pc.getName(),
											new HashMap<String, Triple<List<ParameterSet>, ClusteringQualityMeasure, ClusteringQualityMeasureValue>>());
						if (!bestParams.get(pc.getName()).containsKey(
								dc.getName()))
							bestParams
									.get(pc.getName())
									.put(dc.getName(),
											new Triple<List<ParameterSet>, ClusteringQualityMeasure, ClusteringQualityMeasureValue>(
													new ArrayList<ParameterSet>(),
													measure, def));

						ClusteringQualityMeasureValue currentOpt = bestParams
								.get(pc.getName()).get(dc.getName()).getThird();
						Map<ClusteringQualityMeasure, ParameterSet> optParams = paramOptResult
								.getOptimalParameterSets();
						ParameterSet bestParamSet = optParams.get(measure);
						Clustering cl = paramOptResult
								.getClustering(bestParamSet);
						if (paramOptResult.get(bestParamSet) == null)
							continue;
						ClusteringQualityMeasureValue newBestValue = paramOptResult
								.get(bestParamSet).get(measure);

						if (measure.isBetterThan(newBestValue, currentOpt)) {
							// we found a better quality, so we empty
							// the list and add the new parameter sets
							List<ParameterSet> paramSets = bestParams
									.get(pc.getName()).get(dc.getName())
									.getFirst();
							paramSets.clear();
							paramSets.add(bestParamSet);

							bestParams.get(pc.getName()).get(dc.getName())
									.setThird(newBestValue);
						} else if (newBestValue.isTerminated()
								&& newBestValue.getValue() == currentOpt
										.getValue()) {
							// we found a parameter set with the same
							// quality, add it to the list
							List<ParameterSet> paramSets = bestParams
									.get(pc.getName()).get(dc.getName())
									.getFirst();
							paramSets.add(bestParamSet);
						}
					}
					paramOptResult.unloadFromMemory();
				}
			}
		}

		for (String pc : bestParams.keySet()) {
			for (String dc : bestParams.get(pc).keySet()) {
				this.log.info(String.format("%s\t%s\t%s\n", pc, dc, bestParams
						.get(pc).get(dc).getFirst().get(0)));
			}
		}

		this.log.info("Taking one parameter set for each pair of program config and data config");
		this.parameterValues.clear();
		// TODO: for numerical parameter ranges, take the mean
		// for string parameters, just take any one
		for (ProgramConfig pc : programConfigs) {
			for (DataConfig dc : originalDataConfigs) {
				Map<ProgramParameter<? extends Object>, String> m = new HashMap<ProgramParameter<? extends Object>, String>();

				if (bestParams.containsKey(pc.getName())
						&& bestParams.get(pc.getName()).containsKey(
								dc.getName())) {
					Triple<List<ParameterSet>, ClusteringQualityMeasure, ClusteringQualityMeasureValue> params = bestParams
							.get(pc.getName()).get(dc.getName());

					if (params.getFirst().size() > 0) {
						for (String p : params.getFirst().get(0).keySet())
							m.put(pc.getParameterForName(p), params.getFirst()
									.get(0).get(p));
					}
				}
				// add it several times, once for each randomized dataset
				// per data config
				for (int i = 0; i < this.numberOfDistortedDataSets; i++)
					for (int j = 0; j < this.distortionParams.size(); j++)
						this.parameterValues
								.add(new HashMap<ProgramParameter<? extends Object>, String>(
										m));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.Run#afterPerform()
	 */
	@Override
	protected void afterPerform() {
		this.createAnalysesDirectory();

		super.afterPerform();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.ClusteringRun#createRunRunnableFor(de.clusteval.framework
	 * .threading.RunSchedulerThread, de.clusteval.run.Run,
	 * de.clusteval.program.ProgramConfig, de.clusteval.data.DataConfig,
	 * java.lang.String, boolean)
	 */
	@Override
	protected ExecutionRunRunnable createRunRunnableFor(
			RunSchedulerThread runScheduler, Run run,
			ProgramConfig programConfig, DataConfig dataConfig,
			String runIdentString, boolean isResume,
			Map<ProgramParameter<?>, String> runParams) {
		RobustnessAnalysisRunRunnable r = new RobustnessAnalysisRunRunnable(
				runScheduler, run, programConfig, dataConfig, runIdentString,
				isResume, runParams);
		run.progress.addSubProgress(r.getProgressPrinter(), 10000);
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.ExecutionRun#createAndScheduleRunnableForResumePair(
	 * de.clusteval.framework.threading.RunSchedulerThread, int)
	 */
	@Override
	protected RunRunnable createAndScheduleRunnableForResumePair(
			RunSchedulerThread runScheduler, int p) {
		this.log.info(String.format("%s\t%s\t%s", this.runPairs.get(p)
				.getFirst(), this.runPairs.get(p).getSecond(),
				this.parameterValues.get(p)));
		return super.createAndScheduleRunnableForResumePair(runScheduler, p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.ExecutionRun#getRunParameterForRunPair(int)
	 */
	@Override
	protected Map<ProgramParameter<? extends Object>, String> getRunParameterForRunPair(
			int p) {
		// we have one parameter set for each run pair
		return this.parameterValues.get(p);
	}

	public void setOriginalDataConfigurations(final List<DataConfig> dataConfigs) {
		this.originalDataConfigs = dataConfigs;
	}
}
