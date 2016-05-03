/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.framework.repository.parse;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.IGoldStandardConfig;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.ParameterSet;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.run.IRun;
import de.clusteval.api.stats.IDataStatistic;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.RunResultDataSetConfig;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.DataRandomizer;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.framework.repository.RepositoryController;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.program.ProgramConfig;
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
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.result.postprocessing.RunResultPostprocessorParameters;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Wiwie
 * @param <P>
 *
 */
public abstract class Parser<P extends IRepositoryObject> {

    /**
     * TODO: this is completely wrong approach - we should get parser for some child class
     *
     * @param <T>
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static <T extends IRepositoryObject> Parser<T> getParserForClass(final Class<T> c) {
        if (c.equals(ClusteringRun.class)) {
            return (Parser<T>) new ClusteringRunParser();
        } else if (c.equals(ParameterOptimizationRun.class)) {
            return (Parser<T>) new ParameterOptimizationRunParser();
        } else if (c.equals(InternalParameterOptimizationRun.class)) {
            return (Parser<T>) new InternalParameterOptimizationRunParser();
        } else if (c.equals(DataAnalysisRun.class)) {
            return (Parser<T>) new DataAnalysisRunParser();
        } else if (c.equals(RunAnalysisRun.class)) {
            return (Parser<T>) new RunAnalysisRunParser();
        } else if (c.equals(RunDataAnalysisRun.class)) {
            return (Parser<T>) new RunDataAnalysisRunParser();
        } else if (c.equals(RobustnessAnalysisRun.class)) {
            return (Parser<T>) new RobustnessAnalysisRunParser();
        } else if (c.equals(IDataSetConfig.class)) {
            return (Parser<T>) new DataSetConfigParser();
        } else if (c.equals(RunResultDataSetConfig.class)) {
            return (Parser<T>) new RunResultDataSetConfigParser();
        } else if (c.equals(IDataSet.class)) {
            return (Parser<T>) new DataSetParser();
        } else if (c.equals(IGoldStandardConfig.class)) {
            return (Parser<T>) new GoldStandardConfigParser();
        } else if (c.equals(IProgramConfig.class)) {
            return (Parser<T>) new ProgramConfigParser();
        } else if (c.equals(IRun.class)) {
            return (Parser<T>) new RunParser<>();
        } else if (c.equals(IDataConfig.class)) {
            return (Parser<T>) new DataConfigParser();
        }
        throw new RuntimeException("could not find parser for " + c.getName());
    }

    public static <T extends IRepositoryObject> T parseFromFile(final Class<T> c, final File absPath)
            throws UnknownDataSetFormatException, GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException,
                   GoldStandardConfigNotFoundException, NoDataSetException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException, ConfigurationException, UnknownContextException,
                   FileNotFoundException, RegisterException, UnknownParameterType, NoRepositoryFoundException,
                   UnknownClusteringQualityMeasureException, RunException, IncompatibleContextException,
                   UnknownRunResultFormatException, InvalidOptimizationParameterException, UnknownProgramParameterException,
                   UnknownProgramTypeException, UnknownRProgramException, UnknownDistanceMeasureException,
                   UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        Parser<T> parser = getParserForClass(c);
        parser.parseFromFile(absPath);
        return parser.getResult();
    }

    public static Run parseRunFromFile(final File file)
            throws UnknownDataSetFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownContextException, FileNotFoundException, RegisterException,
                   UnknownParameterType, NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        String runMode = Parser.getModeOfRun(file);
        switch (runMode) {
            case "clustering":
                return Parser.parseFromFile(ClusteringRun.class, file);
            case "parameter_optimization":
                return Parser.parseFromFile(ParameterOptimizationRun.class, file);
            case "internal_parameter_optimization":
                return Parser.parseFromFile(InternalParameterOptimizationRun.class, file);
            case "dataAnalysis":
                return Parser.parseFromFile(DataAnalysisRun.class, file);
            case "runAnalysis":
                return Parser.parseFromFile(RunAnalysisRun.class, file);
            case "runDataAnalysis":
                return Parser.parseFromFile(RunDataAnalysisRun.class, file);
            case "robustnessAnalysis":
                return Parser.parseFromFile(RobustnessAnalysisRun.class, file);
            default:
                break;
        }
        return null;
    }

    protected static String getModeOfRun(final File absPath)
            throws UnknownDataSetFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownContextException, FileNotFoundException, RegisterException,
                   UnknownParameterType, NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        RunParser<? extends Run> p = (RunParser<Run>) getParserForClass(Run.class);
        p.parseFromFile(absPath);
        return p.mode;
    }

    protected P result;

    public abstract void parseFromFile(final File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException;

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
    public void parseFromFile(File absPath)
            throws ConfigurationException, UnknownContextException,
                   NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        result = new ClusteringRun(repo, context, changeDate, absPath, programConfigs, dataConfigs, qualityMeasures,
                runParamValues, postprocessor, maxExecutionTimes);
        result = repo.getRegisteredObject(result, false);
    }
}

class RobustnessAnalysisRunParser extends ExecutionRunParser<RobustnessAnalysisRun> {

    protected List<String> uniqueRunIdentifiers;
    protected List<IDataConfig> originalDataConfigs;

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.ExecutionRunParser#parseFromFile(java
     * .io.File)
     */
    @Override
    public void parseFromFile(File absPath)
            throws ConfigurationException, UnknownContextException,
                   NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        String[] list = getProps().getStringArray("uniqueRunIdentifiers");
        if (list.length == 0) {
            throw new RunException("At least one run result identifier must be specified");
        }
        // 10.07.2014: remove duplicates.
        list = new ArrayList<>(new HashSet<>(Arrays.asList(list))).toArray(new String[0]);
        this.uniqueRunIdentifiers = Arrays.asList(list);

