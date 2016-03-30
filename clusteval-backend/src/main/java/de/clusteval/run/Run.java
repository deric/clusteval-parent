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

import de.clusteval.api.IContext;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import de.clusteval.api.run.IProgress;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import de.clusteval.api.run.IRunRunnable;
import de.clusteval.api.run.IScheduler;
import de.clusteval.api.run.RUN_STATUS;
import de.clusteval.context.Context;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.result.ClusteringRunResult;
import de.clusteval.run.result.NoRunResultFormatParserException;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.run.runnable.RunRunnable;
import de.clusteval.run.runnable.RunRunnableInitializationException;
import de.clusteval.utils.ProgressPrinter;
import de.wiwie.wiutils.file.FileUtils;
import de.wiwie.wiutils.format.Formatter;
import de.wiwie.wiutils.utils.Pair;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * A representation of an abstract run including configurations and results. A
 * run is an entity, that can be performed by the framework. Depending on the
 * concrete subclass of the run, actions during execution differ. After
 * execution of the run results are stored in {@link #results}.
 *
 * <p>
 * A run corresponds to a *.run-file on the file system in the run-directory of
 * the repository. The name of the run is deduced from the filesystem in
 * {@link #getName()}, thus it is unique for every repository.
 *
 * <p>
 * Every time a run is performed, its results are also stored in a new
 * subdirectory in the results-directory of the repository. The subdirectory is
 * named after the {@link #runIdentString}, which consists of a time-stamp and
 * the name of this run.
 *
 * <p>
 * When a run is performed, it's divided into a number of atomic operations that
 * can be performed in parallel, objects of subclasses of {@link RunRunnable}.
 * Those are then passed to the run scheduler and are performed in any order in
 * parallel.
 *
 * @author Christian Wiwie
 *
 */
public abstract class Run extends RepositoryObject implements IRun {

    /**
     * This method is invoked by different copy-constructors of subclasses of
     * this Run, e.g. by ExecutionRun.ExecutionRun(ExecutionRun) to clone a
     * given run object.
     *
     * <p>
     * It is a convenience method to clone a map containing parameter values.
     *
     * @param parameterValues
     * @return
     */
    protected static List<Map<IProgramParameter<?>, String>> cloneParameterValues(
            List<Map<IProgramParameter<?>, String>> parameterValues) {
        List<Map<IProgramParameter<?>, String>> result = new ArrayList<>();

        parameterValues.stream().map((map) -> {
            Map<IProgramParameter<?>, String> copyMap = new HashMap<>();
            map.entrySet().stream().forEach((entry) -> {
                copyMap.put(entry.getKey().clone(), entry.getValue() + "");
            });
            return copyMap;
        }).forEach((copyMap) -> {
            result.add(copyMap);
        });

        return result;
    }

    /**
     * The starting time of the execution of this run. Is used to calculate the
     * duration of the execution afterwards.
     */
    protected long startTime;

    /**
     * Unique identifier of this run - consists of a time & date stamp, as well
     * as the name of the run. This string is inserted into the results path of
     * every run, to avoid overwriting of any files (input, output, whatsoever)
     * between several runs.
     */
    protected String runIdentString;

    /**
     * After this run was performed using the {@link #perform()} method, all the
     * results are stored in this list. If this run is a ExecutionRun this list
     * contains one {@link ClusteringRunResult} object for every executed
     * combination of program and dataset.
     */
    protected List<IRunResult> results;

    /**
     * The path to the log file in the results-directory of the execution of
     * this run.
     */
    protected String logFilePath;

    /**
     * Keeps track of the progress of this run when it is executed. Can be used
     * to get a percental status.
     */
    protected volatile ProgressPrinter progress;

    /**
     * The status of this run.
     * <p>
     * Initially when a Run object is created its status is
     * {@link RUN_STATUS.INACTIVE}.
     *
     * <p>
     * When a Run should be performed it is passed to the run scheduler. Then
     * the runs status is {@link RUN_STATUS.SCHEDULED}.
     *
     * <p>
     * As soon as the run is started by the scheduler, the runs status is
     * {@link RUN_STATUS.RUNNING}.
     *
     * <p>
     * After the runs completion its status is {@link RUN_STATUS.FINISHED}.
     *
     * <p>
     * When the run is terminated by the user during its execution and before
     * it's finished its status is {@link RUN_STATUS.TERMINATED}.
     */
    protected RUN_STATUS status;

    /**
     * Contains the runnable objects created during the execution of this run.
     */
    protected List<IRunRunnable> runnables;

    /**
     * Every run belongs to a context.
     */
    protected Context context;

    /**
     * The constructor of this class takes a date and configuration. It is
     * protected, to force usage of the static method
     *
     * @param repository the repository
     * @param context    The context of this run
     * @param changeDate The date this run was performed.
     * @param absPath    The absolute path to the file on the filesystem that
     *                   corresponds to this run.
     * @throws RegisterException
     */
    protected Run(final IRepository repository, final Context context,
            final long changeDate, final File absPath) throws RegisterException {
        super(repository, false, changeDate, absPath);

        this.runnables = new ArrayList<>();
        this.status = RUN_STATUS.INACTIVE;
        this.context = context;
    }

    /**
     * A copy constructor for the Run class.
     *
     * <p>
     * Runnables, run results and status of the given run are not copied into
     * the new run.
     *
     * @param otherRun The run to clone.
     * @throws RegisterException
     */
    protected Run(final Run otherRun) throws RegisterException {
        super(otherRun);

        this.runnables = new ArrayList<>();
        this.status = RUN_STATUS.INACTIVE;
        this.context = otherRun.context;
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public abstract Run clone();

    /**
     * This method is invoked by {@link #perform(RunSchedulerThread)} after
     * completion of {@link #doPerform(RunSchedulerThread)}.
     *
     * <p>
     * Override this method to do any postcalculations on the run results after
     * everything is finished.
     */
    protected void afterPerform() {
        FileUtils.appendStringToFile(
                this.logFilePath,
                Formatter.currentTimeAsString(true, "MM_dd_yyyy-HH_mm_ss",
                        Locale.UK)
                + "\tFinished run \""
                + this.getName()
                + "\" (Duration "
                + Formatter.formatMsToDuration(System
                        .currentTimeMillis() - startTime)
                + ")"
                + System.getProperty("line.separator"));
        this.log.info("Run " + this + " - All processes finished");
        this.setStatus(RUN_STATUS.FINISHED);
    }

    /**
     * This method is invoked by {@link #resume(RunSchedulerThread, String)}
     * after completion of {@link #doResume(RunSchedulerThread, String)}.
     *
     * <p>
     * Override this method to do any postcalculations on the run results after
     * everything is finished.
     *
     * @param runIdentString The unique run identifier of the results directory,
     *                       corresponding to an execution of a run, that should by resumed.
     */
    @SuppressWarnings("unused")
    protected void afterResume(final String runIdentString) {
        FileUtils.appendStringToFile(
                this.logFilePath,
                Formatter.currentTimeAsString(true, "MM_dd_yyyy-HH_mm_ss",
                        Locale.UK)
                + "\tFinished run \""
                + this.getName()
                + "\" (Duration "
                + Formatter.formatMsToDuration(System
                        .currentTimeMillis() - startTime)
                + ")"
                + System.getProperty("line.separator"));
        this.log.info("Run " + this + " - All processes finished");
        this.setStatus(RUN_STATUS.FINISHED);
    }

    /**
     * This method is invoked by {@link #perform(RunSchedulerThread)} before
     * {@link #doPerform(RunSchedulerThread)} is invoked.
     *
     * <p>
     * Override this method in subclasses to do any operation, that should only
     * be done once per run execution, and before any runnable is started. This
     * can be useful for logging or for filesystem operations like copying any
     * input files to avoid overriding files unintentionally when they would be
     * performed in the runnables asynchronously instead.
     *
     * @throws java.io.IOException
     * @throws RunInitializationException
     */
    protected void beforePerform() throws IOException, RunInitializationException {
        log.info("Starting run \"" + this.getName() + "\"");
        log.info("Run mode is \"" + this.getClass().getSimpleName() + "\"");
        /*
         * Change the status of this run
         */
        // this.setStatus(RUN_STATUS.RUNNING);

        /*
         * Initialize a ProgressPrinter, which keeps the progression of the run
         */
        this.progress = new ProgressPrinter(getUpperLimitProgress(), true);

        initRunIdentificationString();

        this.startTime = System.currentTimeMillis();

        this.copyConfigurationFiles(false);

        this.logFilePath = FileUtils.buildPath(new File(this.getRepository()
                .getClusterResultsBasePath()).getParentFile().getAbsolutePath()
                .replace("%RUNIDENTSTRING", runIdentString), "logs",
                runIdentString + ".log");
        if (!new File(this.logFilePath).exists()) {
            new File(this.logFilePath).getParentFile().mkdirs();
            new File(this.logFilePath).createNewFile();
        }

        FileUtils.appendStringToFile(
                this.logFilePath,
                Formatter.currentTimeAsString(true, "MM_dd_yyyy-HH_mm_ss",
                        Locale.UK)
                + "\tStarting run \""
                + this.getName()
                + "\"" + System.getProperty("line.separator"));
        /*
         * All threads are stored in this list, to be able to wait for them
         * asynchronously later on.
         */
        this.runnables.clear();

        /*
         * Reset the results list
         */
        this.results = new ArrayList<>();
    }

    /**
     * This method is invoked by {@link #resume(RunSchedulerThread, String)}
     * before {@link #doResume(RunSchedulerThread, String)} is invoked.
     *
     * <p>
     * Override this method in subclasses to do any operation, that should only
     * be done once per run execution, and before any runnable is started. This
     * can be useful for logging or for filesystem operations like copying any
     * input files to avoid overriding files unintentionally when they would be
     * performed in the runnables asynchronously instead.
     *
     * @param runIdentString The unique run identifier of the results directory,
     *                       corresponding to an execution of a run, that should by resumed.
     *
     * @throws RunInitializationException
     */
    protected void beforeResume(final String runIdentString) throws RunInitializationException {
        try {
            log.info("RESUMING run \"" + this.getName() + "\"");
            log.info("Run mode is \"" + this.getClass().getSimpleName() + "\"");
            /*
             * Change the status of this run
             */
            // this.status = RUN_STATUS.RUNNING;

            /*
             * Initialize a ProgressPrinter, which keeps the progression of the
             * run
             */
            this.progress = new ProgressPrinter(getUpperLimitProgress(), true);

            this.runIdentString = runIdentString;

            this.startTime = System.currentTimeMillis();

            this.copyConfigurationFiles(true);

            this.logFilePath = FileUtils.buildPath(
                    new File(this.getRepository().getParent()
                            .getClusterResultsBasePath()).getParentFile()
                    .getAbsolutePath()
                    .replace("%RUNIDENTSTRING", runIdentString),
                    "logs", runIdentString + ".log");
            if (!new File(this.logFilePath).exists()) {
                new File(this.logFilePath).getParentFile().mkdirs();
                new File(this.logFilePath).createNewFile();
            }

            FileUtils.appendStringToFile(
                    this.logFilePath,
                    Formatter.currentTimeAsString(true, "MM_dd_yyyy-HH_mm_ss",
                            Locale.UK)
                    + "\tRESUMING run \""
                    + this.getName()
                    + "\"" + System.getProperty("line.separator"));

            /*
             * All threads are stored in this list, to be able to wait for them
             * asynchronously later on.
             */
            this.runnables.clear();

            /*
             * Reset the results list
             */
            this.results = new ArrayList<>();
        } catch (Exception e) {
            throw new RunInitializationException(e);
        }
    }

    /**
     * When this run is performed, this method copies all configuration files to
     * the results directory.
     * <p>
     * This method is invoked by {@link #beforePerform()} or
     * {@link #beforeResume(String)}. Thus it is not executed asynchronously to
     * avoid overwriting of several threads in the result directory.
     *
     * @param isResume Indicates, whether the execution of this run is a
     *                 resumption or not.
     */
    protected void copyConfigurationFiles(final boolean isResume) {
        /*
         * Copy all the configuration files for later reproducability
         */
        if (!isResume) {
            File movedConfigsDir = getMovedConfigsDir();
            movedConfigsDir.mkdirs();
            this.copyToFolder(movedConfigsDir);
        } else {
            File movedConfigsDir = new File(FileUtils.buildPath(
                    new File(this.getRepository().getParent()
                            .getClusterResultsBasePath()).getParentFile()
                    .getAbsolutePath()
                    .replace("%RUNIDENTSTRING", runIdentString),
                    "configs"));
            movedConfigsDir.mkdirs();
            this.copyToFolder(movedConfigsDir, false);
        }
    }

    /**
     * This method will be invoked by {@link #doResume(RunSchedulerThread)} to
     * create the p'th runnable for the resumption of an execution of this run
     * and to submit it to the run scheduler.
     *
     * @param runScheduler The run scheduler to which the newly created runnable
     *                     should be passed.
     * @param p            The index of the runnable to be created.
     * @return
     * @throws RunRunnableInitializationException
     */
    protected abstract IRunRunnable createAndScheduleRunnableForResumePair(
            RunSchedulerThread runScheduler, int p)
            throws RunRunnableInitializationException;

    /**
     * This method will be invoked by {@link #doPerform(RunSchedulerThread)} to
     * create the p'th runnable for the execution of this run and to submit it
     * to the run scheduler.
     *
     * @param runScheduler The run scheduler to which the newly created runnable
     *                     should be passed.
     * @param p            The index of the runnable to be created.
     * @return
     * @throws RunRunnableInitializationException
     */
    protected abstract RunRunnable createAndScheduleRunnableForRunPair(IScheduler runScheduler, int p)
            throws RunRunnableInitializationException;

    /**
     * This method creates instances of subclasses of RunRunnable and passes
     * them to the run scheduler. The run scheduler then executes the run
     * runnables at some point in the future depending on the available physical
     * ressources.
     *
     * <p>
     * The RunRunnable objects represent atomic peaces of the overall run, which
     * can be executed in parallel.
     *
     * @param runScheduler The run scheduler, this run should be executed by.
     * @throws RunRunnableInitializationException
     */
    public void doPerform(final IScheduler runScheduler)
            throws RunRunnableInitializationException {

        for (int p = 0; p < getNumberOfRunRunnables(); p++) {
            RunRunnable r = createAndScheduleRunnableForRunPair(runScheduler, p);
            this.runnables.add(r);
        }
    }

    /**
     * This method will perform this run, i.e. it combines every program with
     * every dataset contained in this run's configuration. For every such
     * combination the result of this will be added to the list of run results
     *
     * @param runScheduler   The run scheduler, this run should be executed by.
     * @param runIdentString The unique run identifier of the results directory,
     *                       corresponding to an execution of a run, that should by resumed.
     * @throws RunRunnableInitializationException
     */
    @SuppressWarnings("unused")
    public void doResume(final RunSchedulerThread runScheduler,
            final String runIdentString)
            throws RunRunnableInitializationException {

        /*
         * Execute every "runpair", consisting of a program (i.e. its
         * configuration) and a dataset (i.e. its configuration).
         */
        for (int p = 0; p < getNumberOfRunRunnables(); p++) {
            IRunRunnable r = createAndScheduleRunnableForResumePair(
                    runScheduler, p);
            this.runnables.add(r);
        }
    }

    /**
     * @see #logFilePath
     * @return The path to the log file when this run is executed.
     */
    public String getLogFilePath() {
        return this.logFilePath;
    }

    /**
     * This method constructs and returns the path to the configuration
     * subdirectory in the results directory of this run execution.
     *
     * <p>
     * This method should only be invoked while a run is executed, because only
     * then the unique run identification string is set.
     *
     * @return The path to the configuration subdirectory in the results
     *         directory of this run execution.
     */
    protected File getMovedConfigsDir() {
        return new File(FileUtils.buildPath(new File(this.getRepository()
                .getClusterResultsBasePath()).getParentFile().getAbsolutePath()
                .replace("%RUNIDENTSTRING", runIdentString), "configs"));
    }

    /**
     * This method constructs and returns the path to the goldstandard
     * subdirectory in the results directory of this run execution.
     *
     * <p>
     * This method should only be invoked while a run is executed, because only
     * then the unique run identification string is set.
     *
     * @return The path to the goldstandard subdirectory in the results
     *         directory of this run execution.
     */
    protected File getMovedGoldStandardsDir() {
        return new File(FileUtils.buildPath(new File(this.getRepository()
                .getClusterResultsBasePath()).getParentFile().getAbsolutePath()
                .replace("%RUNIDENTSTRING", runIdentString), "goldstandards"));
    }

    /**
     * This method constructs and returns the path to the dataset subdirectory
     * in the results directory of this run execution.
     *
     * <p>
     * This method should only be invoked while a run is executed, because only
     * then the unique run identification string is set.
     *
     * @return The path to the dataset subdirectory in the results directory of
     *         this run execution.
     */
    protected File getMovedInputsDir() {
        return new File(FileUtils.buildPath(new File(this.getRepository()
                .getClusterResultsBasePath()).getParentFile().getAbsolutePath()
                .replace("%RUNIDENTSTRING", runIdentString), "inputs"));
    }

    /**
     * The name of a run is used to uniquely identify runs.
     *
     * <p>
     * The name of a run is deduced from the filename of the run file. It is the
     * filename without file extension.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return absPath.getName().replace(".run", "");
    }

    /**
     * Returns the current status of this run execution in terms of finished
     * percentage. Depending on the current status of the run, this method
     * returns different values:
     * <p>
     * If the run is scheduled, finished or terminated this method always
     * returns 100%.
     * <p>
     * If the run is currently running, the percentage of the execution is
     * returned.
     * <p>
     * Otherwise this method returns 0%.
     *
     * @return The percent finished
     */
    public float getPercentFinished() {
        switch (this.status) {
            case SCHEDULED:
                return 0;
            case RUNNING:
                synchronized (this.progress) {
                    return this.progress.getPercent();
                }
            case FINISHED:
                return 100;
            case TERMINATED:
                return this.progress.getPercent();
            default:
                break;
        }
        return 0;
    }

    /**
     * Gets the results.
     *
     * @return Get the list of run results that are produced by the execution of
     *         this run.
     */
    public List<IRunResult> getResults() {
        return this.results;
    }

    /**
     * @see #runIdentString
     * @return The unique run identification string created when this run is
     *         executed.
     */
    public String getRunIdentificationString() {
        return this.runIdentString;
    }

    /**
     * @return A list containing all run runnables this run created during its
     *         execution.
     */
    @Override
    public List<IRunRunnable> getRunRunnables() {
        return this.runnables;
    }

    /**
     * @see #status
     * @return the status
     */
    @Override
    public RUN_STATUS getStatus() {
        return this.status;
    }

    /**
     * This method sets the unique run identification string of this run when it
     * is executed. This unique identifier is also used, amongst other things,
     * as the name of the results directory of this run.
     */
    private void initRunIdentificationString() {
        this.runIdentString = Formatter.currentTimeAsString(true,
                "MM_dd_yyyy-HH_mm_ss", Locale.UK) + "_" + this.getName();
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
            }
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            if (event.getRemovedObject().equals(this)) {
                super.notify(event);
            }
        }
    }

    /**
     * This method will perform this run.
     * <p>
     * First this method invokes {@link #beforePerform()} and does all necessary
     * preoperations before the actual run is started. Afterwards in
     * {@link #doPerform(RunSchedulerThread)} the runnables are created and
     * executed asynchronously by the run scheduler. Then invoking
     * {@link #waitForRunnablesToFinish()} guarantees that the method waits
     * until all calculations are finished. Last {@link #afterPerform()}
     * performs additional operations with the results after all calculations
     * are finished.
     *
     * @param runScheduler The run scheduler, this run should be executed by.
     *
     * @throws IOException                        Signals that an I/O exception has occurred.
     * @throws RunRunnableInitializationException
     * @throws RunInitializationException
     */
    public void perform(final IScheduler runScheduler)
            throws IOException, RunRunnableInitializationException,
                   RunInitializationException {
        beforePerform();
        doPerform(runScheduler);
        waitForRunnablesToFinish();
        afterPerform();
    }

    /**
     * This method will resume a previously started run. This method should only
     * be invoked on a run, that was parsed from a runresult folder. Otherwise
     * it will show unexpected behaviour.
     *
     * @param runScheduler   The run scheduler, this run should be executed by.
     * @param runIdentString The unique run identifier of the results directory,
     *                       corresponding to an execution of a run, that should by resumed.
     *
     * @throws MissingParameterValueException     If a parameter required in the
     *                                            invocation line of some program, is neither set in the program nor in the
     *                                            run configuration, an exception will be thrown.
     * @throws IOException                        Signals that an I/O exception has occurred.
     * @throws NoRunResultFormatParserException   For every
     *                                            {@link RunResultFormat} there needs to be a parser, that converts this
     *                                            format into the default format of the framework for later analysis. If no
     *                                            such parser exists for some format, this exception will be thrown.
     * @throws RunRunnableInitializationException
     * @throws RunInitializationException
     */
    @SuppressWarnings("unused")
    public void resume(final RunSchedulerThread runScheduler,
            final String runIdentString) throws MissingParameterValueException,
                                                IOException, NoRunResultFormatParserException,
                                                RunRunnableInitializationException, RunInitializationException {
        beforeResume(runIdentString);
        doResume(runScheduler, runIdentString);
        waitForRunnablesToFinish();
        afterResume(runIdentString);
    }

    /**
     * Sets the status of this run.
     *
     * @param status
     */
    public void setStatus(RUN_STATUS status) {
        this.status = status;
        // 31.08.2012
        // update status in DB
        this.getRepository().updateStatusOfRun(this, status.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @return A map with the optimization status of this run.
     */
    public Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>> getOptimizationStatus() {
        return null;
    }

    /**
     * This method is invoked by {@link #perform(RunSchedulerThread)}, after
     * completion of {@link #doPerform(RunSchedulerThread)}.
     *
     * <p>
     * It waits, until all threads (corresponding to created runnables) are
     * finished.
     *
     * <p>
     * During this time, it updates the progress after completion of single
     * threads, such that {@link #getPercentFinished()} returns the correct
     * value.
     *
     * <p>
     * Additionally it checks, whether any of the threads threw exceptions and
     * prints those exceptions.
     *
     */
    protected void waitForRunnablesToFinish() {
        this.log.info("Run " + this + " - Waiting for runs to finish...");
        for (IRunRunnable r : runnables) {
            IRunRunnable t = r;
            try {
                try {
                    t.waitFor();
                } catch (CancellationException | ExecutionException e) {
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return The context of this run.
     */
    @Override
    public IContext getContext() {
        return this.context;
    }

    @Override
    public void addSubProgress(IProgress prog, long partOfSubProgress) {
        progress.addSubProgress(progress, partOfSubProgress);
    }

}
