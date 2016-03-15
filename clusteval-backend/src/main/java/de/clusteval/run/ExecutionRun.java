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
package de.clusteval.run;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import de.clusteval.api.run.RUN_STATUS;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.result.NoRunResultFormatParserException;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.runnable.ExecutionRunRunnable;
import de.clusteval.run.runnable.RunRunnable;
import de.clusteval.run.runnable.RunRunnableInitializationException;
import de.wiwie.wiutils.file.FileUtils;
import de.wiwie.wiutils.utils.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for all run types, that involve execution of clustering
 * tools and applying them to datasets.
 *
 * <p>
 * An execution run has a list of program and data configurations. These are
 * pairwise combined and for every pair (see {@link #runPairs}) a runnable is
 * created and executed asynchronously by the {@link RunnerScheduler}.
 *
 * <p>
 * The data and program configurations passed to this run are the same as the
 * objects stored in the repository. Thus those objects can change during
 * runtime of this run. To avoid those changes affecting the run during its
 * execution the original objects are cloned in
 * {@link #initRunPairs(List, List)} before they are passed to the run runnables
 * which are performed asynchronously.
 *
 * @author Christian Wiwie
 *
 */
public abstract class ExecutionRun extends Run {

    protected static List<Map<ProgramParameter<?>, String>> cloneParameterValues(
            final List<Map<ProgramParameter<?>, String>> parameterValues) {
        List<Map<ProgramParameter<?>, String>> result = new ArrayList<>();

        for (Map<ProgramParameter<?>, String> map : parameterValues) {
            Map<ProgramParameter<?>, String> newMap = new HashMap<>();

            for (Map.Entry<ProgramParameter<?>, String> entry : map.entrySet()) {
                newMap.put(entry.getKey().clone(), entry.getValue() + "");
            }

            result.add(newMap);
        }

        return result;
    }

    protected static List<RunResultPostprocessor> clonePostProcessors(
            final List<RunResultPostprocessor> postProcessors) {
        List<RunResultPostprocessor> result = new ArrayList<>();

        for (RunResultPostprocessor postpro : postProcessors) {
            result.add(postpro.clone());
        }

        return result;
    }

    /**
     * A list of program configurations contained in this run.
     *
     * <p>
     * The references to program configurations in this list are the same as
     * those stored in the repository. That means the objects in this list can
     * change during runtime of the run.
     */
    protected List<ProgramConfig> programConfigs;

    protected Map<String, Integer> maxExecutionTimes;

    /**
     * A list of data configurations contained in this run.
     *
     * <p>
     * The references to data configurations in this list are the same as those
     * stored in the repository. That means the objects in this list can change
     * during runtime of the run.
     */
    protected List<IDataConfig> dataConfigs;

    /**
     * The pairwise combinations of data and program configurations that are
     * used to create the runnables.
     */
    protected List<Pair<ProgramConfig, DataConfig>> runPairs;

    /**
     * During execution of this run for every clustering that is calculated a
     * set of clustering quality measures is calculated.
     */
    protected List<ClusteringEvaluation> qualityMeasures;

    /**
     * The parameter values for every pair of program and data configuration.
     */
    protected List<Map<ProgramParameter<?>, String>> parameterValues;

    protected List<RunResultPostprocessor> postProcessors;

    /**
     * The constructor of this class takes a name, date and configuration. It is
     * protected, to force usage of the static method
     *
     * @param repository        the repository
     * @param context
     * @param register          Whether the new instance should be registered at the
     *                          repository.
     * @param changeDate        The date this run was performed.
     * @param absPath           The absolute path to the file on the filesystem that
     *                          corresponds to this run.
     * @param programConfigs    The program configurations of the new run.
     * @param dataConfigs       The data configurations of the new run.
     * @param qualityMeasures   The clustering quality measures of the new run.
     * @param postProcessors
     * @param maxExecutionTimes
     * @param parameterValues   The parameter values of this run.
     * @throws RegisterException
     */
    protected ExecutionRun(final IRepository repository, final Context context,
            final boolean register, final long changeDate, final File absPath,
            final List<ProgramConfig> programConfigs,
            final List<IDataConfig> dataConfigs,
            final List<ClusteringEvaluation> qualityMeasures,
            final List<Map<ProgramParameter<?>, String>> parameterValues,
            final List<RunResultPostprocessor> postProcessors,
            final Map<String, Integer> maxExecutionTimes)
            throws RegisterException {
        super(repository, context, changeDate, absPath);

        this.parameterValues = parameterValues;
        this.qualityMeasures = qualityMeasures;
        this.postProcessors = postProcessors;
        this.maxExecutionTimes = maxExecutionTimes;

        initRunPairs(programConfigs, dataConfigs);

        if (register && this.register()) {
            // register this Run at all dataconfigs and programconfigs
            for (IDataConfig dataConfig : this.dataConfigs) {
                dataConfig.addListener(this);
            }
            for (ProgramConfig programConfig : this.programConfigs) {
                programConfig.addListener(this);
            }

            for (ClusteringEvaluation measure : this.qualityMeasures) {
                // added 21.03.2013: measures are only registered here, if this
                // run has been registered
                measure.register();
                measure.addListener(this);
            }

            for (RunResultPostprocessor postpro : this.postProcessors) {
                postpro.addListener(this);
            }
        }
    }

    /**
     * Copy constructor for execution runs.
     *
     * @param other The execution run to be cloned.
     * @throws RegisterException
     */
    protected ExecutionRun(final ExecutionRun other) throws RegisterException {
        super(other);

        this.parameterValues = cloneParameterValues(other.parameterValues);
        this.qualityMeasures = ClusteringQualityMeasure
                .cloneQualityMeasures(other.qualityMeasures);
        this.postProcessors = clonePostProcessors(other.postProcessors);
        this.maxExecutionTimes = new HashMap<>(other.maxExecutionTimes);

        initRunPairs(
                ProgramConfig.cloneProgramConfigurations(other.programConfigs),
                DataConfig.cloneDataConfigurations(other.dataConfigs));
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public abstract ExecutionRun clone();

    @Override
    public boolean terminate() {
        synchronized (this.runnables) {
            for (RunRunnable thread : this.runnables) {
                thread.terminate();
            }
            this.setStatus(RUN_STATUS.TERMINATED);
            return true;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see run.Run#getUpperLimitProgress()
     */
    @Override
    protected long getUpperLimitProgress() {
        // we set the number of steps of this run to 100% for every run pair.
        return getRunPairs().size() * 10000;
    }

    /**
     * This method will perform this run, i.e. it combines every program with
     * every dataset contained in this run's configuration. For every such
     * combination a runnable is performed and the result of this will be added
     * to the list of run results.
     *
     * @throws RunRunnableInitializationException
     * @throws RunInitializationException
     */
    @Override
    public void perform(final RunSchedulerThread runScheduler)
            throws IOException, RunRunnableInitializationException,
                   RunInitializationException {
        /**
         * Before we start we check, whether this run has been terminated by
         * invoking terminate(). This is also the reason, why we have to
         * synchronize here in order to avoid only partial termination.
         */
        synchronized (this.runnables) {
            if (this.getStatus().equals(RUN_STATUS.TERMINATED)) {
                return;
            }

            beforePerform();
            doPerform(runScheduler);
        }
        waitForRunnablesToFinish();
        afterPerform();
    }

    /**
     * This method will resume this run, i.e. it combines every program with
     * every dataset contained in this run's configuration. For every such
     * combination a runnable is performed and the result of this will be added
     * to the list of run results.
     *
     * @throws de.clusteval.run.MissingParameterValueException
     * @throws java.io.IOException
     * @throws de.clusteval.run.result.NoRunResultFormatParserException
     * @throws RunRunnableInitializationException
     * @throws RunInitializationException
     */
    @SuppressWarnings("unused")
    @Override
    public void resume(final RunSchedulerThread runScheduler,
            final String runIdentString) throws MissingParameterValueException,
                                                IOException, NoRunResultFormatParserException,
                                                RunRunnableInitializationException, RunInitializationException {
        /**
         * Before we start we check, whether this run has been terminated by
         * invoking terminate(). This is also the reason, why we have to
         * synchronize here in order to avoid only partial termination.
         */
        synchronized (this.runnables) {
            if (this.getStatus().equals(RUN_STATUS.TERMINATED)) {
                return;
            }

            beforeResume(runIdentString);
            doResume(runScheduler, runIdentString);
        }
        waitForRunnablesToFinish();
        afterResume(runIdentString);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.Run#getNumberOfRunRunnables()
     */
    @Override
    protected int getNumberOfRunRunnables() {
        return getRunPairs().size();
    }

    @Override
    protected RunRunnable createAndScheduleRunnableForRunPair(
            RunSchedulerThread runScheduler, int p) {

        File movedConfigsDir = getMovedConfigsDir();

        /*
         * We only operate on this copy, in order to avoid multithreading
         * problems.
         */
        // changed 22.01.2013
        // ExecutionRun runCopy = this.clone();
        ExecutionRun runCopy = this;

        final Pair<ProgramConfig, DataConfig> pair = runCopy.getRunPairs().get(p);

        ProgramConfig programConfig = pair.getFirst();
        DataConfig dataConfig = pair.getSecond();

        /**
         * Copy to results directory
         */
        // 06.04.2013: create File objects for later use in order to create new
        // data configuration and program configuration objects
        File copiedDataConfig = new File(FileUtils.buildPath(
                movedConfigsDir.getAbsolutePath(),
                new File(dataConfig.getAbsolutePath()).getName()));
        dataConfig.copyTo(copiedDataConfig);
        File copiedDataSetConfig = new File(FileUtils.buildPath(movedConfigsDir
                .getAbsolutePath(), new File(dataConfig.getDatasetConfig()
                        .getAbsolutePath()).getName()));
        dataConfig.getDatasetConfig().copyTo(copiedDataSetConfig);
        File copiedGoldstandardConfig = null;
        if (dataConfig.hasGoldStandardConfig()) {
            copiedGoldstandardConfig = new File(FileUtils.buildPath(
                    movedConfigsDir.getAbsolutePath(), new File(dataConfig
                            .getGoldstandardConfig().getAbsolutePath())
                    .getName()));
            dataConfig.getGoldstandardConfig().copyTo(copiedGoldstandardConfig);
        }

        File copiedProgramConfig = new File(FileUtils.buildPath(
                movedConfigsDir.getAbsolutePath(),
                new File(programConfig.getAbsolutePath()).getName()));
        programConfig.copyTo(copiedProgramConfig);

        String input = dataConfig.getDatasetConfig().getDataSet()
                .getAbsolutePath();

        /*
         * To avoid overwriting of the input or conversion files, we copy it to
         * the results directory (which is unique for this run).
         */
        // changed 01.09.2012, don't create subdirectory for
        // programConfig_dataConfig
        // undo: 26.02.2013: not creating subdirectory lead to a bug of
        // overwriting same dataset file for different dataconfigs
        String movedInput = FileUtils.buildPath(new File(runCopy
                .getRepository().getClusterResultsBasePath()).getParentFile()
                .getAbsolutePath().replace("%RUNIDENTSTRING", runIdentString),
                "inputs", programConfig.getName() + "_" + dataConfig.getName(),
                new File(input).getParentFile().getName(), new File(input)
                .getName());
        if (!(new File(movedInput).exists())) {
            dataConfig.getDatasetConfig().getDataSet()
                    .copyTo(new File(movedInput));
        }

        /*
         * Copy gold standard
         */
        String movedGoldStandard = null;
        if (dataConfig.hasGoldStandardConfig()) {
            String goldStandard = dataConfig.getGoldstandardConfig()
                    .getGoldstandard().getAbsolutePath();

            movedGoldStandard = FileUtils.buildPath(
                    new File(runCopy.getRepository()
                            .getClusterResultsBasePath()).getParentFile()
                    .getAbsolutePath()
                    .replace("%RUNIDENTSTRING", runIdentString),
                    "goldstandards", new File(goldStandard).getParentFile()
                    .getName(), new File(goldStandard).getName());
            if (!(new File(movedGoldStandard).exists())) {
                dataConfig.getGoldstandardConfig().getGoldstandard().copyTo(new File(movedGoldStandard), false);
            }

            // /*
            // * Change the path to the goldstandard in the GoldstandardConfig.
            // */
            // dataConfig.getGoldstandardConfig().getGoldstandard()
            // .setAbsolutePath(new File(movedGoldStandard));
        }

        /*
         * 06.04.2013: create a new data configuration object for use within the
         * runnable bugfix: the runnable so far accessed the old data
         * configuration and program configuration objects. this lead to
         * inconsistent behaviour when accessing internal attributes, because
         * all threads were operating on the same objects.
         */
        DataConfig newDataConfig = dataConfig.clone();
        newDataConfig.setAbsolutePath(copiedDataConfig);
        newDataConfig.getDatasetConfig().setAbsolutePath(copiedDataSetConfig);
        newDataConfig.getDatasetConfig().getDataSet()
                .setAbsolutePath(new File(movedInput));
        if (newDataConfig.hasGoldStandardConfig()) {
            newDataConfig.getGoldstandardConfig().setAbsolutePath(
                    copiedGoldstandardConfig);
            newDataConfig.getGoldstandardConfig().getGoldstandard()
                    .setAbsolutePath(new File(movedGoldStandard));
        }
        ProgramConfig newProgramConfig = programConfig.clone();
        newProgramConfig.setAbsolutePath(copiedProgramConfig);

        /*
         * Start a thread with the invocation line and a path to the log file.
         * The RunThread redirects all the output of the program into the
         * logFile.
         */
        final ExecutionRunRunnable t = createRunRunnableFor(runScheduler,
                runCopy, newProgramConfig, newDataConfig, runIdentString,
                false, this.getRunParameterForRunPair(p));
        return t;
    }

    @Override
    protected RunRunnable createAndScheduleRunnableForResumePair(
            RunSchedulerThread runScheduler, int p) {

        File movedConfigsDir = getMovedConfigsDir();

        /*
         * We only operate on this copy, in order to avoid multithreading
         * problems.
         */
        // changed 22.01.2013
        // ExecutionRun runCopy = this.clone();
        ExecutionRun runCopy = this;

        final Pair<ProgramConfig, DataConfig> pair = runCopy.getRunPairs().get(
                p);

        ProgramConfig programConfig = pair.getFirst();
        DataConfig dataConfig = pair.getSecond();

        /*
         * Copy to results directory
         */
        // 06.04.2013: create File objects for later use in order to create new
        // data configuration and program configuration objects
        File copiedDataConfig = new File(FileUtils.buildPath(
                movedConfigsDir.getAbsolutePath(),
                new File(dataConfig.getAbsolutePath()).getName()));
        dataConfig.copyTo(copiedDataConfig, false);

        File copiedDataSetConfig = new File(FileUtils.buildPath(movedConfigsDir
                .getAbsolutePath(), new File(dataConfig.getDatasetConfig()
                        .getAbsolutePath()).getName()));
        dataConfig.getDatasetConfig().copyTo(copiedDataSetConfig, false);

        File copiedGoldstandardConfig = null;
        if (dataConfig.hasGoldStandardConfig()) {
            copiedGoldstandardConfig = new File(FileUtils.buildPath(
                    movedConfigsDir.getAbsolutePath(), new File(dataConfig
                            .getGoldstandardConfig().getAbsolutePath())
                    .getName()));
            dataConfig.getGoldstandardConfig().copyTo(copiedGoldstandardConfig,
                    false);
        }
        File copiedProgramConfig = new File(FileUtils.buildPath(
                movedConfigsDir.getAbsolutePath(),
                new File(programConfig.getAbsolutePath()).getName()));
        programConfig.copyTo(copiedProgramConfig, false);

        String input = dataConfig.getDatasetConfig().getDataSet()
                .getAbsolutePath();

        /*
         * To avoid overwriting of the input or conversion files, we copy it to
         * the results directory (which is unique for this run).
         */
        // changed 01.09.2012, don't create subdirectory for
        // programConfig_dataConfig
        // undo: 26.02.2013: not creating subdirectory lead to a bug of
        // overwriting same dataset file for different dataconfigs
        String movedInput = FileUtils.buildPath(
                new File(runCopy.getRepository().getParent()
                        .getClusterResultsBasePath()).getParentFile()
                .getAbsolutePath()
                .replace("%RUNIDENTSTRING", runIdentString), "inputs",
                programConfig.getName() + "_" + dataConfig.getName(), new File(
                        input).getParentFile().getName(), new File(input)
                .getName());
        if (!(new File(movedInput).exists())) {
            dataConfig.getDatasetConfig().getDataSet()
                    .copyTo(new File(movedInput), false);
        }

        /*
         * Change the path to the input in the DataSetConfig.
         */
        dataConfig.getDatasetConfig().getDataSet()
                .setAbsolutePath(new File(movedInput));

        /*
         * Copy gold standard
         */
        String movedGoldStandard = null;
        if (dataConfig.hasGoldStandardConfig()) {
            String goldStandard = dataConfig.getGoldstandardConfig()
                    .getGoldstandard().getAbsolutePath();
            movedGoldStandard = FileUtils.buildPath(
                    new File(runCopy.getRepository().getParent()
                            .getClusterResultsBasePath()).getParentFile()
                    .getAbsolutePath()
                    .replace("%RUNIDENTSTRING", runIdentString),
                    "goldstandards", new File(goldStandard).getParentFile()
                    .getName(), new File(goldStandard).getName());
            if (!(new File(movedGoldStandard).exists())) {
                dataConfig.getGoldstandardConfig().getGoldstandard()
                        .copyTo(new File(movedGoldStandard), false);
            }
        }
        /*
         * 06.04.2013: create a new data configuration object for use within the
         * runnable bugfix: the runnable so far accessed the old data
         * configuration and program configuration objects. this lead to
         * inconsistent behaviour when accessing internal attributes, because
         * all threads were operating on the same objects.
         */
        DataConfig newDataConfig = dataConfig.clone();
        newDataConfig.setAbsolutePath(copiedDataConfig);
        newDataConfig.getDatasetConfig().setAbsolutePath(copiedDataSetConfig);

        newDataConfig.getDatasetConfig().getDataSet()
                .setAbsolutePath(new File(movedInput));
        if (newDataConfig.hasGoldStandardConfig()) {
            newDataConfig.getGoldstandardConfig().setAbsolutePath(
                    copiedGoldstandardConfig);
            newDataConfig.getGoldstandardConfig().getGoldstandard()
                    .setAbsolutePath(new File(movedGoldStandard));
        }
        ProgramConfig newProgramConfig = programConfig.clone();
        newProgramConfig.setAbsolutePath(copiedProgramConfig);

        /*
         * Start a thread with the invocation line and a path to the log file.
         * The RunThread redirects all the output of the program into the
         * logFile.
         */
        final ExecutionRunRunnable t = createRunRunnableFor(runScheduler,
                runCopy, newProgramConfig, newDataConfig, runIdentString, true,
                this.getRunParameterForRunPair(p));
        return t;

    }

    protected Map<ProgramParameter<? extends Object>, String> getRunParameterForRunPair(
            final int p) {
        return this.parameterValues.get((int) Math.round(Math.floor(p
                / (double) this.dataConfigs.size())));
    }

    /**
     * This is a helper method for the
     * {@link #createAndScheduleRunnableForResumePair(RunSchedulerThread, int)}
     * and {@link #createAndScheduleRunnableForRunPair(RunSchedulerThread, int)}
     * methods. This method is responsible to instanciate objects of the
     * corresponding runnable runtime type for the runtime type of this run.
     * Override it in your own sub type of the ExecutionRun class.
     *
     * @param runScheduler   The run scheduler to which the newly created runnable
     *                       should be passed.
     * @param run            A reference to a cloned copy of this run that should be
     *                       executed.
     * @param programConfig  The program configuration that is used by the
     *                       resulting runnable.
     * @param dataConfig     The data configuration that is used by the resulting
     *                       runnable.
     * @param runIdentString The unique run identification string, that
     *                       identifies this execution of the run.
     * @param isResume       A boolean which indicates, whether the created runnable
     *                       should perform a run or resume one.
     * @param runParams
     * @return The runnable being executed asynchronously.
     */
    protected abstract ExecutionRunRunnable createRunRunnableFor(
            RunSchedulerThread runScheduler, Run run,
            ProgramConfig programConfig, DataConfig dataConfig,
            String runIdentString, boolean isResume,
            Map<ProgramParameter<?>, String> runParams);

    /**
     * This method verifies that all quality measures can be calculated for
     * every data configuration. This can be due to the fact, that some quality
     * measures require a goldstandard. If a data configuration does not contain
     * a goldstandard, such quality measures cannot be calculated.
     *
     * @param dataConfigs     The data configurations to check.
     * @param qualityMeasures The quality measures to check.
     * @throws RunException An exception that indicates, that some quality
     *                      measures and data configurations are not compatible.
     */
    public static void checkCompatibilityQualityMeasuresDataConfigs(
            final List<IDataConfig> dataConfigs,
            final List<ClusteringEvaluation> qualityMeasures)
            throws RunException {
        /*
         * Check whether some dataconfigs don't have goldstandards but quality
         * measures require them
         */

        Set<ClusteringEvaluation> qualityMeasuresRequireGS = new HashSet<>();
        for (ClusteringEvaluation qualityMeasure : qualityMeasures) {
            if (qualityMeasure.requiresGoldstandard()) {
                qualityMeasuresRequireGS.add(qualityMeasure);
            }
        }

        Set<IDataConfig> dataConfigsWithoutGS = new HashSet<>();
        for (IDataConfig dataConfig : dataConfigs) {
            if (!dataConfig.hasGoldStandardConfig()
                    && qualityMeasuresRequireGS.size() > 0) {
                dataConfigsWithoutGS.add(dataConfig);
            }
        }

        if (dataConfigsWithoutGS.size() > 0) {
            throw new RunException(
                    "This Run contains dataconfigs that do not provide goldstandards "
                    + dataConfigsWithoutGS
                    + ", but also ClusteringQualityMeasures that require goldstandards "
                    + qualityMeasuresRequireGS);
        }
    }

    /**
     * For every pair of program and data configuration we create an object and
     * add it to the list of runpairs.
     *
     * <p>
     * This method clones the data and program configurations of this execution
     * run, such that only cloned objects are passed to the threads. This
     * ensures, that changes that happen to the configurations during runtime do
     * not affect the runs currently performing.
     *
     * @param programConfigs The list of program configurations of this run.
     * @param dataConfigs    The list of data configurations of this run.
     * @throws RegisterException
     */
    protected void initRunPairs(final List<ProgramConfig> programConfigs,
            final List<IDataConfig> dataConfigs) throws RegisterException {

        this.programConfigs = programConfigs;
        this.dataConfigs = dataConfigs;

        this.runPairs = new ArrayList<>();

        for (ProgramConfig programConfig : this.programConfigs) {
            for (IDataConfig dataConfig : this.dataConfigs) {

                runPairs.add(new Pair<>(new ProgramConfig(programConfig),
                        new DataConfig(dataConfig)));
            }
        }
    }

    /**
     * @return The list of runpairs consisting of program and data
     *         configurations each.
     */
    public List<Pair<ProgramConfig, DataConfig>> getRunPairs() {
        return this.runPairs;
    }

    /**
     * @return A list of maps containing parameter values. Every entry of the
     *         list corresponds to one runpair.
     */
    public List<Map<ProgramParameter<?>, String>> getParameterValues() {
        return parameterValues;
    }

    /**
     * @return
     */
    public List<RunResultPostprocessor> getPostProcessors() {
        return this.postProcessors;
    }

    /**
     * @return A list containing all program configurations of this run.
     */
    public List<ProgramConfig> getProgramConfigs() {
        return this.programConfigs;
    }

    /**
     * @return A list containing all data configurations of this run.
     */
    public List<IDataConfig> getDataConfigs() {
        return this.dataConfigs;
    }

    /**
     * @return A list containing all clustering quality measures to be evaluated
     *         during execution of this run.
     */
    public List<ClusteringEvaluation> getQualityMeasures() {
        return this.qualityMeasures;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.RepositoryObject#notify(utils.RepositoryEvent)
     */
    @Override
    public void notify(RepositoryEvent e) throws RegisterException {
        if (e instanceof RepositoryReplaceEvent) {
            RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
            if (event.getOld().equals(this)) {
                super.notify(event);
            } else if (event.getOld() instanceof DataConfig) {
                event.getOld().removeListener(this);
                if (this.dataConfigs.remove(event.getOld())) {
                    this.log.info("Run "
                            + this.getName()
                            + ": DataConfig reloaded due to modifications in filesystem");
                    event.getReplacement().addListener(this);
                    this.dataConfigs.add((DataConfig) event
                            .getReplacement());

                    initRunPairs(programConfigs, dataConfigs);
                }
            } else if (event.getOld() instanceof ProgramConfig) {
                event.getOld().removeListener(this);
                if (this.programConfigs.remove(event.getOld())) {
                    this.log.info("Run "
                            + this.getName()
                            + ": ProgramConfig reloaded due to modifications in filesystem");
                    event.getReplacement().addListener(this);
                    this.programConfigs.add((ProgramConfig) event
                            .getReplacement());

                    initRunPairs(programConfigs, dataConfigs);
                }
            }
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            if (event.getRemovedObject().equals(this)) {
                super.notify(event);
            } else if (this.programConfigs.contains(event.getRemovedObject())) {
                event.getRemovedObject().removeListener(this);
                this.log.info("Run " + this
                        + ": Removed, because ProgramConfig "
                        + event.getRemovedObject() + " was removed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
                        this);
                this.notify(newEvent);
                this.unregister();
            } else if (this.dataConfigs.contains(event.getRemovedObject())) {
                event.getRemovedObject().removeListener(this);
                this.log.info("Run " + this
                        + ": Removed, because DataConfig "
                        + event.getRemovedObject() + " was removed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
                        this);
                this.notify(newEvent);
                this.unregister();
            } else if (this.qualityMeasures.contains(event
                    .getRemovedObject())) {
                event.getRemovedObject().removeListener(this);
                this.log.info("Run "
                        + this
                        + ": Removed, because ClusteringQualityMeasure "
                        + event.getRemovedObject().getClass()
                        .getSimpleName() + " was removed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
                        this);
                this.notify(newEvent);
                this.unregister();
            }
        }
    }

    public boolean hasMaxExecutionTime(final ProgramConfig pc) {
        return this.maxExecutionTimes.containsKey(pc.getName());
    }

    public int getMaxExecutionTime(final ProgramConfig pc) {
        return this.maxExecutionTimes.get(pc.getName());
    }
}
