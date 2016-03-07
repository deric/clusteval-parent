/**
 * 
 */
package de.clusteval.framework.repository.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualityMeasureParameters;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.Context;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.AbsoluteDataSet;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetAttributeParser;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.DataSetNotFoundException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.NoDataSetException;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.data.dataset.RunResultDataSetConfig;
import de.clusteval.data.dataset.format.AbsoluteDataSetFormat;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.RelativeDataSetFormat;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.DataSetType;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.goldstandard.GoldStandardConfigNotFoundException;
import de.clusteval.data.goldstandard.GoldStandardConfigurationException;
import de.clusteval.data.goldstandard.GoldStandardNotFoundException;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.DataRandomizer;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.program.NoOptimizableProgramParameterException;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.Program;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.program.StandaloneProgram;
import de.clusteval.program.UnknownParameterType;
import de.clusteval.program.UnknownProgramParameterException;
import de.clusteval.program.UnknownProgramTypeException;
import de.clusteval.program.r.RProgram;
import de.clusteval.program.r.RProgramConfig;
import de.clusteval.program.r.UnknownRProgramException;
import de.clusteval.run.AnalysisRun;
import de.clusteval.run.ClusteringRun;
import de.clusteval.run.DataAnalysisRun;
import de.clusteval.run.ExecutionRun;
import de.clusteval.run.InternalParameterOptimizationRun;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.RobustnessAnalysisRun;
import de.clusteval.run.Run;
import de.clusteval.run.RunAnalysisRun;
import de.clusteval.run.RunDataAnalysisRun;
import de.clusteval.run.RunException;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.run.result.format.UnknownRunResultFormatException;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.result.postprocessing.RunResultPostprocessorParameters;
import de.clusteval.run.result.postprocessing.UnknownRunResultPostprocessorException;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.wiwie.wiutils.file.FileUtils;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class Parser<P extends RepositoryObject> {

	@SuppressWarnings("unchecked")
	protected static <T extends RepositoryObject> Parser<T> getParserForClass(final Class<T> c) {
		if (c.equals(ClusteringRun.class))
			return (Parser<T>) new ClusteringRunParser();
		else if (c.equals(ParameterOptimizationRun.class))
			return (Parser<T>) new ParameterOptimizationRunParser();
		else if (c.equals(InternalParameterOptimizationRun.class))
			return (Parser<T>) new InternalParameterOptimizationRunParser();
		else if (c.equals(DataAnalysisRun.class))
			return (Parser<T>) new DataAnalysisRunParser();
		else if (c.equals(RunAnalysisRun.class))
			return (Parser<T>) new RunAnalysisRunParser();
		else if (c.equals(RunDataAnalysisRun.class))
			return (Parser<T>) new RunDataAnalysisRunParser();
		else if (c.equals(RobustnessAnalysisRun.class))
			return (Parser<T>) new RobustnessAnalysisRunParser();
		else if (c.equals(DataSetConfig.class))
			return (Parser<T>) new DataSetConfigParser();
		else if (c.equals(RunResultDataSetConfig.class))
			return (Parser<T>) new RunResultDataSetConfigParser();
		else if (c.equals(DataSet.class))
			return (Parser<T>) new DataSetParser();
		else if (c.equals(GoldStandardConfig.class))
			return (Parser<T>) new GoldStandardConfigParser();
		else if (c.equals(ProgramConfig.class))
			return (Parser<T>) new ProgramConfigParser();
		else if (c.equals(Run.class))
			return (Parser<T>) new RunParser<Run>();
		else if (c.equals(DataConfig.class))
			return (Parser<T>) new DataConfigParser();
		return null;
	}

	public static <T extends RepositoryObject> T parseFromFile(final Class<T> c, final File absPath)
			throws UnknownDataSetFormatException, GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException, ConfigurationException, UnknownContextException,
			FileNotFoundException, RegisterException, UnknownParameterType, NoRepositoryFoundException,
			UnknownClusteringQualityMeasureException, RunException, IncompatibleContextException,
			UnknownRunResultFormatException, InvalidOptimizationParameterException, UnknownProgramParameterException,
			UnknownProgramTypeException, UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		Parser<T> parser = getParserForClass(c);
		parser.parseFromFile(absPath);
		return parser.getResult();
	}

	public static Run parseRunFromFile(final File file) throws UnknownDataSetFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException, FileNotFoundException, RegisterException,
			UnknownParameterType, NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		String runMode = Parser.getModeOfRun(file);
		if (runMode.equals("clustering")) {
			return Parser.parseFromFile(ClusteringRun.class, file);
		} else if (runMode.equals("parameter_optimization")) {
			return Parser.parseFromFile(ParameterOptimizationRun.class, file);
		} else if (runMode.equals("internal_parameter_optimization")) {
			return Parser.parseFromFile(InternalParameterOptimizationRun.class, file);
		} else if (runMode.equals("dataAnalysis")) {
			return Parser.parseFromFile(DataAnalysisRun.class, file);
		} else if (runMode.equals("runAnalysis")) {
			return Parser.parseFromFile(RunAnalysisRun.class, file);
		} else if (runMode.equals("runDataAnalysis")) {
			return Parser.parseFromFile(RunDataAnalysisRun.class, file);
		} else if (runMode.equals("robustnessAnalysis")) {
			return Parser.parseFromFile(RobustnessAnalysisRun.class, file);
		}
		return null;
	}

	protected static String getModeOfRun(final File absPath) throws UnknownDataSetFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException, FileNotFoundException, RegisterException,
			UnknownParameterType, NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		RunParser<? extends Run> p = (RunParser<Run>) getParserForClass(Run.class);
		p.parseFromFile(absPath);
		return p.mode;
	}

	protected P result;

	public abstract void parseFromFile(final File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException;

	public P getResult() {
		return this.result;
	}
}

class AnalysisRunParser<T extends AnalysisRun<?>> extends RunParser<T> {
}

class ClusteringRunParser extends ExecutionRunParser<ClusteringRun> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.ExecutionRunParser#parseFromFile(java
	 * .io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws ConfigurationException, UnknownContextException,
			NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		result = new ClusteringRun(repo, context, changeDate, absPath, programConfigs, dataConfigs, qualityMeasures,
				runParamValues, postprocessor, maxExecutionTimes);
		result = repo.getRegisteredObject(result, false);
	}
}

