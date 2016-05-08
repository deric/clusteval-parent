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

import de.clusteval.api.Pair;
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
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.ParameterSet;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.IClusteringRunResult;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.IRunResultFormatParser;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.NoRunResultFormatParserException;
import de.clusteval.api.run.Run;
import de.clusteval.api.run.RunException;
import de.clusteval.api.run.RunResultFormat;
import de.clusteval.api.run.result.ExecutionRunResult;
import de.clusteval.api.run.result.RunResultNotFoundException;
import de.clusteval.cluster.Clustering;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.ClusteringRun;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * The Class ClusteringResult.
 *
 * @author Christian Wiwie
 *
 */
@ServiceProvider(service = IRunResult.class)
public class ClusteringRunResult extends ExecutionRunResult implements IClusteringRunResult {

    /**
     * The result format.
     */
    protected IRunResultFormat resultFormat;

    protected Pair<ParameterSet, Clustering> clustering;

    private static final String NAME = "clustering result";

    @Override
    public String getName() {
        return NAME;
    }

    public ClusteringRunResult() throws RegisterException {
        super(null);//TODO
    }

    /**
     * Instantiates a new clustering result.
     *
     * @param repository     the repository
     * @param changeDate
     * @param dataConfig     the data config
     * @param programConfig  the program config
     * @param resultFormat   the result format
     * @param absPath        the abs file path
     * @param runIdentString the run ident string
     * @param run
     * @throws RegisterException
     */
    public ClusteringRunResult(final IRepository repository, final long changeDate, final File absPath,
            final IDataConfig dataConfig, final IProgramConfig programConfig, final IRunResultFormat resultFormat,
            final String runIdentString, final IRun run) throws RegisterException {
        super(repository, changeDate, absPath, runIdentString, run, dataConfig, programConfig);

        this.resultFormat = resultFormat;
    }

