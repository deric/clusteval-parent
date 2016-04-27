/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.run.result;

import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
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
import de.clusteval.api.repository.RegisterException;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.framework.repository.RepositoryController;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.ClusteringRun;
import de.clusteval.run.DataAnalysisRun;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.Run;
import de.clusteval.run.RunAnalysisRun;
import de.clusteval.run.RunDataAnalysisRun;
import de.clusteval.run.RunException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.InvalidConfigurationFileException;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class for runresults produced by runs of the framework.
 *
 * @author Christian Wiwie
 *
 */
public abstract class RunResult extends RepositoryObject implements IRunResult {

    /**
     * @param parentRepository
     * @param runResultFolder
     * @param result
     * @param parseClusterings
     * @param storeClusterings
     * @param register
     * @return A runresult object for the given runresult folder.
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
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     * @throws NoRepositoryFoundException
     * @throws GoldStandardNotFoundException
     * @throws InvalidOptimizationParameterException
     * @throws GoldStandardConfigurationException
     * @throws DataSetConfigurationException
     * @throws DataSetNotFoundException
     * @throws DataSetConfigNotFoundException
     * @throws GoldStandardConfigNotFoundException
     * @throws DataConfigurationException
     * @throws DataConfigNotFoundException
     * @throws RunException
     * @throws UnknownDataStatisticException
     * @throws UnknownProgramTypeException
     * @throws UnknownRProgramException
     * @throws IncompatibleParameterOptimizationMethodException
     * @throws UnknownDistanceMeasureException
     * @throws UnknownRunStatisticException
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
    // TODO: we cannot move this method into Parser#RunResultParser, because
    // ParameterOptimizationRun.parseFromRunResultFolder() returns several
    // RunResult objects per invocation. This is not compatible with the
    // structure of the Parser class. Best solution: rewrite
    // ParameterOptimizationRunResult class, such that it contains all results
    // of the folder in one object.
    public static Run parseFromRunResultFolder(final IRepository parentRepository, final File runResultFolder,
            final List<IRunResult> result, final boolean parseClusterings, final boolean storeClusterings,
            final boolean register) throws IOException, UnknownRunResultFormatException, UnknownDataSetFormatException,
                                           UnknownClusteringQualityMeasureException, InvalidRunModeException,
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
            Logger log = LoggerFactory.getLogger(RunResult.class);
            log.debug("Parsing run result from '" + runResultFolder + "'");
            RepositoryController ctrl = RepositoryController.getInstance();
            IRepository childRepository = ctrl.getRepositoryForExactPath(runResultFolder.getAbsolutePath());
            if (childRepository == null) {
                childRepository = new RunResultRepository(runResultFolder.getAbsolutePath(), parentRepository);
            }
            childRepository.initialize();
            try {

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

                if (run instanceof ClusteringRun) {
                    return ClusteringRunResult.parseFromRunResultFolder((ClusteringRun) run, childRepository,
                            runResultFolder, result, register);
                } else if (run instanceof ParameterOptimizationRun) {
                    return ParameterOptimizationResult.parseFromRunResultFolder((ParameterOptimizationRun) run,
                            childRepository, runResultFolder, result, parseClusterings, storeClusterings, register);
                } else if (run instanceof DataAnalysisRun) {
                    DataAnalysisRunResult.parseFromRunResultFolder((DataAnalysisRun) run, childRepository,
                            runResultFolder, result, register);
                    return run;
                } else if (run instanceof RunDataAnalysisRun) {
                    RunDataAnalysisRunResult.parseFromRunResultFolder((RunDataAnalysisRun) run, childRepository,
                            runResultFolder, result, register);
                    return run;
                } else if (run instanceof RunAnalysisRun) {
                    RunAnalysisRunResult.parseFromRunResultFolder((RunAnalysisRun) run, childRepository,
                            runResultFolder, result, register);
                    return run;
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
     * The run ident string.
     */
    protected String runIdentString;

    protected IRun run;

    protected boolean changedSinceLastRegister;

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param runIdentString
     * @param run
     * @throws RegisterException
     */
    public RunResult(IRepository repository, long changeDate, File absPath, final String runIdentString, final IRun run)
            throws RegisterException {
        super(repository, false, changeDate, absPath);
        this.runIdentString = runIdentString;
        this.run = run;
    }

    /**
     * The copy constructor of run results.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public RunResult(final RunResult other) throws RegisterException {
        super(other);
        this.runIdentString = other.runIdentString;
        this.run = other.run.clone();
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public abstract RunResult clone();

    /**
     * @return The unique identifier of this runresult, equal to the name of the
     *         runresult folder.
     */
    public String getIdentifier() {
        return this.runIdentString;
    }

    /**
     * @return The run this runresult belongs to.
     */
    public IRun getRun() {
        return this.run;
    }

    public boolean hasChangedSinceLastRegister() {
        return this.changedSinceLastRegister;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.framework.repository.RepositoryObject#register()
     */
    @Override
    public boolean register() throws RegisterException {
        boolean result = super.register();
        if (result) {
            this.changedSinceLastRegister = false;
        }
        return result;
    }
}