class RobustnessAnalysisRunParser extends ExecutionRunParser<RobustnessAnalysisRun> {

	protected List<String> uniqueRunIdentifiers;
	protected List<DataConfig> originalDataConfigs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.ExecutionRunParser#parseFromFile(java
	 * .io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws ConfigurationException, UnknownContextException,
			NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		String[] list = getProps().getStringArray("uniqueRunIdentifiers");
		if (list.length == 0)
			throw new RunException("At least one run result identifier must be specified");
		// 10.07.2014: remove duplicates.
		list = new ArrayList<String>(new HashSet<String>(Arrays.asList(list))).toArray(new String[0]);
		this.uniqueRunIdentifiers = Arrays.asList(list);

		String randomizerS = getProps().getString("randomizer");
		DataRandomizer randomizer;
		if (this.repo instanceof RunResultRepository)
			randomizer = DataRandomizer.parseFromString(this.repo.getParent(), randomizerS);
		else
			randomizer = DataRandomizer.parseFromString(this.repo, randomizerS);
		int numberOfRandomizedDataSets = getProps().getInt("numberOfRandomizedDataSets");

		// get randomizer parameter sets
		List<ParameterSet> paramSets = new ArrayList<ParameterSet>();
		int c = 1;
		while (getProps().getSections().contains(randomizerS + "_" + c)) {
			ParameterSet paramSet = new ParameterSet();
			Iterator<String> parameters = getProps().getKeys(randomizerS + "_" + c);
			while (parameters.hasNext()) {
				String param = parameters.next();
				String value = getProps().getString(param);

				paramSet.put(param.replace(randomizerS + "_" + c + ".", ""), value);
			}
			c++;
			paramSets.add(paramSet);
		}

		result = new RobustnessAnalysisRun(repo, context, changeDate, absPath, uniqueRunIdentifiers, programConfigs,
				dataConfigs, originalDataConfigs, qualityMeasures, runParamValues, postprocessor, randomizer, paramSets,
				numberOfRandomizedDataSets, maxExecutionTimes);
		result = repo.getRegisteredObject(result, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.parse.ExecutionRunParser#
	 * parseDataConfigurations()
	 */
	@Override
	protected void parseDataConfigurations() throws RunException, UnknownDataSetFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			RegisterException, NoRepositoryFoundException, UnknownDistanceMeasureException, UnknownDataSetTypeException,
			UnknownDataPreprocessorException, IncompatibleDataSetConfigPreprocessorException, ConfigurationException,
			UnknownContextException, FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException, UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {

		if (this.repo instanceof RunResultRepository) {
			this.originalDataConfigs = new ArrayList<DataConfig>();
			// get the original data configs
			String[] list = getProps().getStringArray("dataConfig");
			if (list.length == 0)
				throw new RunException("At least one data config must be specified");
			// 10.07.2014: remove duplicates.
			list = new ArrayList<String>(new HashSet<String>(Arrays.asList(list))).toArray(new String[0]);
			for (String dataConfig : list) {
				this.originalDataConfigs.add(repo.getParent().getStaticObjectWithName(DataConfig.class, dataConfig));
			}
			this.dataConfigs = new ArrayList<DataConfig>(this.originalDataConfigs);

			// class DataConfigFileExtFilter implements FilenameFilter {
			//
			// /**
			// *
			// */
			// public DataConfigFileExtFilter() {
			// }
			//
			// /*
			// * (non-Javadoc)
			// *
			// * @see java.io.FilenameFilter#accept(java.io.File,
			// * java.lang.String)
			// */
			// @Override
			// public boolean accept(File dir, String name) {
			// return name.endsWith(".dataconfig");
			// }
			// }
			//
			// List<DataConfig> randomizedDataConfigs = new
			// ArrayList<DataConfig>();
			// String[] dataConfigFiles = new File(
			// this.repo.getBasePath(DataConfig.class))
			// .list(new DataConfigFileExtFilter());
			// for (String dcFile : dataConfigFiles)
			// randomizedDataConfigs.add(this.repo.getStaticObjectWithName(
			// DataConfig.class, dcFile.replace(".dataconfig", "")));
			// this.dataConfigs = randomizedDataConfigs;
		} else {
			super.parseDataConfigurations();
			this.originalDataConfigs = new ArrayList<DataConfig>(this.dataConfigs);
		}
	}
}

class DataAnalysisRunParser extends AnalysisRunParser<DataAnalysisRun> {

	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		String[] list = getProps().getStringArray("dataConfig");
		if (list.length == 0)
			throw new RunException("At least one data config must be specified");

		/*
		 * An analysis run consists of a set of dataconfigs
		 */
		List<DataConfig> dataConfigs = new LinkedList<DataConfig>();

		List<DataStatistic> dataStatistics = new LinkedList<DataStatistic>();

		for (String dataConfig : list) {
			dataConfigs.add(repo.getRegisteredObject(Parser.parseFromFile(DataConfig.class,
					new File(FileUtils.buildPath(repo.getBasePath(DataConfig.class), dataConfig + ".dataconfig")))));
		}

		/**
		 * We catch the exceptions such that all statistics are tried to be
		 * loaded once so that they are ALL registered as missing in the
		 * repository.
		 */
		List<UnknownDataStatisticException> thrownExceptions = new ArrayList<UnknownDataStatisticException>();
		for (String dataStatistic : getProps().getStringArray("dataStatistics")) {
			try {
				dataStatistics.add(DataStatistic.parseFromString(repo, dataStatistic));
			} catch (UnknownDataStatisticException e) {
				thrownExceptions.add(e);
			}
		}
		if (thrownExceptions.size() > 0) {
			// just throw the first exception
			throw thrownExceptions.get(0);
		}

		result = new DataAnalysisRun(repo, context, changeDate, absPath, dataConfigs, dataStatistics);
		result = repo.getRegisteredObject(result, false);
	};
}