    /**
     * The copy constructor of run results.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public ClusteringRunResult(final ClusteringRunResult other) throws RegisterException {
        super(other);

        this.resultFormat = other.resultFormat.clone();
        this.clustering = Pair.getPair(other.clustering.getFirst(), other.clustering.getSecond());
    }

    /*
     * (non-Javadoc)
     *
     * @see run.result.RunResult#clone()
     */
    @Override
    public ClusteringRunResult clone() {
        try {
            return new ClusteringRunResult(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
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
     * Convert to.
     *
     * @param format         the format
     * @param internalParams Internal parameters used to produced the clustering
     *                       result needed for parsing parameters.
     * @param params         Parameters used to produced the clustering result needed
     *                       for parsing parameters.
     * @return the clustering result
     * @throws NoRunResultFormatParserException the no run result format parser
     * exception
     * @throws RunResultNotFoundException
     * @throws RegisterException
     */
    @SuppressWarnings("unused")
    public ClusteringRunResult convertTo(final IRunResultFormat format, final Map<String, String> internalParams,
            final Map<String, String> params)
            throws NoRunResultFormatParserException, RunResultNotFoundException, RegisterException {
        ClusteringRunResult result = null;
        IRunResultFormatParser p = null;

        if (!new File(this.absPath.getAbsolutePath()).exists()) {
            throw new RunResultNotFoundException(
                    "The result file " + this.absPath.getAbsolutePath() + " does not exist!");
        }

        /*
         * We already have the same format
         */
        if (this.getResultFormat().equals(format)) {
            /*
             * Just copy the result file and return the corresponding
             * ClusteringRunResult
             */
            try {
                org.apache.commons.io.FileUtils.copyFile(new File(this.getAbsolutePath()),
                        new File(this.getAbsolutePath() + ".conv"));
                return new ClusteringRunResult(this.repository, System.currentTimeMillis(),
                        new File(this.absPath.getAbsolutePath() + ".conv"), this.dataConfig, this.programConfig, format,
                        this.runIdentString, run);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            p = this.repository.getRunResultFormatParser(this.getResultFormat().getClass().getName())
                    .getConstructor(Map.class, Map.class, String.class)
                    .newInstance(internalParams, params, this.absPath.getAbsolutePath());
            if (p != null) {
                p.convertToStandardFormat();
                result = new ClusteringRunResult(this.repository, System.currentTimeMillis(),
                        new File(this.absPath.getAbsolutePath() + ".conv"), this.dataConfig, this.programConfig, format,
                        this.runIdentString, run);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof FileNotFoundException | (e.getCause() instanceof IOException
                    && e.getCause().getMessage().startsWith("Empty file given"))) {
                /*
                 * Ensure, that all the files of this result are deleted
                 */
                FileUtils.delete(this.absPath);
                throw new RunResultNotFoundException(e.getCause().getMessage());
            }
            e.printStackTrace();
        } catch (IOException e) {
            /*
             * Ensure, that all the files of this result are deleted
             */
            FileUtils.delete(this.absPath);
            throw new RunResultNotFoundException(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @return The clustering corresponding to this clustering run result.
     */
    public Pair<ParameterSet, Clustering> getClustering() {
        return this.clustering;
    }

    /**
     * Gets the result format.
     *
     * @return the result format
     */
    public IRunResultFormat getResultFormat() {
        return resultFormat;
    }

    /**
     * Sets the result format.
     *
     * @param resultFormat the new result format
     */
    public void setResultFormat(RunResultFormat resultFormat) {
        this.resultFormat = resultFormat;
    }

    /**
     * @param run             The run corresponding to the runresult folder.
     * @param repository      The repository in which we want to register the
     *                        runresult.
     * @param runResultFolder A file object referencing the runresult folder.
     * @param result          The list of runresults this method fills.
     * @param register
     * @return The parameter optimization run parsed from the runresult folder.
     * @throws RegisterException
     */
    public static Run parseFromRunResultFolder(final ClusteringRun run, final IRepository repository,
            final File runResultFolder, final List<IRunResult> result, final boolean register) throws RegisterException {

        File clusterFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "clusters"));

        for (final IDataConfig dataConfig : run.getDataConfigs()) {
            for (final IProgramConfig programConfig : run.getProgramConfigs()) {
                final File completeFile = new File(FileUtils.buildPath(clusterFolder.getAbsolutePath(),
                        programConfig.toString() + "_" + dataConfig + ".1.results.conv"));
                final ClusteringRunResult tmpResult = parseFromRunResultCompleteFile(repository, run, dataConfig,
                        programConfig, completeFile, register);
                if (tmpResult != null) {
                    result.add(tmpResult);
                }
            }
        }

        return run;
    }

    /**
     * @param repository
     * @param run
     * @param dataConfig
     * @param programConfig
     * @param completeFile
     * @param register
     * @return The parameter optimization run result parsed from the given
     *         runresult folder.
     * @throws RegisterException
     */
    public static ClusteringRunResult parseFromRunResultCompleteFile(IRepository repository, ClusteringRun run,
            final IDataConfig dataConfig, final IProgramConfig programConfig, final File completeFile,
            final boolean register) throws RegisterException {
        ClusteringRunResult result = null;
        if (completeFile.exists()) {
            result = new ClusteringRunResult(repository, completeFile.lastModified(), completeFile, dataConfig,
                    programConfig, programConfig.getOutputFormat(),
                    completeFile.getParentFile().getParentFile().getName(), run);

            if (register) {
                /*
                 * Register after parsing
                 */
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

    @Override
    public void loadIntoMemory() {
        if (absPath.exists()) {
            try {
                final Pair<ParameterSet, Clustering> pair = Clustering.parseFromFile(repository,
                        new File(absPath.getAbsolutePath().replace("results.qual.complete", "1.results.conv")), true);

                ParameterSet paramSet = new ParameterSet();
                for (String param : pair.getFirst().keySet()) {
                    paramSet.put(param, pair.getFirst().get(param));
                }
                this.clustering = Pair.getPair(paramSet, pair.getSecond());
                this.clustering.getSecond().loadIntoMemory();
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }
    }

    @Override
    public boolean isInMemory() {
        return this.clustering != null;
    }

    @Override
    public void unloadFromMemory() {
        this.clustering.getSecond().unloadFromMemory();
        this.clustering = null;
    }

    @Override
    public void convertToStandardFormat() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static IRun parseFromRunResultFolder(final IRepository parentRepository, final File runResultFolder,
            final List<ExecutionRunResult> result, final boolean register)
            throws IOException, UnknownRunResultFormatException, InvalidRunModeException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownProgramParameterException, UnknownGoldStandardFormatException,
                   InvalidConfigurationFileException, RepositoryAlreadyExistsException, InvalidRepositoryException,
                   NoRepositoryFoundException, GoldStandardNotFoundException, InvalidOptimizationParameterException,
                   GoldStandardConfigurationException, DataSetConfigurationException, DataSetNotFoundException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, RunException,
                   UnknownProgramTypeException, UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   RepositoryConfigurationException,
                   ConfigurationException, RegisterException, NumberFormatException,
                   NoDataSetException,
                   IncompatibleDataSetConfigPreprocessorException,
                   IncompatibleContextException, UnknownParameterType, InterruptedException,
                   UnknownRunResultPostprocessorException, UnknownProviderException {

        IRepository childRepository;
        try {
            childRepository = new RunResultRepository(runResultFolder.getAbsolutePath(), parentRepository);
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
            final IRun run = Parser.parseRunFromFile(runFile);

            if (run instanceof ClusteringRun) {
                final ClusteringRun paramRun = (ClusteringRun) run;

                File clusterFolder = new File(FileUtils.buildPath(runResultFolder.getAbsolutePath(), "clusters"));
                for (final IDataConfig dataConfig : paramRun.getDataConfigs()) {
                    for (final IProgramConfig programConfig : paramRun.getProgramConfigs()) {
                        final File completeFile = new File(FileUtils.buildPath(clusterFolder.getAbsolutePath(),
                                programConfig.toString() + "_" + dataConfig + ".results.qual.complete"));
                        final ClusteringRunResult tmpResult = parseFromRunResultCompleteFile(parentRepository, paramRun,
                                dataConfig, programConfig, completeFile, register);
                        if (tmpResult != null) {
                            result.add(tmpResult);
                        }
                    }
                }
            }
            return run;
        } catch (DatabaseConnectException e) {
            // cannot happen
            return null;
        }
    }

    @Override
    public IRunResult parseFromRunResultFolder(IRepository parentRepository, File runResultFolder) throws FileNotFoundException, UnknownProviderException {
        //TODO: convert static method
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