        String randomizerS = getProps().getString("randomizer");
        DataRandomizer randomizer;
        if (this.repo instanceof RunResultRepository) {
            randomizer = DataRandomizer.parseFromString(this.repo.getParent(), randomizerS);
        } else {
            randomizer = DataRandomizer.parseFromString(this.repo, randomizerS);
        }
        int numberOfRandomizedDataSets = getProps().getInt("numberOfRandomizedDataSets");

        // get randomizer parameter sets
        List<ParameterSet> paramSets = new ArrayList<>();
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
    protected void parseDataConfigurations()
            throws RunException, UnknownDataSetFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   RegisterException, NoRepositoryFoundException, UnknownDistanceMeasureException,
                   UnknownDataPreprocessorException, IncompatibleDataSetConfigPreprocessorException, ConfigurationException,
                   UnknownContextException, FileNotFoundException, UnknownParameterType,
                   UnknownClusteringQualityMeasureException, IncompatibleContextException, UnknownRunResultFormatException,
                   InvalidOptimizationParameterException, UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {

        if (this.repo instanceof RunResultRepository) {
            this.originalDataConfigs = new ArrayList<>();
            // get the original data configs
            String[] list = getProps().getStringArray("dataConfig");
            if (list.length == 0) {
                throw new RunException("At least one data config must be specified");
            }
            // 10.07.2014: remove duplicates.
            list = new ArrayList<>(new HashSet<String>(Arrays.asList(list))).toArray(new String[0]);
            for (String dataConfig : list) {
                this.originalDataConfigs.add(repo.getParent().getStaticObjectWithName(DataConfig.class, dataConfig));
            }
            this.dataConfigs = new ArrayList<>(this.originalDataConfigs);

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
            this.originalDataConfigs = new ArrayList<>(this.dataConfigs);
        }
    }
}

class DataAnalysisRunParser extends AnalysisRunParser<DataAnalysisRun> {

    @Override
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        String[] list = getProps().getStringArray("dataConfig");
        if (list.length == 0) {
            throw new RunException("At least one data config must be specified");
        }