class DataConfigParser extends RepositoryObjectParser<DataConfig> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
	 * (java.io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		log.debug("Parsing data config \"" + absPath + "\"");

		try {
			getProps().setThrowExceptionOnMissing(true);

			String datasetConfigName = getProps().getString("datasetConfig");
			DataSetConfig dataSetConfig;
			if (repo instanceof RunResultRepository)
				dataSetConfig = Parser.parseFromFile(RunResultDataSetConfig.class, new File(
						FileUtils.buildPath(repo.getBasePath(DataSetConfig.class), datasetConfigName + ".dsconfig")));
			else
				dataSetConfig = Parser.parseFromFile(DataSetConfig.class, new File(
						FileUtils.buildPath(repo.getBasePath(DataSetConfig.class), datasetConfigName + ".dsconfig")));

			GoldStandardConfig goldStandardConfig = null;
			try {
				String gsConfigName = getProps().getString("goldstandardConfig");
				goldStandardConfig = Parser.parseFromFile(GoldStandardConfig.class, new File(
						FileUtils.buildPath(repo.getBasePath(GoldStandardConfig.class), gsConfigName + ".gsconfig")));
			} catch (NoSuchElementException e) {
				// No goldstandard config given
			}

			result = new DataConfig(repo, changeDate, absPath, dataSetConfig, goldStandardConfig);
			result = repo.getRegisteredObject(result);
		} catch (NoSuchElementException e) {
			throw new DataConfigurationException(e);
		}
	}
}

class DataSetConfigParser extends RepositoryObjectParser<DataSetConfig> {

	protected String datasetName;
	protected String datasetFile;

	protected DataSet dataSet;
	protected ConversionInputToStandardConfiguration configInputToStandard;
	protected ConversionStandardToInputConfiguration configStandardToInput;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
	 * (java.io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		log.debug("Parsing dataset config \"" + absPath + "\"");

		try {
			getProps().setThrowExceptionOnMissing(true);

			datasetName = getProps().getString("datasetName");
			datasetFile = getProps().getString("datasetFile");

			DistanceMeasure distanceMeasure;
			if (getProps().containsKey("distanceMeasureAbsoluteToRelative")) {
				distanceMeasure = DistanceMeasure.parseFromString(repo,
						getProps().getString("distanceMeasureAbsoluteToRelative"));
			} else
				distanceMeasure = DistanceMeasure.parseFromString(repo, "EuclidianDistanceMeasure");

			NUMBER_PRECISION similarityPrecision = NUMBER_PRECISION.DOUBLE;
			if (getProps().containsKey("similarityPrecision")) {
				String val = getProps().getString("similarityPrecision");
				if (val.equals("double"))
					similarityPrecision = NUMBER_PRECISION.DOUBLE;
				else if (val.equals("float"))
					similarityPrecision = NUMBER_PRECISION.FLOAT;
				else if (val.equals("short"))
					similarityPrecision = NUMBER_PRECISION.SHORT;
			}

			dataSet = this.getDataSet();

			// added 12.04.2013
			List<DataPreprocessor> preprocessorBeforeDistance;
			if (getProps().containsKey("preprocessorBeforeDistance")) {
				preprocessorBeforeDistance = DataPreprocessor.parseFromString(repo,
						getProps().getStringArray("preprocessorBeforeDistance"));

				for (DataPreprocessor proc : preprocessorBeforeDistance) {
					if (!proc.getCompatibleDataSetFormats()
							.contains(dataSet.getDataSetFormat().getClass().getSimpleName())) {
						throw new IncompatibleDataSetConfigPreprocessorException("The data preprocessor "
								+ proc.getClass().getSimpleName() + " cannot be applied to a dataset with format "
								+ dataSet.getDataSetFormat().getClass().getSimpleName());
					}
				}
			} else
				preprocessorBeforeDistance = new ArrayList<DataPreprocessor>();

			List<DataPreprocessor> preprocessorAfterDistance;
			if (getProps().containsKey("preprocessorAfterDistance")) {
				preprocessorAfterDistance = DataPreprocessor.parseFromString(repo,
						getProps().getStringArray("preprocessorAfterDistance"));
			} else
				preprocessorAfterDistance = new ArrayList<DataPreprocessor>();

			configInputToStandard = new ConversionInputToStandardConfiguration(distanceMeasure, similarityPrecision,
					preprocessorBeforeDistance, preprocessorAfterDistance);
			configStandardToInput = new ConversionStandardToInputConfiguration();

			result = new DataSetConfig(repo, changeDate, absPath, dataSet, configInputToStandard,
					configStandardToInput);
			result = repo.getRegisteredObject(result);
		} catch (NoSuchElementException e) {
			throw new DataSetConfigurationException(e);
		}
	}

	protected DataSet getDataSet()
			throws DataSetNotFoundException, UnknownDataSetFormatException, DataSetConfigurationException,
			NoDataSetException, NumberFormatException, RegisterException, NoRepositoryFoundException,
			UnknownDataSetTypeException, GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException, UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException, RunException, IncompatibleContextException,
			UnknownRunResultFormatException, InvalidOptimizationParameterException, UnknownProgramParameterException,
			UnknownProgramTypeException, UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException, IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException, UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException, UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		if (repo instanceof RunResultRepository)
			return repo.getStaticObjectWithName(DataSet.class, datasetName + "/" + datasetFile);
		return Parser.parseFromFile(DataSet.class,
				new File(FileUtils.buildPath(repo.getBasePath(DataSet.class), datasetName, datasetFile)));
	}
}

class DataSetParser extends RepositoryObjectParser<DataSet> {

