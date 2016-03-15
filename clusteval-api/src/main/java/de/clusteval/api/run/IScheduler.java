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

import de.clusteval.api.repository.IRun;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Runs scheduler
 *
 * @author deric
 */
public interface IScheduler {

    /**
     *
     * @return A collection of runs, that have been executed or resumed.
     */
    public Set<IRun> getRuns();

    /**
     * This method takes a {@link RunRunnable} and adds it to the thread pool of
     * this run scheduler thread. The thread pool then determines, when the
     * runnable can and will be performed depending on the available resources.
     *
     * @param t
     * @param runnable The new runnable to perform.
     *
     * @return A future object, that allows to retrieve the current status of
     *         the execution of the runnable.
     */
    //public Future<?> registerRunRunnable(RunRunnable runRunnable);
    void informOnStartedIterationRunnable(final Thread t, final IterationRunnable runnable);

    void informOnFinishedIterationRunnable(final Thread t, final IterationRunnable runnable);

    void updateThreadPoolSize(final int numberThreads);

    Future<?> registerIterationRunnable(IterationRunnable iterationRunnable);

}
