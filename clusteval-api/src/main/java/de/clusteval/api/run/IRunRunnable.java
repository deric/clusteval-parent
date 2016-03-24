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

import de.clusteval.api.exceptions.RunIterationException;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author deric
 * @param <IR>
 * @param <IW>
 */
public interface IRunRunnable<IR extends IterationRunnable, IW extends IterationWrapper> extends Runnable {

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
}