	/**
	 * 
	 */
	public DataSetParser() {
		this.loadConfigFile = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
	 * (java.io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		try {
			Map<String, String> attributeValues = extractDataSetAttributes(absPath);

			if (attributeValues.size() == 0)
				throw new NoDataSetException("The file " + absPath + " does not contain a dataset header.");

			String alias;
			if (attributeValues.containsKey("alias"))
				alias = attributeValues.get("alias");
			else
				throw new DataSetConfigurationException("No alias specified for data set " + absPath.getAbsolutePath());
			// check whether the alias is already taken by another dataset ->
			// throw exception
			Collection<DataSet> dataSets;
			if (repo instanceof RunResultRepository)
				dataSets = repo.getParent().getCollectionStaticEntities(DataSet.class);
			else
				dataSets = repo.getCollectionStaticEntities(DataSet.class);

			for (DataSet ds : dataSets)
				if (!(repo instanceof RunResultRepository) && !(ds.getAbsolutePath().equals(absPath.getAbsolutePath()))
						&& ds.getAlias().equals(alias))
					throw new DataSetConfigurationException("The alias (" + alias + ") of the data set "
							+ absPath.getAbsolutePath() + " is already taken by the data set " + ds.getAbsolutePath());

			DataSetFormat dsFormat;
			if (attributeValues.containsKey("dataSetFormat")) {
				if (attributeValues.containsKey("dataSetFormatVersion"))
					dsFormat = DataSetFormat.parseFromString(repo, attributeValues.get("dataSetFormat"),
							Integer.valueOf(attributeValues.get("dataSetFormatVersion")).intValue());
				else
					dsFormat = DataSetFormat.parseFromString(repo, attributeValues.get("datasetFormat"));
			} else {
				throw new DataSetConfigurationException("No format specified for dataset " + absPath.getAbsolutePath());
			}

			DataSetType dsType;
			if (attributeValues.containsKey("dataSetType")) {
				dsType = DataSetType.parseFromString(repo, attributeValues.get("dataSetType"));
			} else {
				throw new DataSetConfigurationException("No type specified for dataset " + absPath.getAbsolutePath());
			}

			DataSet.WEBSITE_VISIBILITY websiteVisibility = DataSet.WEBSITE_VISIBILITY.HIDE;
			String vis = attributeValues.containsKey("websiteVisibility")
					? attributeValues.get("websiteVisibility")
					: "hide";
			if (vis.equals("hide"))
				websiteVisibility = DataSet.WEBSITE_VISIBILITY.HIDE;
			else if (vis.equals("show_always"))
				websiteVisibility = DataSet.WEBSITE_VISIBILITY.SHOW_ALWAYS;
			else if (vis.equals("show_optional"))
				websiteVisibility = DataSet.WEBSITE_VISIBILITY.SHOW_OPTIONAL;

			final long changeDate = absPath.lastModified();

			LoggerFactory.getLogger(DataSet.class).debug("Parsing dataset \"" + absPath + "\"");

			/*
			 * Either the format is absolute or relative
			 */
			if (RelativeDataSetFormat.class.isAssignableFrom(dsFormat.getClass()))
				result = new RelativeDataSet(repo, true, changeDate, absPath, alias, (RelativeDataSetFormat) dsFormat,
						dsType, websiteVisibility);
			else
				result = new AbsoluteDataSet(repo, true, changeDate, absPath, alias, (AbsoluteDataSetFormat) dsFormat,
						dsType, websiteVisibility);
			result = repo.getRegisteredObject(result);
			LoggerFactory.getLogger(DataSet.class).debug("Dataset parsed");
		} catch (IOException e) {
			throw new UnknownDataSetFormatException(e);
		}
	}

	/**
	 * This method parses the header of a dataset file. A header is required for
	 * a dataset file to be recognized by the framework as a valid dataset file.
	 * If the file does not contain any header lines, it is ignored by the
	 * framework. A header line is of the form '// attribute = value'. The
	 * header should contain several lines:
	 * 
	 * <p>
	 * The type of the dataset, e.g. '// dataSetType =
	 * GeneExpressionDataSetType'
	 * <p>
	 * The format of the dataset, e.g. '// dataSetFormat = RowSimDataSetFormat'
	 * <p>
	 * The version of the dataset format, e.g. '// dataSetFormatVersion = 1'
	 * 
	 * @param absPath
	 * @return
	 * @throws IOException
	 */
	protected static Map<String, String> extractDataSetAttributes(final File absPath) throws IOException {
		DataSetAttributeParser attributeParser = new DataSetAttributeParser(absPath.getAbsolutePath());
		attributeParser.process();
		Map<String, String> attributeValues = attributeParser.getAttributeValues();
		return attributeValues;
	}
}

class ExecutionRunParser<T extends ExecutionRun> extends RunParser<T> {

	protected List<ProgramConfig> programConfigs;
	protected List<DataConfig> dataConfigs;
	protected List<ClusteringQualityMeasure> qualityMeasures;
	protected List<Map<ProgramParameter<?>, String>> runParamValues;
	protected Map<ProgramParameter<?>, String> paramMap;
	protected List<RunResultPostprocessor> postprocessor;
	protected Map<String, Integer> maxExecutionTimes;

	@Override
	public void parseFromFile(final File absPath) throws ConfigurationException, UnknownContextException,
			NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		/*
		 * A run consists of a set of programconfigs and a set of dataconfigs,
		 * that are pairwise combined.
		 */
		programConfigs = new LinkedList<ProgramConfig>();
		dataConfigs = new LinkedList<DataConfig>();
		/*
		 * The quality measures that should be calculated for every pair of
		 * programconfig+dataconfig.
		 */
		qualityMeasures = new LinkedList<ClusteringQualityMeasure>();
		/*
		 * A list with parameter values that are set in the run config. They
		 * will overwrite the default values of the program config.
		 */
		runParamValues = new ArrayList<Map<ProgramParameter<?>, String>>();

		maxExecutionTimes = new HashMap<String, Integer>();

		parseProgramConfigurations();

		parseQualityMeasures();

		parseDataConfigurations();

		parsePostprocessor();

		ExecutionRun.checkCompatibilityQualityMeasuresDataConfigs(dataConfigs, qualityMeasures);
	}

	protected void parseProgramConfigurations()
			throws RunException, UnknownContextException, IncompatibleContextException, UnknownDataSetFormatException,
			ConfigurationException, FileNotFoundException, RegisterException, UnknownParameterType,
			UnknownRunResultFormatException, NoRepositoryFoundException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			NoOptimizableProgramParameterException, GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException, UnknownClusteringQualityMeasureException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException {

		String[] list = getProps().getStringArray("programConfig");
		if (list.length == 0)
			throw new RunException("At least one program config must be specified");
		// 10.07.2014: remove duplicates.
		list = new ArrayList<String>(new HashSet<String>(Arrays.asList(list))).toArray(new String[0]);
		for (String programConfig : list) {
			ProgramConfig newProgramConfig = Parser.parseFromFile(ProgramConfig.class,
					new File(FileUtils.buildPath(repo.getBasePath(ProgramConfig.class), programConfig + ".config")));

			if (!newProgramConfig.getProgram().getContext().equals(context))
				throw new IncompatibleContextException("Incompatible run context (" + context
						+ ") and program context (" + newProgramConfig.getProgram().getContext() + ")");

			newProgramConfig = repo.getRegisteredObject(newProgramConfig);
			programConfigs.add(newProgramConfig);

			/*
			 * parse the overriding parameter-values for this program config
			 */
			parseProgramConfigParams(newProgramConfig);
		}
	}

	protected boolean isParamConfigurationEntry(final String name) {
		return name != null;
	}

