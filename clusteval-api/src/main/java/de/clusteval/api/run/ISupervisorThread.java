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

import de.clusteval.framework.ClustevalThread;

/**
 *
 * @author deric
 */
public interface ISupervisorThread {

    /**
     * Run, Forrest, run
     */
    void run();

    void interrupt();

    IScheduler getRunScheduler();

    /**
     * @param clazz
     *              The class for which we want the thread instance
     * @return The thread instance of the passed class.
     */
    ClustevalThread getThread(Class<? extends ClustevalThread> clazz);

    /**
     * Tests if this thread is alive. A thread is alive if it has
     * been started and has not yet died.
     *
     * @return <code>true</code> if this thread is alive;
     *         <code>false</code> otherwise.
     */
    boolean isAlive();

}