        /**
         * An analysis run consists of a set of dataconfigs
         */
        List<IDataConfig> dataConfigs = new LinkedList<>();

        List<IDataStatistic> dataStatistics = new LinkedList<>();

        for (String dataConfig : list) {
            dataConfigs.add(repo.getRegisteredObject(Parser.parseFromFile(DataConfig.class,
                    new File(FileUtils.buildPath(repo.getBasePath(IDataConfig.class), dataConfig + ".dataconfig")))));
        }

        /**
         * We catch the exceptions such that all statistics are tried to be
         * loaded once so that they are ALL registered as missing in the
         * repository.
         */
        List<UnknownDataStatisticException> thrownExceptions = new ArrayList<>();
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
    }
}

class ExecutionRunParser<T extends ExecutionRun> extends RunParser<T> {

    protected List<IProgramConfig> programConfigs;
    protected List<IDataConfig> dataConfigs;
    protected List<ClusteringEvaluation> qualityMeasures;
    protected List<Map<IProgramParameter<?>, String>> runParamValues;
    protected Map<IProgramParameter<?>, String> paramMap;
    protected List<RunResultPostprocessor> postprocessor;
    protected Map<String, Integer> maxExecutionTimes;

    @Override
    public void parseFromFile(final File absPath)
            throws ConfigurationException, UnknownContextException,
                   NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        /**
         * A run consists of a set of programconfigs and a set of dataconfigs,
         * that are pairwise combined.
         */
        programConfigs = new LinkedList<>();
        dataConfigs = new LinkedList<>();
        /**
         * The quality measures that should be calculated for every pair of
         * programconfig+dataconfig.
         */
        qualityMeasures = new LinkedList<>();
        /**
         * A list with parameter values that are set in the run config. They
         * will overwrite the default values of the program config.
         */
        runParamValues = new ArrayList<>();

        maxExecutionTimes = new HashMap<>();

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
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, UnknownDataStatisticException, UnknownRunStatisticException,
                   UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {

        String[] list = getProps().getStringArray("programConfig");
        if (list.length == 0) {
            throw new RunException("At least one program config must be specified");
        }
        // 10.07.2014: remove duplicates.
        list = new ArrayList<>(new HashSet<>(Arrays.asList(list))).toArray(new String[0]);
        for (String programConfig : list) {
            ProgramConfig newProgramConfig = Parser.parseFromFile(ProgramConfig.class,
                    new File(FileUtils.buildPath(repo.getBasePath(ProgramConfig.class), programConfig + ".config")));

            if (!newProgramConfig.getProgram().getContext().equals(context)) {
                throw new IncompatibleContextException("Incompatible run context (" + context
                        + ") and program context (" + newProgramConfig.getProgram().getContext() + ")");
            }

            newProgramConfig = repo.getRegisteredObject(newProgramConfig);
            programConfigs.add(newProgramConfig);

            /**
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

        if (getProps().getStringArray("qualityMeasures").length == 0) {
            throw new RunException("At least one quality measure must be specified");
        }
        /**
         * We catch the exceptions such that all quality measures are tried to
         * be loaded once so that they are ALL registered as missing in the
         * repository.
         */
        List<UnknownClusteringQualityMeasureException> thrownExceptions = new ArrayList<>();
        for (String qualityMeasure : getProps().getStringArray("qualityMeasures")) {
            try {
                // parse parameters for this quality measure
                ClusteringEvaluationParameters p = new ClusteringEvaluationParameters();
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

    protected void parseDataConfigurations()
            throws RunException, UnknownDataSetFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   RegisterException, NoRepositoryFoundException, UnknownDistanceMeasureException, UnknownDataPreprocessorException, IncompatibleDataSetConfigPreprocessorException, ConfigurationException,
                   UnknownContextException, FileNotFoundException, UnknownParameterType,
                   UnknownClusteringQualityMeasureException, IncompatibleContextException, UnknownRunResultFormatException,
                   InvalidOptimizationParameterException, UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        String[] list = getProps().getStringArray("dataConfig");
        if (list.length == 0) {
            throw new RunException("At least one data config must be specified");
        }
        // 10.07.2014: remove duplicates.
        list = new ArrayList<>(new HashSet<>(Arrays.asList(list))).toArray(new String[0]);
        for (String dataConfig : list) {
            dataConfigs.add(repo.getRegisteredObject(Parser.parseFromFile(DataConfig.class,
                    new File(FileUtils.buildPath(repo.getBasePath(DataConfig.class), dataConfig + ".dataconfig")))));
        }
    }

    protected void parseProgramConfigParams(final IProgramConfig programConfig)
            throws NoOptimizableProgramParameterException, UnknownProgramParameterException, RunException,
                   ConfigurationException {

        paramMap = new HashMap<>();

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
                } else if (isParamConfigurationEntry(param)) {
                    try {
                        IProgramParameter<?> p = programConfig.getParamWithId(param);

                        if (checkParamValueToMap(param)) {
                            paramMap.put(p, getProps().getSection(programConfig.getName()).getString(param));
                        }
                    } catch (UnknownProgramParameterException e) {
                        log.error("The run " + absPath.getName() + " contained invalid parameter values: "
                                + programConfig.getProgram() + " does not have a parameter " + param);
                    }
                }
            }
        }
        runParamValues.add(paramMap);
    }

