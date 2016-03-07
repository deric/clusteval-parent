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
package de.clusteval.run.result;

import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualitySet;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.DataSetNotFoundException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.NoDataSetException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.GoldStandardConfigNotFoundException;
import de.clusteval.data.goldstandard.GoldStandardConfigurationException;
import de.clusteval.data.goldstandard.GoldStandardNotFoundException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.db.DatabaseConnectException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.program.NoOptimizableProgramParameterException;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.UnknownParameterType;
import de.clusteval.program.UnknownProgramParameterException;
import de.clusteval.program.UnknownProgramTypeException;
import de.clusteval.program.r.UnknownRProgramException;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.Run;
import de.clusteval.run.RunException;
import de.clusteval.run.result.format.UnknownRunResultFormatException;
import de.clusteval.run.result.postprocessing.UnknownRunResultPostprocessorException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.InvalidConfigurationFileException;
import de.wiwie.wiutils.file.FileUtils;
import de.wiwie.wiutils.utils.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;

/**
 * A wrapper class for parameter optimization runresults produced by parameter
 * optimization runs.
 *
 * @author Christian Wiwie
 *
 */

public class ParameterOptimizationResult extends ExecutionRunResult
		implements
			Iterable<Pair<ParameterSet, ClusteringQualitySet>> {

	/**
	 * @param parentRepository
	 * @param runResultFolder
	 * @param result
	 * @param parseClusterings
	 * @param storeClusterings
	 * @param register
	 *            A boolean indicating whether to register the parsed runresult.
	 * @throws IOException
	 * @throws UnknownRunResultFormatException
	 * @throws UnknownDataSetFormatException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws InvalidRunModeException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownProgramParameterException
	 * @throws UnknownGoldStandardFormatException
	 * @throws InvalidConfigurationFileException
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws NoRepositoryFoundException
	 * @throws GoldStandardNotFoundException
	 * @throws InvalidOptimizationParameterException
	 * @throws GoldStandardConfigurationException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws RunException
	 * @throws UnknownDataStatisticException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownRProgramException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownDistanceMeasureException
	 * @throws UnknownRunStatisticException
	 * @return The parameter optimization run result parsed from the given
	 *         runresult folder.
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws ConfigurationException
	 * @throws RegisterException
	 * @throws UnknownDataSetTypeException
	 * @throws NoDataSetException
	 * @throws NumberFormatException
	 * @throws UnknownRunDataStatisticException
	 * @throws RunResultParseException
	 * @throws UnknownDataPreprocessorException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownContextException
	 * @throws IncompatibleContextException
	 * @throws UnknownParameterType
	 * @throws InterruptedException
	 * @throws UnknownRunResultPostprocessorException
	 * @throws UnknownDataRandomizerException
	 */
    public static Run parseFromRunResultFolder2(final Repository parentRepository, final File runResultFolder,
         			final List<ParameterOptimizationResult> result, final boolean parseClusterings,
			final boolean storeClusterings, final boolean register) throws IOException, UnknownRunResultFormatException,
					UnknownDataSetFormatException, UnknownClusteringQualityMeasureException, InvalidRunModeException,
					UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
					UnknownProgramParameterException, UnknownGoldStandardFormatException,
					InvalidConfigurationFileException, RepositoryAlreadyExistsException, InvalidRepositoryException,
					NoRepositoryFoundException, GoldStandardNotFoundException, InvalidOptimizationParameterException,
					GoldStandardConfigurationException, DataSetConfigurationException, DataSetNotFoundException,
					DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
					DataConfigNotFoundException, RunException, UnknownDataStatisticException,
					UnknownProgramTypeException, UnknownRProgramException,
					IncompatibleParameterOptimizationMethodException, UnknownDistanceMeasureException,
					UnknownRunStatisticException, RepositoryConfigNotFoundException, RepositoryConfigurationException,
					ConfigurationException, RegisterException, UnknownDataSetTypeException, NumberFormatException,
					NoDataSetException, UnknownRunDataStatisticException, RunResultParseException,
					UnknownDataPreprocessorException, IncompatibleDataSetConfigPreprocessorException,
					UnknownContextException, IncompatibleContextException, UnknownParameterType, InterruptedException,
					UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		try {
			Repository childRepository = new RunResultRepository(runResultFolder.getAbsolutePath(), parentRepository);
			childRepository.initialize();
			try {

				File runFile = null;
				File configFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "configs"));
				if (!configFolder.exists())
					return null;
				for (File child : configFolder.listFiles())
					if (child.getName().endsWith(".run")) {
						runFile = child;
						break;
					}
				if (runFile == null)
					return null;
				final Run run = Parser.parseRunFromFile(runFile);

				if (run instanceof ParameterOptimizationRun) {
					final ParameterOptimizationRun paramRun = (ParameterOptimizationRun) run;

					// TODO
					// List<ParameterOptimizationResult> result = new
					// ArrayList<ParameterOptimizationResult>();

					File clusterFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "clusters"));
					for (final ParameterOptimizationMethod method : paramRun.getOptimizationMethods()) {
						final File completeFile = new File(FileUtils.buildPath(clusterFolder.getAbsolutePath(),
								method.getProgramConfig().toString() + "_" + method.getDataConfig().toString()
										+ ".results.qual.complete"));
						final ParameterOptimizationResult tmpResult = parseFromRunResultCompleteFile(parentRepository,
								paramRun, method, completeFile, parseClusterings, storeClusterings, register);
						if (tmpResult != null)
							result.add(tmpResult);

					}
					// try to change 17.07.2012 to fix for
					// internal_parameter-Optimization
					// for (Pair<ProgramConfig, DataConfig> pair :
					// run.getRunPairs())
					// {
					// final File completeFile = new File(FileUtils.buildPath(
					// clusterFolder.getAbsolutePath(),
					// pair.getFirst().toString()
					// + "_" + pair.getSecond().toString()
					// + ".results.qual.complete"));
					// final ParameterOptimizationResult tmpResult =
					// parseFromRunResultCompleteFile(
					// parentRepository, run, method, completeFile);
					// if (tmpResult != null)
					// result.add(tmpResult);
					//
					// }
				}
				return run;
			} finally {
				childRepository.terminateSupervisorThread();
			}
		} catch (DatabaseConnectException e) {
			// cannot happen
			return null;
		}
	}

	/**
	 * @param repository
	 * @param run
	 * @param method
	 * @param completeFile
	 * @param parseClusterings
	 * @param storeClusterings
	 * @param register
	 *            A boolean indicating whether to register the parsed runresult.
	 * @return The parameter optimization run result parsed from the given
	 *         runresult folder.
	 * @throws RegisterException
	 * @throws RunResultParseException
	 */
    public static ParameterOptimizationResult parseFromRunResultCompleteFile(final Repository repository,
         			ParameterOptimizationRun run, ParameterOptimizationMethod method, File completeFile,
			final boolean parseClusterings, final boolean storeClusterings, final boolean register)
					throws RegisterException, RunResultParseException {
		ParameterOptimizationResult result = null;
		if (completeFile.exists()) {
			result = new ParameterOptimizationResult(repository, false, completeFile.lastModified(), completeFile,
					completeFile.getParentFile().getParentFile().getName(), run, method, parseClusterings,
					storeClusterings);

			if (register) {
				result.loadIntoMemory();
				try {
					result.register();
				} finally {
					result.unloadFromMemory();
				}
			}
		}
		return result;
	}

	/*
	 * Belongs to a optimization method
	 */
	protected ParameterOptimizationMethod method;

	protected Map<ParameterSet, ClusteringQualitySet> parameterSetToQualities;

	// added 20.08.2012
	protected Map<ParameterSet, Clustering> parameterSetToClustering;

	// added 04.04.2013
	protected Map<ParameterSet, Long> parameterSetToIterationNumber;

	/*
	 * We keep track of the optimal parameter sets
	 */
	protected Map<ClusteringQualityMeasure, ParameterSet> optimalParameterSet;

	protected ClusteringQualitySet optimalCriterionValue;

	protected Map<ClusteringQualityMeasure, Clustering> optimalClustering;

	protected List<ParameterSet> parameterSets;

	protected List<Long> iterationNumbers;

	protected boolean parseClusterings, storeClusterings;

	/**
	 * By default we do not parse clusterings.
	 *
	 * @param repository
	 * @param changeDate
	 * @param absPath
	 * @param runIdentString
	 * @param run
	 * @param method
	 * @throws RegisterException
	 *
	 */
	public ParameterOptimizationResult(final Repository repository, final long changeDate, final File absPath,
			final String runIdentString, final Run run, final ParameterOptimizationMethod method)
					throws RegisterException {
		this(repository, false, changeDate, absPath, runIdentString, run, method, false, false);
	}

	/**
	 * Use this constructor if you want to parse clusterings as well. They will
	 * be stored in a map from parameter sets to the clusterings.
	 *
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @param runIdentString
	 * @param run
	 * @param method
	 * @param parseClusterings
	 *            Whether to parse the clusterings from the file system.
	 * @param storeClusterings
	 *            Whether to store parsed clusterings in RAM.
	 * @throws RegisterException
	 */
	public ParameterOptimizationResult(final Repository repository, final boolean register, final long changeDate,
			final File absPath, final String runIdentString, final Run run, final ParameterOptimizationMethod method,
			final boolean parseClusterings, final boolean storeClusterings) throws RegisterException {
		super(repository, changeDate, absPath, runIdentString, run, method.getDataConfig(), method.getProgramConfig());
		this.method = method;
		this.parseClusterings = parseClusterings;
		this.storeClusterings = storeClusterings;

		this.initAttributes();

		if (register)
			this.register();
	}

	protected void initAttributes() {
		this.parameterSetToQualities = new HashMap<ParameterSet, ClusteringQualitySet>();
		this.optimalCriterionValue = new ClusteringQualitySet();
		this.optimalParameterSet = new HashMap<ClusteringQualityMeasure, ParameterSet>();
		this.parameterSets = new ArrayList<ParameterSet>();
		this.iterationNumbers = new ArrayList<Long>();
		this.parameterSetToIterationNumber = new HashMap<ParameterSet, Long>();
		this.parameterSetToClustering = new HashMap<ParameterSet, Clustering>();
		this.optimalClustering = new HashMap<ClusteringQualityMeasure, Clustering>();
	}

	/**
	 * The copy constructor of run results.
	 *
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public ParameterOptimizationResult(final ParameterOptimizationResult other) throws RegisterException {
		super(other);

		this.method = other.method.clone();
		this.parameterSetToQualities = cloneParameterSetQualities(other.parameterSetToQualities);
		this.optimalCriterionValue = other.optimalCriterionValue.clone();
		this.optimalParameterSet = cloneOptimalParameterSets(other.optimalParameterSet);
		this.parameterSets = cloneParameterSets(other.parameterSets);
		this.iterationNumbers = cloneIterationNumbers(other.iterationNumbers);
		this.parameterSetToIterationNumber = cloneParameterSetToIterationNumbers(other.parameterSetToIterationNumber);
		this.parameterSetToClustering = cloneParameterSetsToClustering(other.parameterSetToClustering);
		this.optimalClustering = cloneOptimalClustering(other.optimalClustering);
	}

	private Map<ClusteringQualityMeasure, Clustering> cloneOptimalClustering(
			Map<ClusteringQualityMeasure, Clustering> optimalClustering) {
		final Map<ClusteringQualityMeasure, Clustering> result = new HashMap<ClusteringQualityMeasure, Clustering>();

		for (Map.Entry<ClusteringQualityMeasure, Clustering> entry : optimalClustering.entrySet()) {
			result.put(entry.getKey().clone(), entry.getValue().clone());
		}

		return result;
	}

	private Map<ParameterSet, Clustering> cloneParameterSetsToClustering(
			Map<ParameterSet, Clustering> parameterSetToClustering) {
		final Map<ParameterSet, Clustering> result = new HashMap<ParameterSet, Clustering>();

		for (Map.Entry<ParameterSet, Clustering> entry : parameterSetToClustering.entrySet()) {
			result.put(entry.getKey().clone(), entry.getValue().clone());
		}

		return result;
	}

	private List<ParameterSet> cloneParameterSets(List<ParameterSet> parameterSets) {
		final List<ParameterSet> result = new ArrayList<ParameterSet>();

		for (ParameterSet paramSet : parameterSets) {
			result.add(paramSet.clone());
		}

		return result;
	}

	private List<Long> cloneIterationNumbers(List<Long> iterationNumbers) {
		return new ArrayList<Long>(iterationNumbers);
	}

	private Map<ParameterSet, Long> cloneParameterSetToIterationNumbers(Map<ParameterSet, Long> iterationNumbers) {
		final Map<ParameterSet, Long> result = new HashMap<ParameterSet, Long>();

		for (Map.Entry<ParameterSet, Long> entry : iterationNumbers.entrySet()) {
			result.put(entry.getKey().clone(), entry.getValue());
		}

		return result;
	}

	private Map<ClusteringQualityMeasure, ParameterSet> cloneOptimalParameterSets(
			Map<ClusteringQualityMeasure, ParameterSet> optimalParameterSet) {
		final Map<ClusteringQualityMeasure, ParameterSet> result = new HashMap<ClusteringQualityMeasure, ParameterSet>();

		for (Map.Entry<ClusteringQualityMeasure, ParameterSet> entry : optimalParameterSet.entrySet()) {
			result.put(entry.getKey().clone(), entry.getValue().clone());
		}

		return result;
	}

	private Map<ParameterSet, ClusteringQualitySet> cloneParameterSetQualities(
			Map<ParameterSet, ClusteringQualitySet> parameterSetToQualities) {
		final Map<ParameterSet, ClusteringQualitySet> result = new HashMap<ParameterSet, ClusteringQualitySet>();

		for (Map.Entry<ParameterSet, ClusteringQualitySet> entry : parameterSetToQualities.entrySet()) {
			result.put(entry.getKey().clone(), entry.getValue().clone());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see run.result.RunResult#clone()
	 */
	@Override
	public ParameterOptimizationResult clone() {
		try {
			return new ParameterOptimizationResult(this);
		} catch (RegisterException e) {
			// should not occur
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * A convenience method for
	 * {@link #put(long, ParameterSet, ClusteringQualitySet, Clustering)}.
	 *
	 * @param iterationNumber
	 *            The number of the iteration.
	 *
	 * @param last
	 *            The parameter set for which we want to add clustering
	 *            qualities.
	 * @param qualities
	 *            The qualities which we want to add for the parameter set.
	 * @return The old value, if this operation replaced an old mapping,
	 */
	// public ClusteringQualitySet put(long iterationNumber, ParameterSet last,
	// ClusteringQualitySet qualities) {
	// return this.put(iterationNumber, last, qualities, null);
	// }

	/**
	 * This method adds the given qualities for the given parameter set and
	 * resulting clustering.
	 *
	 * @param iterationNumber
	 *            The number of the iteration.
	 *
	 * @param last
	 *            The parameter set for which we want to add clustering
	 *            qualities.
	 * @param qualities
	 *            The qualities which we want to add for the parameter set.
	 * @param clustering
	 *            The clustering resulting the given parameter set.
	 * @return The old value, if this operation replaced an old mapping,
	 */
	public ClusteringQualitySet put(long iterationNumber, ParameterSet last, ClusteringQualitySet qualities,
			Clustering clustering) {
		// we want that this result is parsed again
		changedSinceLastRegister = true;

		ClusteringQualitySet result = this.parameterSetToQualities.put(last, qualities);

		if (this.parameterSetToClustering != null)
			this.parameterSetToClustering.put(last, clustering);

		if (this.parameterSetToIterationNumber != null)
			this.parameterSetToIterationNumber.put(last, iterationNumber);

		if (qualities != null) {
			for (ClusteringQualityMeasure measure : qualities.keySet()) {
				if (optimalCriterionValue.get(measure) == null
						|| measure.isBetterThan(qualities.get(measure), this.optimalCriterionValue.get(measure))) {

					this.optimalCriterionValue.put(measure, qualities.get(measure));
					this.optimalParameterSet.put(measure, last);

					if (this.parameterSetToClustering != null)
						this.optimalClustering.put(measure, clustering);
				}
			}
		}

		return result;
	}

	/**
	 * @return The parameter set which lead to the highest clustering quality of
	 *         the optimization criterion.
	 */
	public ParameterSet getOptimalParameterSet() {
		if (this.optimalParameterSet != null)
			return this.optimalParameterSet.get(this.method.getOptimizationCriterion());
		return null;
	}

	/**
	 * @return A map with the optimal parameter sets for every clustering
	 *         quality measure.
	 */
	public Map<ClusteringQualityMeasure, ParameterSet> getOptimalParameterSets() {
		return this.optimalParameterSet;
	}

	/**
	 * @return The optimal quality value achieved for the optimization
	 *         criterion.
	 */
	public ClusteringQualitySet getOptimalCriterionValue() {
		return this.optimalCriterionValue;
	}

	/**
	 * @return The clustering corresponding to the highest achieved quality
	 *         value for the optimization criterion (see
	 *         {@link #getOptimalCriterionValue()}).
	 */
	public Clustering getOptimalClustering() {
		if (this.optimalClustering != null)
			return this.optimalClustering.get(this.method.getOptimizationCriterion());
		return null;
	}

	/**
	 * @return A map with all clustering quality measures together with the
	 *         clusterings which achieved the highest quality values for each of
	 *         those.
	 */
	public Map<ClusteringQualityMeasure, Clustering> getOptimalClusterings() {
		return this.optimalClustering;
	}

	/**
	 * @return A list of pairs containing all parameter sets evaluated during
	 *         the optimization process together with the optimal resulting
	 *         quality sets.
	 */
	public List<Pair<ParameterSet, ClusteringQualitySet>> getOptimizationQualities() {
		List<Pair<ParameterSet, ClusteringQualitySet>> result = new ArrayList<Pair<ParameterSet, ClusteringQualitySet>>();
		for (ParameterSet paramSet : this.parameterSets)
			result.add(Pair.getPair(paramSet, this.parameterSetToQualities.get(paramSet)));
		return result;
	}

	/**
	 * @return A list with all evaluated parameter sets of this optimization
	 *         process.
	 */
	public List<ParameterSet> getParameterSets() {
		return this.parameterSets;
	}

	/**
	 * @return A list with all evaluated parameter sets of this optimization
	 *         process.
	 */
	public List<Long> getIterationNumbers() {
		return this.iterationNumbers;
	}

	public long getIterationNumberForParameterSet(final ParameterSet parameterSet) {
		try {
			if (this.parameterSetToIterationNumber.containsKey(parameterSet))
				return this.parameterSetToIterationNumber.get(parameterSet);
			return -1l;
		} catch (NullPointerException e) {
			System.out.println("bla");
			throw e;
		}
	}

	/**
	 * @return A list of pairs containing all parameter sets evaluated during
	 *         the optimization process together with the optimal resulting
	 *         clusterings.
	 */
	public List<Pair<ParameterSet, Clustering>> getOptimizationClusterings() {
		List<Pair<ParameterSet, Clustering>> result = new ArrayList<Pair<ParameterSet, Clustering>>();
		for (ParameterSet paramSet : this.parameterSets)
			result.add(Pair.getPair(paramSet, this.parameterSetToClustering.get(paramSet)));
		return result;
	}

	/**
	 * @return The parameter optimization method which created this result.
	 */
	public ParameterOptimizationMethod getMethod() {
		return this.method;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.clusteval.run.result.RunResult#getRun()
	 */
	@Override
	public ParameterOptimizationRun getRun() {
		return (ParameterOptimizationRun) super.getRun();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.clusteval.run.result.RunResult#isInMemory()
	 */
	@Override
	public boolean isInMemory() {
		return this.iterationNumbers != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.clusteval.run.result.RunResult#loadIntoMemory()
	 */
	@Override
	public void loadIntoMemory() throws RunResultParseException {
		this.initAttributes();
		// the parser fills the attributes of this run result object
		ParameterOptimizationResultParser parser;
		try {
			parser = new ParameterOptimizationResultParser(method, this.getRun(), this, absPath.getAbsolutePath(),
					new int[]{}, new int[]{}, parseClusterings, storeClusterings);
			parser.process();
		} catch (Exception e) {
			throw new RunResultParseException(e.getMessage());
		}
	}

	/**
	 * This method clears all internal attributes that do not store the optimal
	 * results (those might be needed afterwards). This includes
	 * {@link #parameterSets}, {@link #parameterSetToClustering} and
	 * {@link #parameterSetToQualities}.
	 */
	@Override
	public void unloadFromMemory() {
		if (this.iterationNumbers != null) {
			this.iterationNumbers.clear();
			this.iterationNumbers = null;
		}
		if (this.optimalClustering != null) {
			this.optimalClustering.clear();
			this.optimalClustering = null;
		}
		if (this.optimalCriterionValue != null) {
			this.optimalCriterionValue.clear();
			this.optimalCriterionValue = null;
		}
		if (this.optimalParameterSet != null) {
			this.optimalParameterSet.clear();
			this.optimalParameterSet = null;
		}
		if (this.parameterSets != null) {
			this.parameterSets.clear();
			this.parameterSets = null;
		}
		if (this.iterationNumbers != null) {
			this.iterationNumbers.clear();
			this.iterationNumbers = null;
		}
		if (this.parameterSetToIterationNumber != null) {
			this.parameterSetToIterationNumber.clear();
			this.parameterSetToIterationNumber = null;
		}
		if (this.parameterSetToClustering != null) {
			this.parameterSetToClustering.clear();
			this.parameterSetToClustering = null;
		}
		if (this.parameterSetToQualities != null) {
			this.parameterSetToQualities.clear();
			this.parameterSetToQualities = null;
		}
	}

	/**
	 * @param paramSet
	 *            The parameter set for which we want the resulting clustering
	 *            quality set.
	 * @return The clustering quality set resulting from the given parameter
	 *         set.
	 */
	public ClusteringQualitySet get(final ParameterSet paramSet) {
		return this.parameterSetToQualities.get(paramSet);
	}

	/**
	 *
	 * @param paramSet
	 *            The parameter set for which we want to know the resulting
	 *            clustering.
	 * @return The clustering resulting from the given parameter set.
	 */
	public Clustering getClustering(final ParameterSet paramSet) {
		return this.parameterSetToClustering.get(paramSet);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getAbsolutePath();
	}

	/**
	 * @param run
	 *            The run corresponding to the runresult folder.
	 * @param repository
	 *            The repository in which we want to register the runresult.
	 * @param runResultFolder
	 *            A file object referencing the runresult folder.
	 * @param result
	 *            The list of runresults this method fills.
	 * @param parseClusterings
	 *            Whether to parse clusterings.
	 * @param storeClusterings
	 *            Whether to store clusterings, if they are parsed.
	 * @param register
	 *            A boolean indicating whether to register the parsed runresult.
	 * @return The parameter optimization run parsed from the runresult folder.
	 * @throws RegisterException
	 * @throws RunResultParseException
	 */
	public static Run parseFromRunResultFolder(final ParameterOptimizationRun run, final Repository repository,
			final File runResultFolder, final List<RunResult> result, final boolean parseClusterings,
			final boolean storeClusterings, final boolean register) throws RegisterException, RunResultParseException {

		File clusterFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "clusters"));
		for (final ParameterOptimizationMethod method : run.getOptimizationMethods()) {
			final File completeFile = new File(
					FileUtils.buildPath(clusterFolder.getAbsolutePath(), method.getProgramConfig().toString() + "_"
							+ method.getDataConfig().toString() + ".results.qual.complete"));
			final ParameterOptimizationResult tmpResult = parseFromRunResultCompleteFile(repository, run, method,
					completeFile, parseClusterings, storeClusterings, register);
			if (tmpResult != null)
				result.add(tmpResult);

		}
		return run;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Pair<ParameterSet, ClusteringQualitySet>> iterator() {
		return new ParameterOptimizationResultIterator(this);
	}
}

class ParameterOptimizationResultIterator implements Iterator<Pair<ParameterSet, ClusteringQualitySet>> {

	protected ParameterOptimizationResult result;

	protected int currPos;

	public ParameterOptimizationResultIterator(final ParameterOptimizationResult result) {
		super();
		this.result = result;
		this.currPos = 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		/*
		 * Number of iterations
		 */
		return this.currPos < result.getParameterSets().size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Pair<ParameterSet, ClusteringQualitySet> next() {
		ParameterSet set = result.getParameterSets().get(currPos++);
		return Pair.getPair(set, result.get(set));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// not supported
	}

}
