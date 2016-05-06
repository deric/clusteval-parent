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
package de.clusteval.run;

import de.clusteval.api.IContext;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunRunnable;
import de.clusteval.api.run.IScheduler;
import de.clusteval.api.run.RunRunnable;
import de.clusteval.api.stats.RunStatistic;
import de.clusteval.run.runnable.RunAnalysisRunRunnable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A type of analysis run that conducts analyses of run results.
 *
 * <p>
 * A run analysis run has a list of unique run identifiers in
 * {@link #uniqueRunAnalysisRunIdentifiers} , that should be assessed during
 * execution of the run. Additionally they inherit a list of run statistics in
 * {@link AnalysisRun#statistics} that should be assessed for every run result
 * corresponding to a unique run identifier.
 *
 * @author Christian Wiwie
 *
 */
public class RunAnalysisRun extends AnalysisRun<RunStatistic> {

    /**
     * A list of unique run identifiers, that should be assessed during
     * execution of the run
     */
    protected List<String> uniqueRunAnalysisRunIdentifiers;

    /**
     * @param repository
     *                             The repository this run should be registered at.
     * @param context
     * @param changeDate
     *                             The date this run was performed.
     * @param absPath
     *                             The absolute path to the file on the filesystem that
     *                             corresponds to this run.
     * @param uniqueRunIdentifiers
     *                             The list of unique run identifiers, that should be assessed
     *                             during execution of the run.
     * @param statistics
     *                             The statistics that should be assessed for the objects of
     *                             analysis.
     * @throws RegisterException
     */
    public RunAnalysisRun(IRepository repository, final IContext context,
            long changeDate, File absPath, List<String> uniqueRunIdentifiers,
            List<RunStatistic> statistics) throws RegisterException {
        super(repository, context, changeDate, absPath, statistics);
        this.uniqueRunAnalysisRunIdentifiers = uniqueRunIdentifiers;

        if (this.register()) {
            for (RunStatistic statistic : this.statistics) {
                // added 21.03.2013
                statistic.register();
                statistic.addListener(this);
            }
        }
    }

    /**
     * Copy constructor of run analysis runs.
     *
     * @param other
     *              The run analysis run to be cloned.
     * @throws RegisterException
     */
    protected RunAnalysisRun(final RunAnalysisRun other)
            throws RegisterException {
        super(other);

        this.uniqueRunAnalysisRunIdentifiers = new ArrayList<>();

        for (String s : other.uniqueRunAnalysisRunIdentifiers) {
            this.uniqueRunAnalysisRunIdentifiers.add(s);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see run.AnalysisRun#cloneStatistics(java.util.List)
     */
    @Override
    protected List<RunStatistic> cloneStatistics(List<RunStatistic> statistics) {
        final List<RunStatistic> result = new ArrayList<>();

        for (RunStatistic st : statistics) {
            result.add(st.clone());
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.Run#clone()
     */
    @Override
    public RunAnalysisRun clone() {
        try {
            return new RunAnalysisRun(this);
        } catch (RegisterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.Run#terminate()
     */
    @Override
    public boolean terminate() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.Run#getUpperLimitProgress()
     */
    @Override
    public long getUpperLimitProgress() {
        return this.statistics.size() * 100;
    }

    @Override
    protected IRunRunnable createAndScheduleRunnableForRunPair(IScheduler runScheduler, int p) {
        /*
         * We only operate on this copy, in order to avoid multithreading
         * problems.
         */
        RunAnalysisRun runCopy = this.clone();

        String uniqueRunIdentifier = this.getUniqueRunAnalysisRunIdentifiers()
                .get(p);

        /*
         * Start a thread with the invocation line and a path to the log file.
         * The RunThread redirects all the output of the pr ogram into the
         * logFile.
         */
        final RunRunnable t = new RunAnalysisRunRunnable(runScheduler, runCopy,
                runIdentString, false, uniqueRunIdentifier,
                runCopy.getStatistics());
        return t;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.Run#getNumberOfRunRunnables()
     */
    @Override
    public int getNumberOfRunRunnables() {
        return this.getUniqueRunAnalysisRunIdentifiers().size();
    }

    @Override
    protected RunRunnable createAndScheduleRunnableForResumePair(IScheduler runScheduler, int p) {

        /*
         * We only operate on this copy, in order to avoid multithreading
         * problems.
         */
        // changed 13.02.2013
        // RunAnalysisRun runCopy = this.clone();
        RunAnalysisRun runCopy = this;

        String uniqueRunIdentifier = this.getUniqueRunAnalysisRunIdentifiers()
                .get(p);

        /*
         * Start a thread with the invocation line and a path to the log file.
         * The RunThread redirects all the output of the pr ogram into the
         * logFile.
         */
        final RunRunnable t = new RunAnalysisRunRunnable(runScheduler, runCopy,
                runIdentString, true, uniqueRunIdentifier,
                runCopy.getStatistics());
        return t;
    }

    /**
     * @return A list with all unique run identifiers of this run.
     */
    public List<String> getUniqueRunAnalysisRunIdentifiers() {
        return this.uniqueRunAnalysisRunIdentifiers;
    }
}
