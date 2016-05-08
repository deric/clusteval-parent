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

import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.InvalidConfigurationFileException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import de.clusteval.api.stats.DataStatistic;
import de.clusteval.api.stats.IDataStatistic;
import de.clusteval.api.stats.IStatistic;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.DataAnalysisRun;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@ServiceProvider(service = IRunResult.class)
public class DataAnalysisRunResult extends AnalysisRunResult<IDataConfig, IDataStatistic> implements IRunResult {

    private static final String NAME = "DataAnalysisRunResult";

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param runIdentString
     * @param run
     * @throws RegisterException
     */
    public DataAnalysisRunResult(IRepository repository, long changeDate, File absPath, String runIdentString,
            final IRun run) throws RegisterException {
        super(repository, changeDate, absPath, runIdentString, run);
    }

    public DataAnalysisRunResult() {
        super();
    }

    /**
     * The copy constructor for data analysis run results.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public DataAnalysisRunResult(final DataAnalysisRunResult other) throws RegisterException {
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
    protected Map<IDataConfig, List<IDataStatistic>> cloneStatistics(Map<IDataConfig, List<IDataStatistic>> statistics) {
        final Map<IDataConfig, List<IDataStatistic>> result = new HashMap<>();

        for (Map.Entry<IDataConfig, List<IDataStatistic>> entry : statistics.entrySet()) {
            List<IDataStatistic> newList = new ArrayList<>();

            for (IDataStatistic elem : entry.getValue()) {
                newList.add(elem.clone());
            }

            result.put(entry.getKey().clone(), newList);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.result.AnalysisRunResult#clone()
     */
    @Override
    public DataAnalysisRunResult clone() {
        try {
            return new DataAnalysisRunResult(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    /*
     * @param parentRepository
     * @param runResultFolder
     * @return The data analysis run result parsed from the given runresult
     * folder.
     *
     */
    @Override
    public DataAnalysisRunResult parseFromRunResultFolder(final IRepository parentRepository, final File runResultFolder)
            throws RepositoryAlreadyExistsException, InvalidRepositoryException,
                   GoldStandardConfigurationException, DataSetConfigurationException, DataSetNotFoundException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, IOException, UnknownRunResultFormatException,
                   InvalidConfigurationFileException,
                   InvalidRunModeException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownProgramParameterException, NoRepositoryFoundException, GoldStandardNotFoundException,
                   InvalidOptimizationParameterException, RunException,
                   UnknownProgramTypeException, UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownGoldStandardFormatException, AnalysisRunResultException,
                   RepositoryConfigurationException, ConfigurationException,
                   RegisterException, NumberFormatException, NoDataSetException,
                   IncompatibleContextException, UnknownParameterType, InterruptedException,
                   UnknownRunResultPostprocessorException, FileNotFoundException, UnknownProviderException {
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
            final IRun object = Parser.parseRunFromFile(runFile);

            DataAnalysisRunResult analysisResult = null;

            if (object instanceof DataAnalysisRun) {
                final DataAnalysisRun run = (DataAnalysisRun) object;

                File analysesFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "analyses"));

                analysisResult = new DataAnalysisRunResult(parentRepository, analysesFolder.lastModified(),
                        analysesFolder, analysesFolder.getParentFile().getName(), run);

                for (final IDataConfig dataConfig : run.getDataConfigs()) {

                    List<IDataStatistic> statistics = new ArrayList<>();
                    for (final IStatistic dataStatistic : run.getStatistics()) {
                        final File completeFile = new File(FileUtils.buildPath(analysesFolder.getAbsolutePath(),
                                dataConfig.toString() + "_" + dataStatistic.getIdentifier() + ".txt"));
                        if (!completeFile.exists()) {
                            throw new AnalysisRunResultException("The result file of (" + dataConfig + ","
                                    + dataStatistic.getIdentifier() + ") could not be found: " + completeFile);
                        }
                        final String fileContents = FileUtils.readStringFromFile(completeFile.getAbsolutePath());

                        dataStatistic.parseFromString(fileContents);
                        statistics.add((IDataStatistic) dataStatistic);

                    }
                    analysisResult.put(dataConfig, statistics);
                }
                analysisResult.register();
            }
            return analysisResult;
        } catch (DatabaseConnectException e) {
            // cannot happen
            return null;
        }
    }

    /**
     * @return The data configurations encapsulating the datasets that were
     *         analysed.
     */
    public Set<IDataConfig> getDataConfigs() {
        return this.statistics.keySet();
    }

    /**
     * @param dataConfig
     *                   The data configuration for which we want to know which data
     *                   statistics were evaluated.
     * @return The data statistics that were assessed for the given data
     *         configuration.
     */
    public List<IDataStatistic> getDataStatistics(final IDataConfig dataConfig) {
        return this.statistics.get(dataConfig);
    }

    /**
     * @param run
     * @param parentRepository
     * @param runResultFolder
     * @param result
     * @param register
     * @return The data analysis run result parsed from the given runresult
     *         folder.
     * @throws RegisterException
     * @throws RunResultParseException
     *
     */
    public static DataAnalysisRunResult parseFromRunResultFolder(final DataAnalysisRun run,
            final IRepository parentRepository, final File runResultFolder, final List<IRunResult> result,
            final boolean register) throws RegisterException, RunResultParseException {

        DataAnalysisRunResult analysisResult = null;

        File analysesFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "analyses"));

        analysisResult = new DataAnalysisRunResult(parentRepository, analysesFolder.lastModified(), analysesFolder,
                analysesFolder.getParentFile().getName(), run);

        if (register) {
            analysisResult.loadIntoMemory();
            try {
                analysisResult.register();
            } finally {
                analysisResult.unloadFromMemory();
            }
        }
        result.add(analysisResult);
        return analysisResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.result.RunResult#loadIntoMemory()
     */
    @Override
    public void loadIntoMemory() throws RunResultParseException {

        for (final IDataConfig dataConfig : getRun().getDataConfigs()) {

            List<IDataStatistic> stats = new ArrayList<>();
            for (final IStatistic dataStatistic : getRun().getStatistics()) {
                final File completeFile = new File(FileUtils.buildPath(absPath.getAbsolutePath(),
                        dataConfig.toString() + "_" + dataStatistic.getIdentifier() + ".txt"));
                if (!completeFile.exists()) {
                    throw new RunResultParseException("The result file of (" + dataConfig + ","
                            + dataStatistic.getIdentifier() + ") could not be found: " + completeFile);
                }
                final String fileContents = FileUtils.readStringFromFile(completeFile.getAbsolutePath());

                try {
                    dataStatistic.parseFromString(fileContents);
                    stats.add((DataStatistic) dataStatistic);
                } catch (Exception e) {

                }

            }
            this.put(dataConfig, stats);
        }
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

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.result.RunResult#getRun()
     */
    @Override
    public DataAnalysisRun getRun() {
        return (DataAnalysisRun) super.getRun();
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
