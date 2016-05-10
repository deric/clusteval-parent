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
package de.clusteval.framework.threading;

import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IScheduler;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.data.dataset.DataSetConfigFinderThread;
import de.clusteval.framework.ClustevalThread;
import de.clusteval.run.RunFinderThread;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This supervisor thread is responsible for starting and keeping alive all
 * other threads of the framework. He continuously checks whether the other
 * threads are still alive as they should. In case any of those threads
 * terminates in an uncontrolled way, the supervisor thread restarts a new
 * thread of the corresponding class of threads that was terminated
 * unexpectedly.
 *
 * @author Christian Wiwie
 *
 */
public abstract class SupervisorThread extends Thread implements ISupervisorThread {

    protected boolean interrupted;

    /**
     * @param classes
     *                The clusteval thread classes to add to a list.
     * @return A list of clusteval thread classes.
     */
    public static List<Class<? extends ClustevalThread>> createList(
            Class<? extends ClustevalThread>... classes) {
        List<Class<? extends ClustevalThread>> result = new ArrayList<>();
        result.addAll(Arrays.asList(classes));
        return result;
    }

    /**
     * The repository this supervisor belongs to and for which all threads
     * should be supervised.
     */
    protected IRepository repository;

    /**
     * A map containing all threads that were started by this supervisor
     * together with their thread class.
     *
     * <p>
     * This map is used by the supervisor to iterate through the existing
     * threads and checking whether they are still alive. If the supervisor
     * thread finds a thread in this map that terminated unexpectedly, he
     * creates a new thread of this class.
     */
    final protected Map<Class<? extends ClustevalThread>, ClustevalThread> threads;

    /**
     * The time between two checks of this thread.
     */
    protected long supervisorSleepTime;

    /**
     * A boolean indicating, whether this thread only should check once and then
     * terminate. This can be useful in some subclasses of this class (e.g.
     * {@link RunResultRepositorySupervisorThread}).
     */
    protected boolean checkOnce;

    protected Logger log;

    protected final Map<String, Long> threadSleepTimes;

    /**
     * Constructor of abstract supervisor threads.
     *
     * @param repository
     *                         The repository this supervisor belongs to and for which all
     *                         threads should be supervised.
     * @param toStart
     *                         A list containing all threads this supervisor thread should
     *                         start and keep alive.
     * @param threadSleepTimes
     *                         A map containing sleep times for threads (must not be
     *                         complete).
     * @param checkOnce
     *                         A boolean indicating, whether this thread only should check
     *                         once and then terminate. This can be useful in some subclasses
     *                         of this class (e.g.
     *                         {@link RunResultRepositorySupervisorThread}).
     */
    public SupervisorThread(final IRepository repository,
            final List<Class<? extends ClustevalThread>> toStart,
            final Map<String, Long> threadSleepTimes, final boolean checkOnce) {
        super();
        this.setName(this.getName().replace("Thread", "Supervisor"));
        this.log = LoggerFactory.getLogger(this.getClass());
        this.threadSleepTimes = threadSleepTimes;
        this.repository = repository;
        this.supervisorSleepTime = threadSleepTimes
                .containsKey("SupervisorThread") ? threadSleepTimes
                .get("SupervisorThread")
                                   : // if not specified, check every 5 seconds
                5000;
        this.checkOnce = checkOnce;

        this.threads = new LinkedHashMap<>();

        synchronized (this.threads) {
            for (Class<? extends ClustevalThread> thread : toStart) {
                try {
                    long sleep = getSleepTime(thread);
                    ClustevalThread inst = thread.getConstructor(
                            ISupervisorThread.class, IRepository.class,
                            long.class, boolean.class).newInstance(
                                    this, repository, sleep, checkOnce);
                    this.threads.put(thread, inst);
                } catch (IllegalArgumentException | SecurityException | InstantiationException |
                         IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private long getSleepTime(Class<? extends ClustevalThread> thread) {
        if (threadSleepTimes.containsKey(thread.getSimpleName())) {
            return threadSleepTimes.get(thread.getSimpleName());
        }
        //default sleep time for most threads
        return 30000;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        // check, that all threads are running
        while (!this.isInterrupted()) {
            try {
                synchronized (this.threads) {
                    // changed at 08.05.2012
                    for (Class<? extends ClustevalThread> threadClass : this.threads
                            .keySet()) {
                        if (this.threads.get(threadClass) == null
                                || (this.threads.get(threadClass).getState()
                                .equals(State.TERMINATED) && !checkOnce)) {
                            this.log.warn("Restarting "
                                    + threadClass.getSimpleName());
                            Constructor<? extends ClustevalThread> constr;
                            try {
                                constr = threadClass.getConstructor(
                                        ISupervisorThread.class,
                                        IRepository.class, long.class, boolean.class);
                                this.threads.put(threadClass, constr
                                        .newInstance(this, repository, getSleepTime(threadClass), false));
                            } catch (NoSuchMethodException | SecurityException |
                                     InstantiationException | IllegalAccessException |
                                     IllegalArgumentException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (checkOnce && this.repository.isInitialized()) {
                    return;
                }
                sleep(this.supervisorSleepTime);
            } catch (InterruptedException e) {
                this.interrupt();
            } // added 12.12.2012
            catch (ConcurrentModificationException e) {
                e.printStackTrace();
                // do nothing
            }
        }
    }

    @Override
    // TODO: fixme: not all threads (iteration/runrunnable threads?!) are terminated
    public void interrupt() {
        this.interrupted = true;
        synchronized (this.threads) {
            Map.Entry<Class<? extends ClustevalThread>, ClustevalThread>[] entries = this.threads
                    .entrySet().toArray(new Map.Entry[0]);
            for (Map.Entry<Class<? extends ClustevalThread>, ClustevalThread> entry : entries) {
                this.threads.remove(entry.getKey());
                entry.getValue().interrupt();
                try {
                    entry.getValue().join(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        super.interrupt();
    }

    /**
     * @return The thread which finds dataset configurations.
     */
    public DataSetConfigFinderThread getDataSetConfigThread() {
        return (DataSetConfigFinderThread) this.threads
                .get(DataSetConfigFinderThread.class);
    }

    /**
     * @return The thread which finds runs.
     */
    public RunFinderThread getRunFinderThread() {
        return (RunFinderThread) this.threads.get(RunFinderThread.class);
    }

    /**
     * @return The run scheduler thread.
     */
    @Override
    public IScheduler getRunScheduler() {
        return (RunSchedulerThread) this.threads.get(RunSchedulerThread.class);
    }

    /**
     * @param clazz
     *              The class for which we want the thread instance
     * @return The thread instance of the passed class.
     */
    public ClustevalThread getThread(Class<? extends ClustevalThread> clazz) {
        return this.threads.get(clazz);
    }
}
