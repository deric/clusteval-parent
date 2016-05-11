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
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.RunResultParseException;
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
import de.clusteval.api.stats.IDataStatistic;
import de.clusteval.api.stats.RunStatistic;
import de.clusteval.api.stats.Statistic;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.RunAnalysisRun;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
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
public class RunAnalysisRunResult extends AnalysisRunResult<String, RunStatistic> {

    private static final String NAME = "run-analysis-result";

    public RunAnalysisRunResult() throws RegisterException {
        super(null); //TODO
    }

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @param runIdentString
     * @param run
     * @throws RegisterException
     */
    public RunAnalysisRunResult(IRepository repository, boolean register, long changeDate, File absPath,
            String runIdentString, final IRun run) throws RegisterException {
        super(repository, changeDate, absPath, runIdentString, run);

        if (register) {
            this.register();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * The copy constructor for run analysis run results.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RunAnalysisRunResult(final RunAnalysisRunResult other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.result.AnalysisRunResult#cloneStatistics(java.util.Map)
     */
    @Override
    protected Map<String, List<RunStatistic>> cloneStatistics(Map<String, List<RunStatistic>> statistics) {
        final Map<String, List<RunStatistic>> result = new HashMap<>();

        for (Map.Entry<String, List<RunStatistic>> entry : statistics.entrySet()) {
            List<RunStatistic> newList = new ArrayList<>();

            for (RunStatistic elem : entry.getValue()) {
                newList.add(elem.clone());
            }

            result.put(new String(entry.getKey()), newList);
        }

        return result;
    }

    @Override
    public RunAnalysisRunResult clone() {
        try {
            return new RunAnalysisRunResult(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RunAnalysisRunResult parseFromRunResultFolder(final IRepository repository, final File runResultFolder)
            throws FileNotFoundException, UnknownProviderException, RepositoryAlreadyExistsException, InterruptedException, InvalidRepositoryException, RepositoryConfigurationException, RegisterException, GoldStandardConfigurationException, GoldStandardNotFoundException, DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, NoDataSetException, NumberFormatException, ConfigurationException, UnknownParameterType, NoRepositoryFoundException, RunException, IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException, UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException, IncompatibleParameterOptimizationMethodException, UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException, UnknownRunResultPostprocessorException, RunResultParseException {
        try {
            IRepository childRepository = new RunResultRepository(runResultFolder.getAbsolutePath(), repository);
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

            RunAnalysisRunResult analysisResult = null;

            if (object instanceof RunAnalysisRun) {
                final RunAnalysisRun run = (RunAnalysisRun) object;

                File analysesFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "analyses"));

                analysisResult = new RunAnalysisRunResult(repository, false, analysesFolder.lastModified(),
                        analysesFolder, analysesFolder.getParentFile().getName(), run);

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
     * @see de.clusteval.run.result.RunResult#getRun()
     */
    @Override
    public RunAnalysisRun getRun() {
        return (RunAnalysisRun) super.getRun();
    }

    @Override
    public void loadIntoMemory() throws RunResultParseException {

        for (final String uniqueRunIdentifier : this.getRun().getUniqueRunAnalysisRunIdentifiers()) {

            List<RunStatistic> statistics = new ArrayList<>();
            for (final Statistic runStatistic : this.getRun().getStatistics()) {
                final File completeFile = new File(FileUtils.buildPath(absPath.getAbsolutePath(),
                        uniqueRunIdentifier + "_" + runStatistic.getIdentifier() + ".txt"));
                if (!completeFile.exists()) {
                    throw new RunResultParseException("The result file of (" + uniqueRunIdentifier + ","
                            + runStatistic.getIdentifier() + ") could not be found: " + completeFile);
                }
                final String fileContents = FileUtils.readStringFromFile(completeFile.getAbsolutePath());

                runStatistic.parseFromString(fileContents);
                statistics.add((RunStatistic) runStatistic);

            }
            this.put(uniqueRunIdentifier, statistics);
        }
    }

    @Override
    public boolean isInMemory() {
        return this.statistics.isEmpty();
    }

    @Override
    public void unloadFromMemory() {
        this.statistics.clear();
    }

    /**
     * @return A set with all runresult identifiers, which the run analysed
     *         which produced this runresult.
     */
    public Set<String> getUniqueRunIdentifier() {
        return this.statistics.keySet();
    }

    /**
     * @param uniqueRunIdentifier
     *                            The runresult identifier for which we want to know the
     *                            assessed run statistics.
     * @return A list with all run statistics assessed for the given runresult
     *         identifier.
     */
    public List<RunStatistic> getRunStatistics(final String uniqueRunIdentifier) {
        return this.statistics.get(uniqueRunIdentifier);
    }

    /**
     * @param run
     *                        The run analysis run corresponding to the given runresult
     *                        folder.
     * @param repository
     *                        The repository in which we want to register the parsed
     *                        runresult.
     * @param runResultFolder
     *                        A file object referencing the runresult folder.
     * @param result
     * @param register
     * @return The run analysis runresult parsed from the given runresult
     *         folder.
     * @throws RunResultParseException
     * @throws RegisterException
     *
     */
    public static RunAnalysisRunResult parseFromRunResultFolder(final RunAnalysisRun run, final IRepository repository,
            final File runResultFolder, final List<IRunResult> result, final boolean register)
            throws RunResultParseException, RegisterException {

        RunAnalysisRunResult analysisResult = null;

        File analysesFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "analyses"));

        analysisResult = new RunAnalysisRunResult(repository, false, analysesFolder.lastModified(), analysesFolder,
                analysesFolder.getParentFile().getName(), run);

        for (final String uniqueRunIdentifier : run.getUniqueRunAnalysisRunIdentifiers()) {

            List<RunStatistic> statistics = new ArrayList<>();
            for (final Statistic runStatistic : run.getStatistics()) {
                final File completeFile = new File(FileUtils.buildPath(analysesFolder.getAbsolutePath(),
                        uniqueRunIdentifier + "_" + runStatistic.getIdentifier() + ".txt"));
                if (!completeFile.exists()) {
                    throw new RunResultParseException("The result file of (" + uniqueRunIdentifier + ","
                            + runStatistic.getIdentifier() + ") could not be found: " + completeFile);
                }
                final String fileContents = FileUtils.readStringFromFile(completeFile.getAbsolutePath());

                runStatistic.parseFromString(fileContents);
                statistics.add((RunStatistic) runStatistic);

            }
            analysisResult.put(uniqueRunIdentifier, statistics);
        }

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

    @Override
    public Set<IDataConfig> getDataConfigs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IDataStatistic> getDataStatistics(IDataConfig dataConfig) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
