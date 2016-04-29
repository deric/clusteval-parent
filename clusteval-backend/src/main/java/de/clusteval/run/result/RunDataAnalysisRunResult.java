/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.run.result;

import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.run.Run;
import de.clusteval.run.RunDataAnalysisRun;
import de.clusteval.run.RunException;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.InvalidConfigurationFileException;
import de.clusteval.utils.Statistic;
import de.clusteval.utils.FileUtils;
import de.clusteval.api.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@ServiceProvider(service = IRunResult.class)
public class RunDataAnalysisRunResult extends AnalysisRunResult<Pair<List<String>, List<String>>, RunDataStatistic> {

    private static final String NAME = "run-data-analysis-result";

    public RunDataAnalysisRunResult() throws RegisterException {
        //TODO: move logic from constructor
        super(null);
    }

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param runIdentString
     * @param run
     * @throws RegisterException
     */
    public RunDataAnalysisRunResult(IRepository repository, long changeDate, File absPath, String runIdentString,
            final IRun run) throws RegisterException {
        super(repository, changeDate, absPath, runIdentString, run);
    }

    /**
     * The copy constructor for run data analysis run results.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RunDataAnalysisRunResult(final RunDataAnalysisRunResult other) throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return NAME;
    }


    /*
     * (non-Javadoc)
     *
     * @see run.result.AnalysisRunResult#cloneStatistics(java.util.Map)
     */
    @Override
    protected Map<Pair<List<String>, List<String>>, List<RunDataStatistic>> cloneStatistics(
            Map<Pair<List<String>, List<String>>, List<RunDataStatistic>> statistics) {
        final Map<Pair<List<String>, List<String>>, List<RunDataStatistic>> result = new HashMap<>();

        for (Map.Entry<Pair<List<String>, List<String>>, List<RunDataStatistic>> entry : statistics.entrySet()) {
            List<RunDataStatistic> newList = new ArrayList<>();

            for (RunDataStatistic elem : entry.getValue()) {
                newList.add(elem.clone());
            }
            Pair<List<String>, List<String>> oldPair = entry.getKey();
            result.put(new Pair<>(oldPair.getFirst(), oldPair.getSecond()), newList);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.result.AnalysisRunResult#clone()
     */
    @Override
    public RunDataAnalysisRunResult clone() {
        try {
            return new RunDataAnalysisRunResult(this);
        } catch (RegisterException e) {
            Exceptions.printStackTrace(e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.result.RunResult#getRun()
     */
    @Override
    public RunDataAnalysisRun getRun() {
        return (RunDataAnalysisRun) super.getRun();
    }

    /**
     * @param parentRepository
     * @param runResultFolder
     * @return The run-data analysis runresult parsed from the given runresult
     *         folder.
     *
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     * @throws UnknownDistanceMeasureException
     * @throws IncompatibleParameterOptimizationMethodException
     * @throws UnknownRProgramException
     * @throws UnknownProgramTypeException
     * @throws UnknownDataStatisticException
     * @throws RunException
     * @throws InvalidOptimizationParameterException
     * @throws GoldStandardNotFoundException
     * @throws NoRepositoryFoundException
     * @throws UnknownProgramParameterException
     * @throws NoOptimizableProgramParameterException
     * @throws UnknownParameterOptimizationMethodException
     * @throws InvalidRunModeException
     * @throws UnknownClusteringQualityMeasureException
     * @throws InvalidConfigurationFileException
     * @throws UnknownDataSetFormatException
     * @throws UnknownRunResultFormatException
     * @throws IOException
     * @throws DataConfigNotFoundException
     * @throws DataConfigurationException
     * @throws GoldStandardConfigNotFoundException
     * @throws DataSetConfigNotFoundException
     * @throws DataSetNotFoundException
     * @throws DataSetConfigurationException
     * @throws GoldStandardConfigurationException
     * @throws UnknownRunStatisticException
     * @throws UnknownGoldStandardFormatException
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
    public static RunDataAnalysisRunResult parseFromRunResultFolder(final IRepository parentRepository,
            final File runResultFolder) throws RepositoryAlreadyExistsException, InvalidRepositoryException,
                                               GoldStandardConfigurationException, DataSetConfigurationException, DataSetNotFoundException,
                                               DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
                                               DataConfigNotFoundException, IOException, UnknownRunResultFormatException,
                                               UnknownDataSetFormatException, InvalidConfigurationFileException,
                                               UnknownClusteringQualityMeasureException, InvalidRunModeException,
                                               UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                                               UnknownProgramParameterException, NoRepositoryFoundException, GoldStandardNotFoundException,
                                               InvalidOptimizationParameterException, RunException, UnknownDataStatisticException,
                                               UnknownProgramTypeException, UnknownRProgramException,
                                               IncompatibleParameterOptimizationMethodException, UnknownDistanceMeasureException,
                                               UnknownRunStatisticException, UnknownGoldStandardFormatException, RepositoryConfigNotFoundException,
                                               RepositoryConfigurationException, ConfigurationException, RegisterException,
                                               UnknownDataSetTypeException, NumberFormatException, NoDataSetException,
                                               UnknownRunDataStatisticException, RunResultParseException, UnknownDataPreprocessorException,
                                               IncompatibleDataSetConfigPreprocessorException, UnknownContextException,
                                               IncompatibleContextException, UnknownParameterType, InterruptedException,
                                               UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
        try {
            IRepository childRepository = new RunResultRepository(runResultFolder.getAbsolutePath(), parentRepository);
            childRepository.initialize();

            File runFile = null;
            File configFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "configs"));
            if (!configFolder.exists()) {
                return null;
            }
            for (File child : configFolder.listFiles()) {
                if (child.getName().endsWith(".run")) {
                    runFile = child;
                    break;
                }
            }
            if (runFile == null) {
                return null;
            }
            final Run run = Parser.parseRunFromFile(runFile);

            RunDataAnalysisRunResult analysisResult = null;

            if (run instanceof RunDataAnalysisRun) {
                final RunDataAnalysisRun runDataRun = (RunDataAnalysisRun) run;

                File analysesFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "analyses"));

                analysisResult = new RunDataAnalysisRunResult(parentRepository, analysesFolder.lastModified(),
                        analysesFolder, analysesFolder.getParentFile().getName(), runDataRun);

                analysisResult.loadIntoMemory();
                try {
                    analysisResult.register();
                } finally {
                    analysisResult.unloadFromMemory();
                }
            }
            return analysisResult;
        } catch (DatabaseConnectException e) {
            // cannot happen
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.result.RunResult#loadIntoMemory()
     */
    @Override
    public void loadIntoMemory() throws RunResultParseException {

        List<RunDataStatistic> res = new ArrayList<>();
        for (final Statistic runDataStatistic : this.getRun().getStatistics()) {
            final File completeFile = new File(
                    FileUtils.buildPath(absPath.getAbsolutePath(), runDataStatistic.getIdentifier() + ".txt"));
            if (!completeFile.exists()) {
                throw new RunResultParseException("The result file of (" + runDataStatistic.getIdentifier()
                        + ") could not be found: " + completeFile);
            }
            final String fileContents = FileUtils.readStringFromFile(completeFile.getAbsolutePath());

            runDataStatistic.parseFromString(fileContents);
            res.add((RunDataStatistic) runDataStatistic);

        }
        this.put(Pair.getPair(this.getRun().getUniqueRunAnalysisRunIdentifiers(),
                this.getRun().getUniqueDataAnalysisRunIdentifiers()), res);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.result.RunResult#isInMemory()
     */
    @Override
    public boolean isInMemory() {
        return this.statistics.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.result.RunResult#unloadFromMemory()
     */
    @Override
    public void unloadFromMemory() {
        this.statistics.clear();
    }

    /**
     * @return A set with all pairs of identifiers of run analysis runresults
     *         and data analysis runresults.
     */
    public Set<Pair<List<String>, List<String>>> getUniqueIdentifierPairs() {
        return this.statistics.keySet();
    }

    /**
     * @param uniqueRunIdentifierPair
     *                                A pair with identifier of run analysis runresult and data
     *                                analysis runresult for which we want to know which run-data
     *                                statistics were evaluated.
     * @return A list with all run-data statistics that were evaluated for the
     *         given pair.
     */
    public List<RunDataStatistic> getRunDataStatistics(final Pair<List<String>, List<String>> uniqueRunIdentifierPair) {
        return this.statistics.get(uniqueRunIdentifierPair);
    }

    /**
     * @param run
     *                        The run corresponding to the given runresult folder.
     * @param repository
     *                        The repository in which we want to register the parsed
     *                        runresult.
     * @param runResultFolder
     *                        The folder containing the runresult.
     * @param result
     * @param register
     * @return The run-data analysis runresult parsed from the given runresult
     *         folder.
     * @throws RunResultParseException
     * @throws RegisterException
     *
     */
    public static RunDataAnalysisRunResult parseFromRunResultFolder(final RunDataAnalysisRun run,
            final IRepository repository, final File runResultFolder, final List<IRunResult> result,
            final boolean register) throws RunResultParseException, RegisterException {

        RunDataAnalysisRunResult analysisResult = null;

        File analysesFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "analyses"));

        analysisResult = new RunDataAnalysisRunResult(repository, analysesFolder.lastModified(), analysesFolder,
                analysesFolder.getParentFile().getName(), run);

        List<RunDataStatistic> statistics = new ArrayList<>();
        for (final Statistic runDataStatistic : run.getStatistics()) {
            final File completeFile = new File(
                    FileUtils.buildPath(analysesFolder.getAbsolutePath(), runDataStatistic.getIdentifier() + ".txt"));
            if (!completeFile.exists()) {
                throw new RunResultParseException("The result file of (" + runDataStatistic.getIdentifier()
                        + ") could not be found: " + completeFile);
            }
            final String fileContents = FileUtils.readStringFromFile(completeFile.getAbsolutePath());

            runDataStatistic.parseFromString(fileContents);
            statistics.add((RunDataStatistic) runDataStatistic);

        }
        analysisResult.put(
                Pair.getPair(run.getUniqueRunAnalysisRunIdentifiers(), run.getUniqueDataAnalysisRunIdentifiers()),
                statistics);

        result.add(analysisResult);
        if (register) {
            analysisResult.register();
        }
        return analysisResult;
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
}