	protected boolean checkParamValueToMap(final String param) {
		return true;
	}

	protected void parseQualityMeasures()
			throws RunException, UnknownClusteringQualityMeasureException, ConfigurationException {

		if (getProps().getStringArray("qualityMeasures").length == 0)
			throw new RunException("At least one quality measure must be specified");
		/**
		 * We catch the exceptions such that all quality measures are tried to
		 * be loaded once so that they are ALL registered as missing in the
		 * repository.
		 */
		List<UnknownClusteringQualityMeasureException> thrownExceptions = new ArrayList<UnknownClusteringQualityMeasureException>();
		for (String qualityMeasure : getProps().getStringArray("qualityMeasures")) {
			try {
				// parse parameters for this quality measure
				ClusteringQualityMeasureParameters p = new ClusteringQualityMeasureParameters();
				if (getProps().getSections().contains(qualityMeasure)) {
					Iterator<String> parameters = getProps().getKeys(qualityMeasure);
					while (parameters.hasNext()) {
						String param = parameters.next();
						String value = getProps().getString(param);

						p.put(param, value);
					}
				}
				ClusteringQualityMeasure measure = ClusteringQualityMeasure.parseFromString(repo, qualityMeasure, p);

				qualityMeasures.add(measure);

			} catch (UnknownClusteringQualityMeasureException e) {
				thrownExceptions.add(e);
			}
		}
		if (thrownExceptions.size() > 0) {
			// just throw the first exception
			throw thrownExceptions.get(0);
		}
	}

	protected void parseDataConfigurations() throws RunException, UnknownDataSetFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			RegisterException, NoRepositoryFoundException, UnknownDistanceMeasureException, UnknownDataSetTypeException,
			UnknownDataPreprocessorException, IncompatibleDataSetConfigPreprocessorException, ConfigurationException,
			UnknownContextException, FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException, UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		String[] list = getProps().getStringArray("dataConfig");
		if (list.length == 0)
			throw new RunException("At least one data config must be specified");
		// 10.07.2014: remove duplicates.
		list = new ArrayList<String>(new HashSet<String>(Arrays.asList(list))).toArray(new String[0]);
		for (String dataConfig : list) {
			dataConfigs.add(repo.getRegisteredObject(Parser.parseFromFile(DataConfig.class,
					new File(FileUtils.buildPath(repo.getBasePath(DataConfig.class), dataConfig + ".dataconfig")))));
		}
	}

	protected void parseProgramConfigParams(final ProgramConfig programConfig)
			throws NoOptimizableProgramParameterException, UnknownProgramParameterException, RunException,
			ConfigurationException {

		paramMap = new HashMap<ProgramParameter<?>, String>();

		if (getProps().getSections().contains(programConfig.getName())) {
			/*
			 * General parameters, not only for optimization.
			 */
			Iterator<String> itParams = getProps().getSection(programConfig.getName()).getKeys();
			while (itParams.hasNext()) {
				String param = itParams.next();
				if (param.equals("maxExecutionTimeMinutes")) {
					this.maxExecutionTimes.put(programConfig.getName(),
							Integer.parseInt(getProps().getSection(programConfig.getName()).getString(param)));
				} else if (isParamConfigurationEntry(param))
					try {
						ProgramParameter<?> p = programConfig.getParamWithId(param);

						if (checkParamValueToMap(param))
							paramMap.put(p, getProps().getSection(programConfig.getName()).getString(param));
					} catch (UnknownProgramParameterException e) {
						log.error("The run " + absPath.getName() + " contained invalid parameter values: "
								+ programConfig.getProgram() + " does not have a parameter " + param);
					}
			}
		}
		runParamValues.add(paramMap);
	}

	protected void parsePostprocessor() throws UnknownRunResultPostprocessorException, ConfigurationException {

		postprocessor = new ArrayList<RunResultPostprocessor>();

		if (!getProps().containsKey("postprocessor"))
			return;

		String[] list = getProps().getStringArray("postprocessor");
		// 10.07.2014: remove duplicates.
		list = new ArrayList<String>(new HashSet<String>(Arrays.asList(list))).toArray(new String[0]);
		for (String postprocessor : list) {

			// parse parameters
			RunResultPostprocessorParameters params = new RunResultPostprocessorParameters();

			if (getProps().getSections().contains(postprocessor)) {
				Iterator<String> it = getProps().getSection(postprocessor).getKeys();
				while (it.hasNext()) {
					String param = it.next();

					params.put(param, getProps().getSection(postprocessor).getString(param));
				}
			}

			RunResultPostprocessor newPostprocessor = RunResultPostprocessor.parseFromString(this.repo, postprocessor,
					params);
			this.postprocessor.add(newPostprocessor);
		}
	}
}

class GoldStandardConfigParser extends RepositoryObjectParser<GoldStandardConfig> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
	 * (java.io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		log.debug("Parsing goldstandard config \"" + absPath + "\"");

		try {
			getProps().setThrowExceptionOnMissing(true);

			String gsName = getProps().getString("goldstandardName");
			String gsFile = getProps().getString("goldstandardFile");

			result = new GoldStandardConfig(repo, changeDate, absPath, GoldStandard.parseFromFile(
					new File(FileUtils.buildPath(repo.getBasePath(GoldStandard.class), gsName, gsFile))));
			result = repo.getRegisteredObject(result);
			log.debug("Goldstandard config parsed");
		} catch (NoSuchElementException e) {
			throw new GoldStandardConfigurationException(e);
		}
	}
}

class InternalParameterOptimizationRunParser extends ExecutionRunParser<InternalParameterOptimizationRun> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.ExecutionRunParser#parseFromFile(java
	 * .io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws ConfigurationException, UnknownContextException,
			NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		result = new InternalParameterOptimizationRun(repo, context, changeDate, absPath, programConfigs, dataConfigs,
				qualityMeasures, runParamValues, postprocessor, maxExecutionTimes);
		result = repo.getRegisteredObject(result, false);

	}
}

class ParameterOptimizationRunParser extends ExecutionRunParser<ParameterOptimizationRun> {

	protected List<Map<ProgramParameter<?>, String>> parameterValues;
	protected List<List<ProgramParameter<?>>> optimizationParameters;
	protected List<ParameterOptimizationMethod> optimizationMethods;
	protected String[] optimizationParas;
	protected List<ProgramParameter<?>> optParaList;
	protected String paramOptMethod;
	protected List<String> paramOptMethods;

