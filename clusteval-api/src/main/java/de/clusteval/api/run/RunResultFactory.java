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
package de.clusteval.api.run;

import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
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
import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.ParameterOptimizationMethod;
import de.clusteval.api.opt.ParameterOptimizationRun;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IParser;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.ParserFactory;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.repository.RepositoryController;
import de.clusteval.api.repository.RepositoryFactory;
import de.clusteval.api.run.result.RunResult;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class RunResultFactory extends ServiceFactory<IRunResult> {

    private static RunResultFactory instance;

    public static RunResultFactory getInstance() {
        if (instance == null) {
            instance = new RunResultFactory();
        }
        return instance;
    }

    private RunResultFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IRunResult> list = Lookup.getDefault().lookupAll(IRunResult.class);
        for (IRunResult c : list) {
            providers.put(c.getName(), c);
        }
        sort();
    }

    public static IRunResult parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    /**
     * @param run
     *                         The run corresponding to the runresult folder.
     * @param repository
     *                         The repository in which we want to register the runresult.
     * @param runResultFolder
     *                         A file object referencing the runresult folder.
     * @param result
     *                         The list of runresults this method fills.
     * @param parseClusterings
     *                         Whether to parse clusterings.
     * @param storeClusterings
     *                         Whether to store clusterings, if they are parsed.
     * @param register
     *                         A boolean indicating whether to register the parsed runresult.
     * @return The parameter optimization run parsed from the runresult folder.
     * @throws RegisterException
     * @throws RunResultParseException
     */
    public static Run parseFromRunResultFolder(final ParameterOptimizationRun run, final IRepository repository,
            final File runResultFolder, final List<IRunResult> result, final boolean parseClusterings,
            final boolean storeClusterings, final boolean register) throws RegisterException, RunResultParseException, UnknownProviderException {

        File clusterFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "clusters"));
        for (final ParameterOptimizationMethod method : run.getOptimizationMethods()) {
            final File completeFile = new File(
                    FileUtils.buildPath(clusterFolder.getAbsolutePath(), method.getProgramConfig().toString() + "_"
                            + method.getDataConfig().toString() + ".results.qual.complete"));
            final IRunResult tmpResult = parseFromRunResultCompleteFile(repository, run, method,
                    completeFile, parseClusterings, storeClusterings, register);
            if (tmpResult != null) {
                result.add(tmpResult);
            }

        }
        return run;
    }

    /**
     * @param repository
     * @param run
     * @param method
     * @param completeFile
     * @param parseClusterings
     * @param storeClusterings
     * @param register
     *                         A boolean indicating whether to register the parsed runresult.
     * @return The parameter optimization run result parsed from the given
     *         runresult folder.
     * @throws RegisterException
     * @throws RunResultParseException
     */
    public static IRunResult parseFromRunResultCompleteFile(final IRepository repository,
            ParameterOptimizationRun run, ParameterOptimizationMethod method, File completeFile,
            final boolean parseClusterings, final boolean storeClusterings, final boolean register)
            throws RegisterException, RunResultParseException, UnknownProviderException {
        IRunResult result = null;
        if (completeFile.exists()) {
            result = parseFromString(completeFile.getParentFile().getParentFile().getName());
            result.init(repository, completeFile.lastModified(), completeFile);
            //result.setRun(run);
            //result.setMethod(method);

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

    public static IRun parseFromRunResultFolder2(final IRepository parentRepository, final File runResultFolder,
            final List<IRunResult> result, final boolean parseClusterings,
            final boolean storeClusterings, final boolean register)
            throws IOException, UnknownRunResultFormatException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownProgramParameterException, UnknownGoldStandardFormatException,
                   InvalidConfigurationFileException, RepositoryAlreadyExistsException, InvalidRepositoryException,
                   NoRepositoryFoundException, GoldStandardNotFoundException, InvalidOptimizationParameterException,
                   GoldStandardConfigurationException, DataSetConfigurationException, DataSetNotFoundException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   RunException,
                   UnknownProgramTypeException, UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   RepositoryConfigurationException,
                   ConfigurationException, RegisterException, NumberFormatException,
                   NoDataSetException, RunResultParseException,
                   IncompatibleContextException, UnknownParameterType, InterruptedException,
                   UnknownRunResultPostprocessorException, FileNotFoundException, UnknownProviderException {
        try {
            IRepository childRepository = RepositoryFactory.parseFromString("RunResultRepository");
            childRepository.init(runResultFolder.getAbsolutePath(), parentRepository, null);
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
                //TODO: make it work!
                IParser parser = ParserFactory.findParser(runFile);
                final IRun run = parser.parseRunFromFile(runFile);

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
                        final IRunResult tmpResult = parseFromRunResultCompleteFile(parentRepository,
                                paramRun, method, completeFile, parseClusterings, storeClusterings, register);
                        if (tmpResult != null) {
                            result.add(tmpResult);
                        }

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
     * @param parentRepository
     * @param runResultFolder
     * @param result
     * @param parseClusterings
     * @param storeClusterings
     * @param register
     * @return A runresult object for the given runresult folder.
     * @throws IOException
     * @throws UnknownRunResultFormatException
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
     * @throws RunException
     * @throws UnknownProgramTypeException
     * @throws UnknownRProgramException
     * @throws IncompatibleParameterOptimizationMethodException
     * @throws RepositoryConfigurationException
     * @throws ConfigurationException
     * @throws RegisterException
     * @throws NoDataSetException
     * @throws NumberFormatException
     * @throws RunResultParseException
     * @throws IncompatibleContextException
     * @throws UnknownParameterType
     * @throws InterruptedException
     * @throws UnknownRunResultPostprocessorException
     */
    // TODO: we cannot move this method into Parser#RunResultParser, because
    // ParameterOptimizationRun.parseFromRunResultFolder() returns several
    // RunResult objects per invocation. This is not compatible with the
    // structure of the Parser class. Best solution: rewrite
    // ParameterOptimizationRunResult class, such that it contains all results
    // of the folder in one object.
    public static IRun parseFromRunResultFolder(final IRepository parentRepository, final File runResultFolder,
            final List<IRunResult> result, final boolean parseClusterings, final boolean storeClusterings,
            final boolean register)
            throws IOException, UnknownRunResultFormatException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownProgramParameterException, UnknownGoldStandardFormatException,
                   InvalidConfigurationFileException, RepositoryAlreadyExistsException, InvalidRepositoryException,
                   NoRepositoryFoundException, GoldStandardNotFoundException, InvalidOptimizationParameterException,
                   GoldStandardConfigurationException, DataSetConfigurationException, DataSetNotFoundException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   RunException,
                   UnknownProgramTypeException, UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   RepositoryConfigurationException,
                   ConfigurationException, RegisterException, NumberFormatException,
                   NoDataSetException, RunResultParseException,
                   IncompatibleContextException, UnknownParameterType, InterruptedException,
                   UnknownRunResultPostprocessorException, FileNotFoundException, UnknownProviderException {
        try {
            Logger log = LoggerFactory.getLogger(RunResult.class);
            log.debug("Parsing run result from '" + runResultFolder + "'");
            RepositoryController ctrl = RepositoryController.getInstance();
            IRepository childRepository = ctrl.getRepositoryForExactPath(runResultFolder.getAbsolutePath());
            if (childRepository == null) {
                childRepository = RepositoryFactory.parseFromString("RunResultRepository");
                childRepository.init(runResultFolder.getAbsolutePath(), parentRepository, null);
                childRepository.initialize();
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

                IParser parser = ParserFactory.findParser(runFile);
                final IRun run = parser.parseRunFromFile(runFile);
                //TODO write an interface for finding appropriate Run class
                /*
                 * if (run instanceof ClusteringRun) {
                 * return ClusteringRunResult.parseFromRunResultFolder((ClusteringRun) run, childRepository,
                 * runResultFolder, result, register);
                 * } else if (run instanceof ParameterOptimizationRun) {
                 * return ParameterOptimizationResult.parseFromRunResultFolder((ParameterOptimizationRun) run,
                 * childRepository, runResultFolder, result, parseClusterings, storeClusterings, register);
                 * } else if (run instanceof DataAnalysisRun) {
                 * DataAnalysisRunResult.parseFromRunResultFolder((DataAnalysisRun) run, childRepository,
                 * runResultFolder, result, register);
                 * return run;
                 * } else if (run instanceof RunDataAnalysisRun) {
                 * RunDataAnalysisRunResult.parseFromRunResultFolder((RunDataAnalysisRun) run, childRepository,
                 * runResultFolder, result, register);
                 * return run;
                 * } else if (run instanceof RunAnalysisRun) {
                 * RunAnalysisRunResult.parseFromRunResultFolder((RunAnalysisRun) run, childRepository,
                 * runResultFolder, result, register);
                 * return run;
                 * } */
                return run;
            } finally {
                childRepository.terminateSupervisorThread();
            }
        } catch (DatabaseConnectException e) {
            // cannot happen
            return null;
        }
    }

}