    protected void parsePostprocessor() throws UnknownRunResultPostprocessorException, ConfigurationException {

        postprocessor = new ArrayList<>();

        if (!getProps().containsKey("postprocessor")) {
            return;
        }

        String[] list = getProps().getStringArray("postprocessor");
        // 10.07.2014: remove duplicates.
        list = new ArrayList<>(new HashSet<>(Arrays.asList(list))).toArray(new String[0]);
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
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
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
    public void parseFromFile(File absPath)
            throws ConfigurationException, UnknownContextException,
                   NoRepositoryFoundException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        result = new InternalParameterOptimizationRun(repo, context, changeDate, absPath, programConfigs, dataConfigs,
                qualityMeasures, runParamValues, postprocessor, maxExecutionTimes);
        result = repo.getRegisteredObject(result, false);

    }
}

class ParameterOptimizationRunParser extends ExecutionRunParser<ParameterOptimizationRun> {

    protected List<Map<IProgramParameter<?>, String>> parameterValues;
    protected List<List<IProgramParameter<?>>> optimizationParameters;
    protected List<ParameterOptimizationMethod> optimizationMethods;
    protected String[] optimizationParas;
    protected List<IProgramParameter<?>> optParaList;
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
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, UnknownDataStatisticException, UnknownRunStatisticException,
                   UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {

        this.optimizationParameters = new ArrayList<>();
        /*
         * The optimization methods, for every program one method.
         */
        this.optimizationMethods = new ArrayList<>();

        super.parseFromFile(absPath);

        ClusteringEvaluation optimizationCriterion = null;

        String paramOptCriterion = getProps().getString("optimizationCriterion");
        for (ClusteringEvaluation m : qualityMeasures) {
            if (m.toString().equals(paramOptCriterion)) {
                optimizationCriterion = m;
            }
        }
        if (optimizationCriterion == null) {
            throw new UnknownClusteringQualityMeasureException(
                    "The optimization criterion is not contained in the list of quality measures.");
        }

        String paramOptIterations = getProps().getString("optimizationIterations");
        if (!getProps().containsKey("optimizationIterations")) {
            throw new RunException(
                    "The number of optimization iterations has to be specified as attribute 'optimizationIterations'");
        }

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
        for (String optPa : optimizationParas) {
            if (optPa.equals(param)) {
                return false;
            }
        }
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
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, UnknownDataStatisticException, UnknownRunStatisticException,
                   UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {

        /*
         * Default optimization method for all programs, where no specific
         * method is defined
         */
        paramOptMethod = getProps().getString("optimizationMethod");
        paramOptMethods = new ArrayList<>();

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
    protected void parseProgramConfigParams(IProgramConfig programConfig)
            throws NoOptimizableProgramParameterException,
                   UnknownProgramParameterException, RunException, ConfigurationException {

        optParaList = new ArrayList<>();

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
                    IProgramParameter<?> p = programConfig.getParamWithId(optPa);
                    if (!programConfig.getOptimizableParams().contains(p)) {
                        throw new NoOptimizableProgramParameterException("The run config " + absPath.getName()
                                + " contained invalid optimization parameters: " + optPa
                                + " is not an optimizable program parameter of program " + programConfig.getProgram());
                    }
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
            } /*
             * Default optimization method of this run config
             */ else {
                paramOptMethods.add(paramOptMethod);
            }
        } /*
         * If there are no explicit optimization parameters set in the run
         * config, use all optimizable parameters of program config.
         */ else {
            optParaList.addAll(programConfig.getOptimizableParams());
            paramOptMethods.add(paramOptMethod);
        }

        if (optParaList.isEmpty()) {
            throw new RunException(
                    "At least one optimization parameter must be specified for program configuration " + programConfig);
        }

        optimizationParameters.add(optParaList);

        super.parseProgramConfigParams(programConfig);
    }
}