	@Override
	public void parseFromFile(final File absPath)
			throws RegisterException, IncompatibleParameterOptimizationMethodException, NumberFormatException,
			UnknownParameterOptimizationMethodException, RunException, UnknownClusteringQualityMeasureException,
			NoOptimizableProgramParameterException, UnknownProgramParameterException, UnknownDataSetFormatException,
			ConfigurationException, FileNotFoundException, UnknownContextException, UnknownParameterType,
			UnknownRunResultFormatException, NoRepositoryFoundException, InvalidOptimizationParameterException,
			UnknownProgramTypeException, UnknownRProgramException, IncompatibleContextException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException {

		this.optimizationParameters = new ArrayList<List<ProgramParameter<?>>>();
		/*
		 * The optimization methods, for every program one method.
		 */
		this.optimizationMethods = new ArrayList<ParameterOptimizationMethod>();

		super.parseFromFile(absPath);

		ClusteringQualityMeasure optimizationCriterion = null;

		String paramOptCriterion = getProps().getString("optimizationCriterion");
		for (ClusteringQualityMeasure m : qualityMeasures)
			if (m.toString().equals(paramOptCriterion))
				optimizationCriterion = m;
		if (optimizationCriterion == null)
			throw new UnknownClusteringQualityMeasureException(
					"The optimization criterion is not contained in the list of quality measures.");

		String paramOptIterations = getProps().getString("optimizationIterations");
		if (!getProps().containsKey("optimizationIterations"))
			throw new RunException(
					"The number of optimization iterations has to be specified as attribute 'optimizationIterations'");

		for (int i = 0; i < programConfigs.size(); i++) {
			for (int j = 0; j < dataConfigs.size(); j++) {

				optimizationMethods.add(ParameterOptimizationMethod.parseFromString(repo, paramOptMethods.get(i),
						// first we initialize the object with a null
						// reference instead of the run
						null, programConfigs.get(i), dataConfigs.get(j), optimizationParameters.get(i),
						optimizationCriterion, Integer.valueOf(paramOptIterations), false));
			}
		}

		result = new ParameterOptimizationRun(repo, context, changeDate, absPath, programConfigs, dataConfigs,
				qualityMeasures, runParamValues, optimizationParameters, optimizationMethods, postprocessor,
				maxExecutionTimes);
		ParameterOptimizationRun registeredResult = repo.getRegisteredObject(result, false);

		if (registeredResult != null) {
			result = registeredResult;
		}

		// now we set the run reference of the methods
		for (int i = 0; i < optimizationMethods.size(); i++) {
			ParameterOptimizationMethod method = optimizationMethods.get(i);
			method.setRun(result);
		}

		// if we have the run already registered, we take that run and do not
		// register the parameter optimization methods.
		if (registeredResult == null) {
			// added 21.03.2013: handle registering of the methods
			for (int i = 0; i < optimizationMethods.size(); i++) {
				ParameterOptimizationMethod method = optimizationMethods.get(i);

				method.register();
				optimizationMethods.set(i, repo.getRegisteredObject(method));
			}
		}

		ParameterOptimizationRun.checkCompatibilityParameterOptimizationMethod(optimizationMethods, programConfigs,
				dataConfigs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.ExecutionRunParser#
	 * isParamConfigurationEntry(java.lang.String)
	 */
	@Override
	protected boolean isParamConfigurationEntry(String name) {
		return super.isParamConfigurationEntry(name) && !name.equals("optimizationParameters")
				&& !name.equals("optimizationMethod");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.ExecutionRunParser#addParamValueToMap()
	 */
	@Override
	protected boolean checkParamValueToMap(final String param) {
		for (String optPa : optimizationParas)
			if (optPa.equals(param))
				return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.ExecutionRunParser#
	 * parseProgramConfigurations()
	 */
	@Override
	protected void parseProgramConfigurations()
			throws RunException, UnknownContextException, IncompatibleContextException, UnknownDataSetFormatException,
			ConfigurationException, FileNotFoundException, RegisterException, UnknownParameterType,
			UnknownRunResultFormatException, NoRepositoryFoundException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			NoOptimizableProgramParameterException, GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException, UnknownClusteringQualityMeasureException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException {

		/*
		 * Default optimization method for all programs, where no specific
		 * method is defined
		 */
		paramOptMethod = getProps().getString("optimizationMethod");
		paramOptMethods = new ArrayList<String>();

		super.parseProgramConfigurations();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.ExecutionRunParser#
	 * parseProgramConfiguration(de.clusteval.program.ProgramConfig,
	 * java.util.Map)
	 */
	@Override
	protected void parseProgramConfigParams(ProgramConfig programConfig) throws NoOptimizableProgramParameterException,
			UnknownProgramParameterException, RunException, ConfigurationException {

		optParaList = new ArrayList<ProgramParameter<?>>();

		if (getProps().getSections().contains(programConfig.getName())) {

			/*
			 * These parameters are used for parameter optimization. If we are
			 * in parameter optimization mode and there are concrete values for
			 * this parameters in this section, they will be ignored.
			 */
			optimizationParas = getProps().getSection(programConfig.getName()).getStringArray("optimizationParameters");

			/*
			 * Check whether the given optimization parameter are indeed defined
			 * as optimizable parameters in the program config.
			 */
			for (String optPa : optimizationParas) {
				try {
					ProgramParameter<?> p = programConfig.getParamWithId(optPa);
					if (!programConfig.getOptimizableParams().contains(p))
						throw new NoOptimizableProgramParameterException("The run config " + absPath.getName()
								+ " contained invalid optimization parameters: " + optPa
								+ " is not an optimizable program parameter of program " + programConfig.getProgram());
					optParaList.add(p);
				} catch (UnknownProgramParameterException e) {
					/*
					 * Modify the message
					 */
					throw new UnknownProgramParameterException(
							"The run " + absPath.getName() + " contained invalid parameter values: "
									+ programConfig.getProgram() + " does not have a parameter " + optPa);
				}
			}

			if (getProps().getSection(programConfig.getName()).containsKey("optimizationMethod")) {
				paramOptMethods.add(getProps().getSection(programConfig.getName()).getString("optimizationMethod"));
			}
			/*
			 * Default optimization method of this run config
			 */
			else
				paramOptMethods.add(paramOptMethod);
		}
		/*
		 * If there are no explicit optimization parameters set in the run
		 * config, use all optimizable parameters of program config.
		 */
		else {
			optParaList.addAll(programConfig.getOptimizableParams());
			paramOptMethods.add(paramOptMethod);
		}

		if (optParaList.isEmpty())
			throw new RunException(
					"At least one optimization parameter must be specified for program configuration " + programConfig);

		optimizationParameters.add(optParaList);

		super.parseProgramConfigParams(programConfig);
	}
}

class ProgramConfigParser extends RepositoryObjectParser<ProgramConfig> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
	 * (java.io.File)
	 */
	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		log.debug("Parsing program config \"" + absPath + "\"");
		getProps().setThrowExceptionOnMissing(true);

		Context context;
		// by default we are in a clustering context
		if (getProps().containsKey("context"))
			context = Context.parseFromString(repo, getProps().getString("context"));
		else
			context = Context.parseFromString(repo, "ClusteringContext");

		/*
		 * Added 07.08.2012 Type of programconfig is either standalone or R
		 */
		String type;
		if (getProps().containsKey("type")) {
			type = getProps().getString("type");
		} else
			// Default
			type = "standalone";

		Program programP = null;
		// initialize compatible dataset formats
		String[] compatibleDataSetFormatsStr;

		RunResultFormat runresultFormat;
		List<DataSetFormat> compatibleDataSetFormats;
		boolean expectsNormalizedDataSet = false;
		if (type.equals("standalone")) {
			String program = FileUtils.buildPath(repo.getBasePath(Program.class), getProps().getString("program"));

			File programFile = new File(program);
			if (!(programFile).exists())
				throw new FileNotFoundException(
						"The given program executable does not exist: " + programFile.getAbsolutePath());

			changeDate = programFile.lastModified();

			String outputFormat = getProps().getString("outputFormat");

			compatibleDataSetFormatsStr = getProps().getStringArray("compatibleDataSetFormats");

			compatibleDataSetFormats = DataSetFormat.parseFromString(repo, compatibleDataSetFormatsStr);

			if (getProps().containsKey("expectsNormalizedDataSet"))
				expectsNormalizedDataSet = getProps().getBoolean("expectsNormalizedDataSet");
			else
				expectsNormalizedDataSet = false;

			for (DataSetFormat format : compatibleDataSetFormats)
				format.setNormalized(expectsNormalizedDataSet);

			runresultFormat = RunResultFormat.parseFromString(repo, outputFormat);

			String alias = getProps().getString("alias");

			Map<String, String> envVars = new HashMap<String, String>();
			Iterator<String> vars = getProps().getSection("envVars").getKeys();
			while (vars.hasNext()) {
				String var = vars.next();
				envVars.put(var, getProps().getSection("envVars").getString(var));
			}

			programP = new StandaloneProgram(repo, context, true, changeDate, programFile, alias, envVars);
		} else if (repo.isClassRegistered(RProgram.class, "de.clusteval.program.r." + type)) {
			programP = RProgram.parseFromString(repo, type);

			RProgram rProgram = (RProgram) programP;

			compatibleDataSetFormats = new ArrayList<DataSetFormat>(rProgram.getCompatibleDataSetFormats());

			runresultFormat = rProgram.getRunResultFormat();
		} else {
			throw new UnknownProgramTypeException("The type " + type + " is unknown.");
		}

		List<String> paras = Arrays.asList(getProps().getStringArray("parameters"));
		List<ProgramParameter<?>> params = new ArrayList<ProgramParameter<?>>();
		List<ProgramParameter<?>> optimizableParameters = new ArrayList<ProgramParameter<?>>();

		changeDate = absPath.lastModified();

		// check whether there are parameter-sections for parameters, that are
		// not listed in the parameters-list
		Set<String> sections = getProps().getSections();
		sections.removeAll(paras);
		sections.remove("envVars");
		sections.remove(null);
		sections.remove("invocationFormat");

		if (sections.size() > 0) {
			throw new UnknownProgramParameterException("There are parameter-sections " + sections + " in ProgramConfig "
					+ absPath.getName() + " for undefined parameters. Please add them to the parameter-list.");
		}

		int maxExecutionTimeMinutes = -1;
		if (getProps().containsKey("maxExecutionTimeMinutes"))
			maxExecutionTimeMinutes = getProps().getInt("maxExecutionTimeMinutes");

		if (type.equals("standalone")) {
			String invocationFormat = getProps().getSection("invocationFormat").getString("invocationFormat");
			String invocationFormatWithoutGoldStandard = null;
			String invocationFormatParameterOptimization = null;
			String invocationFormatParameterOptimizationWithoutGoldStandard = null;

			if (getProps().getSection("invocationFormat").containsKey("invocationFormatWithoutGoldStandard"))
				invocationFormatWithoutGoldStandard = getProps().getSection("invocationFormat")
						.getString("invocationFormatWithoutGoldStandard");
			else
				invocationFormatWithoutGoldStandard = invocationFormat;

			if (getProps().getSection("invocationFormat").containsKey("invocationFormatParameterOptimization"))
				invocationFormatParameterOptimization = getProps().getSection("invocationFormat")
						.getString("invocationFormatParameterOptimization");

			if (getProps().getSection("invocationFormat")
					.containsKey("invocationFormatParameterOptimizationWithoutGoldStandard"))
				invocationFormatParameterOptimizationWithoutGoldStandard = getProps().getSection("invocationFormat")
						.getString("invocationFormatParameterOptimizationWithoutGoldStandard");
			else
				invocationFormatParameterOptimizationWithoutGoldStandard = invocationFormatParameterOptimization;

			result = new ProgramConfig(repo, true, changeDate, absPath, programP, runresultFormat,
					compatibleDataSetFormats, invocationFormat, invocationFormatWithoutGoldStandard,
					invocationFormatParameterOptimization, invocationFormatParameterOptimizationWithoutGoldStandard,
					params, optimizableParameters, expectsNormalizedDataSet, maxExecutionTimeMinutes);
		}
		// RProgram
		else {
			result = new RProgramConfig(repo, true, changeDate, absPath, programP, runresultFormat,
					compatibleDataSetFormats, params, optimizableParameters, expectsNormalizedDataSet,
					maxExecutionTimeMinutes);
		}

		// // add parameter objects for input (i), executable (e), output (o)
		// // and goldstandard (gs)
		// params.add(new StringProgramParameter(repo, false, result, "i",
		// "Input", null, null));
		// params.add(new StringProgramParameter(repo, false, result, "e",
		// "Executable", null, null));
		// params.add(new StringProgramParameter(repo, false, result, "o",
		// "Output", null, null));
		// params.add(new StringProgramParameter(repo, false, result, "q",
		// "Quality", null, null));
		// params.add(new StringProgramParameter(repo, false, result, "gs",
		// "Goldstandard", null, null));

		/*
		 * Get the optimization parameters (parameters, that can be optimized
		 * for this program in parameter_optimization runmode
		 */
		String[] optimizableParams = getProps().getStringArray("optimizationParameters");

		// iterate over all parameters
		for (String pa : paras) {

			// skip the empty string
			if (pa.equals(""))
				continue;

			final Map<String, String> paramValues = new HashMap<String, String>();
			paramValues.put("name", pa);

			ProgramParameter<?> param = ProgramParameter.parseFromConfiguration(result, pa, getProps().getSection(pa));
			params.add(param);

			/*
			 * Check if this parameter is declared as an optimizable parameter
			 */
			boolean optimizable = false;
			for (String optPa : optimizableParams)
				if (optPa.equals(pa)) {
					optimizable = true;
					break;
				}

			if (optimizable) {
				/*
				 * Check if min and max values are given for this parameter,
				 * which is necessary for optimizing it
				 */
				if (!(param.isMinValueSet() || !param.isMaxValueSet()) && !param.isOptionsSet())
					throw new InvalidOptimizationParameterException("The parameter " + param
							+ " cannot be used as an optimization parameter, because its min and max values are not set.");
				optimizableParameters.add(param);
			}
		}

		result = repo.getRegisteredObject(result);
	}
}

class RepositoryObjectParser<T extends RepositoryObject> extends Parser<T> {

	// the members of the RepositoryObject class

	protected boolean loadConfigFile = true;
	private HierarchicalINIConfiguration props;
	protected Repository repo;
	protected long changeDate;
	protected File absPath;
	protected Logger log;

	@SuppressWarnings("unused")
	public void parseFromFile(final File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {

		if (!absPath.exists())
			throw new FileNotFoundException("File \"" + absPath + "\" does not exist!");

		this.repo = Repository.getRepositoryForPath(absPath.getAbsolutePath());
		this.changeDate = absPath.lastModified();
		this.absPath = absPath;
		this.log = LoggerFactory.getLogger(this.getClass());
	}

	protected HierarchicalINIConfiguration getProps() throws ConfigurationException {
		if (props == null)
			props = new HierarchicalINIConfiguration(absPath.getAbsolutePath());
		return props;
	}
}

class RunAnalysisRunParser extends AnalysisRunParser<RunAnalysisRun> {

	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		/*
		 * An analysis run consists of a set of dataconfigs
		 */
		List<String> uniqueRunIdentifiers = new LinkedList<String>();

		List<RunStatistic> runStatistics = new LinkedList<RunStatistic>();

		uniqueRunIdentifiers.addAll(Arrays.asList(getProps().getStringArray("uniqueRunIdentifiers")));

		/**
		 * We catch the exceptions such that all statistics are tried to be
		 * loaded once so that they are ALL registered as missing in the
		 * repository.
		 */
		List<UnknownRunStatisticException> thrownExceptions = new ArrayList<UnknownRunStatisticException>();
		for (String runStatistic : getProps().getStringArray("runStatistics")) {
			try {
				runStatistics.add(RunStatistic.parseFromString(repo, runStatistic));
			} catch (UnknownRunStatisticException e) {
				thrownExceptions.add(e);
			}
		}
		if (thrownExceptions.size() > 0) {
			// just throw the first exception
			throw thrownExceptions.get(0);
		}

		result = new RunAnalysisRun(repo, context, changeDate, absPath, uniqueRunIdentifiers, runStatistics);
		result = repo.getRegisteredObject(result, false);
	};
}

class RunDataAnalysisRunParser extends AnalysisRunParser<RunDataAnalysisRun> {

	@Override
	public void parseFromFile(File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		List<String> uniqueRunAnalysisRunIdentifiers = new LinkedList<String>();
		List<String> uniqueDataAnalysisRunIdentifiers = new LinkedList<String>();

		List<RunDataStatistic> runDataStatistics = new LinkedList<RunDataStatistic>();

		uniqueRunAnalysisRunIdentifiers.addAll(Arrays.asList(getProps().getStringArray("uniqueRunIdentifiers")));
		uniqueDataAnalysisRunIdentifiers.addAll(Arrays.asList(getProps().getStringArray("uniqueDataIdentifiers")));

		/**
		 * We catch the exceptions such that all statistics are tried to be
		 * loaded once so that they are ALL registered as missing in the
		 * repository.
		 */
		List<UnknownRunDataStatisticException> thrownExceptions = new ArrayList<UnknownRunDataStatisticException>();
		for (String runStatistic : getProps().getStringArray("runDataStatistics")) {
			try {
				runDataStatistics.add(RunDataStatistic.parseFromString(repo, runStatistic));
			} catch (UnknownRunDataStatisticException e) {
				thrownExceptions.add(e);
			}
		}
		if (thrownExceptions.size() > 0) {
			// just throw the first exception
			throw thrownExceptions.get(0);
		}

		result = new RunDataAnalysisRun(repo, context, changeDate, absPath, uniqueRunAnalysisRunIdentifiers,
				uniqueDataAnalysisRunIdentifiers, runDataStatistics);
		result = repo.getRegisteredObject(result, false);

	};
}

class RunParser<T extends Run> extends RepositoryObjectParser<T> {

	// the members of the Run class
	protected Context context;

	protected String mode;

	@Override
	public void parseFromFile(final File absPath) throws NoRepositoryFoundException, ConfigurationException,
			UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
			UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
			IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
			GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
			NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
		super.parseFromFile(absPath);

		// by default we are in a clustering context
		if (getProps().containsKey("context"))
			context = Context.parseFromString(repo, getProps().getString("context"));
		else
			context = Context.parseFromString(repo, "ClusteringContext");

		mode = getProps().getString("mode", "clustering");
	}
}

class RunResultDataSetConfigParser extends DataSetConfigParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.DataSetConfigParser#getDataSet()
	 */
	@Override
	protected DataSet getDataSet() {
		return repo.getStaticObjectWithName(DataSet.class, datasetName + "/" + datasetFile);
	}
}