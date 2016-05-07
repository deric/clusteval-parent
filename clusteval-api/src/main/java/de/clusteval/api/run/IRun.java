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

import de.clusteval.api.IContext;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.repository.IRepositoryObject;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author deric
 */
public interface IRun extends IRepositoryObject {

    /**
     * The name of a run is used to uniquely identify runs.
     *
     * <p>
     * The name of a run is deduced from the filename of the run file. It is the
     * filename without file extension.
     *
     * @return the name
     */
    String getName();

    List<IRunRunnable> getRunRunnables();

    /**
     * @return The number of run runnables this run will create. This number
     *         will be used in the {@link #doPerform(RunSchedulerThread)} method to
     *         create the correct number of runnables.
     */
    int getNumberOfRunRunnables();

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
    float getPercentFinished();

    /**
     * @see #runIdentString
     * @return The unique run identification string created when this run is
     *         executed.
     */
    String getRunIdentificationString();

    /**
     * Gets the results.
     *
     * @return Get the list of run results that are produced by the execution of
     *         this run.
     */
    List<IRunResult> getResults();

    /**
     * Sets the status of this run.
     *
     * @param status
     */
    void setStatus(RUN_STATUS status);

    /**
     * This method terminates the execution of this run. It waits for the
     * termination of all corresponding threads.
     *
     * @return True if everything, including all corresponding threads, could be
     *         terminated, false otherwise.
     */
    boolean terminate();

    IContext getContext();

    IRun clone();

    void addSubProgress(IProgress progress, long partOfSubProgress);

    /**
     * @see #status
     * @return the status
     */
    RUN_STATUS getStatus();

    /**
     * Implement this method in subclasses to provide the number of steps this
     * run performs before it is finished. This method is then later on used by
     * the method {@link #getPercentFinished()} to calculate the finished
     * percentage.
     *
     * @return
     */
    long getUpperLimitProgress();

    OptStatus getOptimizationStatus();

    void perform(final IScheduler runScheduler)
            throws IOException, RunRunnableInitializationException,
                   RunInitializationException, UnknownProviderException;

    /**
     * This method will resume a previously started run. This method should only
     * be invoked on a run, that was parsed from a runresult folder. Otherwise
     * it will show unexpected behaviour.
     *
     * @param runScheduler   The run scheduler, this run should be executed by.
     * @param runIdentString The unique run identifier of the results directory,
     *                       corresponding to an execution of a run, that should by resumed.
     *
     * @throws MissingParameterValueException If a parameter required in the
     * invocation line of some program, is neither set in the program nor in the
     * run configuration, an exception will be thrown.
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws NoRunResultFormatParserException For every
     * {@link RunResultFormat} there needs to be a parser, that converts this
     * format into the default format of the framework for later analysis. If no
     * such parser exists for some format, this exception will be thrown.
     * @throws RunRunnableInitializationException
     * @throws RunInitializationException
     * @throws de.clusteval.api.factory.UnknownProviderException
     */
    void resume(final IScheduler runScheduler, final String runIdentString)
            throws MissingParameterValueException, IOException, NoRunResultFormatParserException,
                   RunRunnableInitializationException, RunInitializationException, UnknownProviderException;
}