class RepositoryObjectParser<T extends RepositoryObject> extends Parser<T> {

    // the members of the RepositoryObject class
    protected boolean loadConfigFile = true;
    private HierarchicalINIConfiguration props;
    protected IRepository repo;
    protected long changeDate;
    protected File absPath;
    protected Logger log;

    @Override
    public void parseFromFile(final File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {

        if (!absPath.exists()) {
            throw new FileNotFoundException("File \"" + absPath + "\" does not exist!");
        }

        RepositoryController ctrl = RepositoryController.getInstance();
        this.repo = ctrl.getRepositoryForPath(absPath.getAbsolutePath());
        this.changeDate = absPath.lastModified();
        this.absPath = absPath;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    protected HierarchicalINIConfiguration getProps() throws ConfigurationException {
        if (props == null) {
            props = new HierarchicalINIConfiguration(absPath.getAbsolutePath());
        }
        return props;
    }
}

class RunAnalysisRunParser extends AnalysisRunParser<RunAnalysisRun> {

    @Override
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        /*
         * An analysis run consists of a set of dataconfigs
         */
        List<String> uniqueRunIdentifiers = new LinkedList<>();

        List<RunStatistic> runStatistics = new LinkedList<>();

        uniqueRunIdentifiers.addAll(Arrays.asList(getProps().getStringArray("uniqueRunIdentifiers")));

        /**
         * We catch the exceptions such that all statistics are tried to be
         * loaded once so that they are ALL registered as missing in the
         * repository.
         */
        List<UnknownRunStatisticException> thrownExceptions = new ArrayList<>();
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
    }
;

}

class RunDataAnalysisRunParser extends AnalysisRunParser<RunDataAnalysisRun> {

    @Override
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        List<String> uniqueRunAnalysisRunIdentifiers = new LinkedList<>();
        List<String> uniqueDataAnalysisRunIdentifiers = new LinkedList<>();

        List<RunDataStatistic> runDataStatistics = new LinkedList<>();

        uniqueRunAnalysisRunIdentifiers.addAll(Arrays.asList(getProps().getStringArray("uniqueRunIdentifiers")));
        uniqueDataAnalysisRunIdentifiers.addAll(Arrays.asList(getProps().getStringArray("uniqueDataIdentifiers")));

        /**
         * We catch the exceptions such that all statistics are tried to be
         * loaded once so that they are ALL registered as missing in the
         * repository.
         */
        List<UnknownRunDataStatisticException> thrownExceptions = new ArrayList<>();
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
