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
package de.clusteval.run.runnable;

import de.clusteval.api.exceptions.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.framework.RLibraryNotLoadedException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.Run;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Wiwie
 * @param <IW>
 *
 */
public abstract class IterationRunnable<IW extends IterationWrapper> implements Runnable {

    protected Logger log;
    protected IW iterationWrapper;
    // TODO think of nicer design
    protected IOException ioException;
    protected RLibraryNotLoadedException rLibraryException;
    protected RNotAvailableException rNotAvailableException;
    protected InterruptedException interruptedException;

    protected long startTime;

    public IterationRunnable(final IW iterationWrapper) {
        super();
        this.iterationWrapper = iterationWrapper;
        this.log = LoggerFactory.getLogger(getClass());
    }

    /**
     * @return the rNotAvailableException
     */
    public RNotAvailableException getrNotAvailableException() {
        return rNotAvailableException;
    }

    /**
     * @return the ioException
     */
    public IOException getIoException() {
        return ioException;
    }

    /**
     * @return the rLibraryException
     */
    public RLibraryNotLoadedException getrLibraryException() {
        return rLibraryException;
    }

    public InterruptedException getInterruptedException() {
        return interruptedException;
    }

    public RunRunnable getParentRunnable() {
        return this.iterationWrapper.getRunnable();
    }

    public Run getRun() {
        return this.iterationWrapper.getRunnable().getRun();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        beforeRun();
        try {
            doRun();
        } catch (InterruptedException e) {
        } finally {
            afterRun();
        }
    }

    protected void beforeRun() {
        this.startTime = System.currentTimeMillis();

        IRepository repo = getRun().getRepository();
        if (repo instanceof RunResultRepository) {
            repo = repo.getParent();
        }
        RunSchedulerThread scheduler = repo.getSupervisorThread().getRunScheduler();
        scheduler
                .informOnStartedIterationRunnable(Thread.currentThread(), this);
    }

    protected abstract void doRun() throws InterruptedException;

    protected void afterRun() {
        IRepository repo = getRun().getRepository();
        if (repo instanceof RunResultRepository) {
            repo = repo.getParent();
        }
        RunSchedulerThread scheduler = repo.getSupervisorThread().getRunScheduler();
        scheduler.informOnFinishedIterationRunnable(Thread.currentThread(),
                this);
    }

    public long getStartTime() {
        return this.startTime;
    }
}
