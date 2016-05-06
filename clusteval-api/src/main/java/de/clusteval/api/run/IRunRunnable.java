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

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.RunIterationException;
import de.clusteval.api.opt.ParameterOptimizationMethod;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author deric
 * @param <IR>
 * @param <IW>
 */
public interface IRunRunnable<IR extends IterationRunnable, IW extends IterationWrapper> extends Runnable {

    /**
     *
     * @return unique identifier
     */
    String getName();

    void init(IScheduler runScheduler, IRun run, IProgramConfig programConfig, IDataConfig dataConfig,
            ParameterOptimizationMethod optimizationMethod,
            String runIdentString, boolean isResume,
            Map<IProgramParameter<?>, String> runParams);

    IRun getRun();

    void terminate();

    /**
     * @return The progress printer of this runnable.
     * @see #progress
     */
    IProgress getProgress();

    boolean hasNextIteration();

    int consumeNextIteration() throws RunIterationException;

    void doRunIteration(IW iterationWrapper) throws RunIterationException;

    /**
     * This method causes the caller to wait for this runnable's thread to
     * finish its execution.
     *
     * <p>
     * This method also waits in case this runnable has not yet been started
     * (the future object has not yet been initialized).
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    void waitFor() throws InterruptedException, ExecutionException;

    /**
     * @return A list with all exceptions thrown during execution of this
     *         runnable.
     * @see #exceptions
     */
    List<Throwable> getExceptions();

    /**
     * The future object of a runnable is only initialized, when it has been
     * started.
     *
     * @return The future object of this runnable.
     * @see #future
     */
    Future<?> getFuture();

    /**
     * @return Get the optimization method of this parameter optimization run
     *         runnable.
     * @see #optimizationMethod
     */
    ParameterOptimizationMethod getOptimizationMethod();

    /**
     * @return The program configuration of this runnable.
     */
    IProgramConfig getProgramConfig();

    /**
     * @return The data configuration of this runnable.
     */
    IDataConfig getDataConfig();
}
