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
package de.clusteval.run.runnable;

import de.clusteval.api.exceptions.IncompatibleDataSetFormatException;
import de.clusteval.api.exceptions.IncompleteGoldStandardException;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.RunIterationException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.run.IProgress;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunRunnable;
import de.clusteval.api.run.IScheduler;
import de.clusteval.api.run.IterationRunnable;
import de.clusteval.api.run.IterationWrapper;
import de.clusteval.api.run.RUN_STATUS;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.Run;
import de.clusteval.utils.ProgressPrinter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that corresponds to a smaller atomic part of a {@link Run}.
 * A runnable is executed asynchronously and no order among runnables is
 * guaranteed.
 *
 * <p>
 * Objects of subclasses of this class are created in
 * {@link Run#perform(RunSchedulerThread)} (in subclasses) and later executed by
 * the RunScheduler. One instance represents the execution of one subunit of the
 * overall run. The results are added and stored in {@link Run#results} in a
 * synchronized way.
 *
 * <p>
 * In the class hierarchy this class corresponds to the {@link Run} class.
 *
 * @author Christian Wiwie
 * @param <IR>
 * @param <IW>
 */
public abstract class RunRunnable<IR extends IterationRunnable, IW extends IterationWrapper> implements Runnable, IRunRunnable<IR, IW> {

    /**
     * The run this runnable object was created by.
     */
    protected IRun run;

    /**
     * If exceptions are thrown during the execution it is stored in the
     * following attributes. It will not been thrown automatically, to avoid
     * disrupting the successive optimization iterations. If one wants to check
     * for these exceptions afterwards, one can use the corresponding getter
     * methods.
     */
    protected List<Throwable> exceptions;

    /**
     * Keep track of the progress of this runnable. In case of parameter
     * optimization mode, it will increase by one after every percent reached of
     * the parameter sets to evaluate.
     */
    protected IProgress progress;

    /**
     * This attribute indicates, whether this run is a resumption of a previous
     * execution or a completely new execution.
     */
    protected boolean isResume;

    /**
     * A logger that keeps track of all actions done by the runnable.
     */
    protected Logger log;

    /**
     * This object can be used to get the status of the runnable thread.
     */
    protected Future<?> future;

    /**
     * True, if this runnable is paused, false otherwise.
     */
    protected boolean paused;

    /**
     * This attribute holds the running time after finishing the runnable.
     */
    protected long runningTime;

    /**
     * This attribute is used to store the last start time in case this runnable
     * is paused and resumed.
     */
    protected long lastStartTime;

    /**
     * The unique identification string of the run which is used to store the
     * results in a unique folder to avoid overwriting.
     */
    protected String runThreadIdentString;

    protected List<IR> iterationRunnables;

    /**
     * This list holds wrapper objects for each iteration runnable started.
     */
    protected List<Future<?>> futures;

    /**
     * This boolean helper indicates, whether this run runnable has been
     * terminated. We use this boolean instead of the future of this run
     * runnable to signal termination, because the run thread belonging to this
     * runnable will immediately terminate if this run runnable future is
     * cancelled. However, in some cases the termination of this run runnable's
     * iteration runnables takes some time. Then we want to set the future of
     * this runnabel to cancalled only after all iteration runnables are
     * terminated.
     */
    protected boolean terminated;

    /**
     * A map from futures to iteration runnables to be able to handle
     * termination of threads and processes started in the respective iteration
     * run runnable in {@link #afterRun()}
     */
    protected Map<Future<?>, Runnable> futureToIterationRunnable;

    /**
     * Instantiates a new run runnable.
     *
     * @param run
     *                       The run this runnable belongs to.
     * @param runIdentString
     *                       The unique identification string of the run which is used to
     *                       store the results in a unique folder to avoid overwriting.
     * @param isResume
     *                       True, if this run is a resumption of a previous execution or a
     *                       completely new execution.
     */
    public RunRunnable(final IRun run, final String runIdentString, final boolean isResume) {
        super();
        this.run = run;
        this.isResume = isResume;
        this.exceptions = new ArrayList<>();
        this.runThreadIdentString = runIdentString;
        this.iterationRunnables = new ArrayList<>();
        this.futures = new ArrayList<>();
        this.futureToIterationRunnable = new HashMap<>();
        this.log = LoggerFactory.getLogger(this.getClass());
        this.progress = new ProgressPrinter(10000, false);
    }

    /**
     * Checks whether this runnable thread has been interrupted. If yes, it
     * prints out a simple log statement and.
     *
     * @return True, if this thread was interrupted, false otherwise.
     */
    protected final boolean checkForInterrupted() {
        if (isInterrupted()) {
            this.log.info("Caught the signal to terminate. Terminating as soon as possible...");
            return true;
        }
        return false;
    }

    /**
     * Checks whether this runnable thread has been interrupted.
     *
     * @return True, if this thread was interrupted, false otherwise.
     */
    protected final boolean isInterrupted() {
        // return this.future.isCancelled();
        return this.terminated;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public final void run() {
        this.run.setStatus(RUN_STATUS.RUNNING);
        try {
            beforeRun();
            doRun();
        } catch (InterruptedException e) {
            // 02.06.2014: do nothing
        } catch (Throwable e) {
            this.exceptions.add(e);
        } finally {
            try {
                afterRun();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        this.log.info("Terminating runnable ...");
        this.terminated = true;
        // TODO: cancel all the iteration threads
        for (Future<?> f : this.futures) {
            f.cancel(true);
        }
    }

    /**
     * This method is invoked by {@link #run()} before anything else is done. It
     * can be overwritten and used in subclasses to do any precalculations or
     * requirements like copying or moving files.
     *
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws InvalidDataSetFormatVersionException
     * @throws UnknownDataSetFormatException
     * @throws RegisterException
     * @throws InternalAttributeException
     * @throws IncompatibleDataSetFormatException
     * @throws UnknownGoldStandardFormatException
     * @throws IncompleteGoldStandardException
     * @throws InterruptedException
     */
    @SuppressWarnings("unused")
    protected void beforeRun() throws UnknownDataSetFormatException,
                                      InvalidDataSetFormatVersionException, IllegalArgumentException,
                                      IOException, RegisterException, InternalAttributeException,
                                      IncompatibleDataSetFormatException,
                                      UnknownGoldStandardFormatException,
                                      IncompleteGoldStandardException, InterruptedException, UnknownProviderException {
        this.futures.clear();
    }

    /**
     * This method is invoked by {@link #run()} after {@link #beforeRun()} has
     * finished and is responsible for the operation and execution of the
     * runnable itself.
     *
     * @throws de.clusteval.api.exceptions.RunIterationException
     */
    protected void doRun() throws RunIterationException {
        while (this.hasNextIteration()) {
            if (checkForInterrupted()) {
                return;
            }
            final IW iterationWrapper = this.createIterationWrapper();
            this.decorateIterationWrapper(iterationWrapper,
                    consumeNextIteration());
            this.doRunIteration(iterationWrapper);
        }
    }

    /**
     * This method is invoked by {@link #run()} after {@link #doRun()} has
     * finished. It is responsible for cleaning up all files, folders and for
     * doing all kinds of postcalculations.
     *
     * @throws InterruptedException
     */
    protected void afterRun() throws InterruptedException {
        // wait for all iteration runnables to finish
        for (Future<?> f : this.futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
                // here we handle termination of all threads or processes , that
                // have been started by the iteration run runnables.
                Runnable run = this.futureToIterationRunnable.remove(f);
                if (run instanceof ExecutionIterationRunnable) {
                    Process p = ((ExecutionIterationRunnable) run).getProcess();
                    if (p != null) {
                        p.destroyForcibly();
                    }
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // print exceptions
        if (this.exceptions.size() > 0) {
            this.log.warn("During the execution of this run runnable exceptions were thrown:");
            for (Throwable t : this.exceptions) {
                this.log.warn(t.toString());

                StringWriter writer = new StringWriter();
                t.printStackTrace(new PrintWriter(writer));
                String message = writer.toString();
                String[] split = message.split(System
                        .getProperty("line.separator"));
                for (int i = 1; i < split.length; i++) {
                    this.log.warn("|--> " + split[i]);
                }
            }
        }
        this.progress.update(10000);
    }

    /**
     * This method causes the caller to wait for this runnable's thread to
     * finish its execution.
     *
     * <p>
     * This method also waits in case this runnable has not yet been started
     * (the future object has not yet been initialized).
     *
     * @throws InterruptedException
     * @throws CancellationException
     * @throws ExecutionException
     */
    public final void waitFor() throws InterruptedException, ExecutionException {
        while (!this.futures.isEmpty()) {
            boolean nullPointerException = true;
            while (nullPointerException) {
                try {
                    Future<?> f = this.futures.remove(0);
                    f.get();
                } catch (NullPointerException e) {
                    continue;
                } catch (CancellationException e) {
                }
                nullPointerException = false;
            }
        }

        boolean nullPointerException = true;
        while (nullPointerException) {
            try {
                this.future.get();
            } catch (NullPointerException e) {
                continue;
            } catch (CancellationException e) {
            }
            nullPointerException = false;
        }
    }

    /**
     * @return True, if this runnable's thread has been paused.
     */
    public boolean isPaused() {
        return this.paused;
    }

    /**
     * @return True, if this runnable's thread has been cancelled.
     */
    public boolean isCancelled() {
        if (this.future != null) {
            return this.future.isCancelled();
        }
        return false;
    }

    /**
     * @return True, if this runnable's thread has finished its execution.
     */
    public boolean isDone() {
        if (this.future != null) {
            return this.future.isDone();
        }
        return false;
    }

    /**
     * This method pauses this runnable's thread until {@link #resume()} is
     * invoked. If it was paused before this invocation will be ignored.
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * This method resumes this runnable's thread, after it has been paused. If
     * it wasn't paused before this invocation will be ignored.
     */
    public void resume() {
        this.paused = false;
    }

    /**
     * The future object of a runnable is only initialized, when it has been
     * started.
     *
     * @return The future object of this runnable.
     * @see #future
     */
    public Future<?> getFuture() {
        return this.future;
    }

    /**
     * @return A list with all exceptions thrown during execution of this
     *         runnable.
     * @see #exceptions
     */
    @Override
    public List<Throwable> getExceptions() {
        return this.exceptions;
    }

    /**
     * @return The progress printer of this runnable.
     * @see #progress
     */
    public IProgress getProgress() {
        return this.progress;
    }

    /**
     * @return The run this runnable belongs to.
     */
    @Override
    public IRun getRun() {
        return this.run;
    }

    protected abstract IR createIterationRunnable(final IW iterationWrapper);

    protected abstract IW createIterationWrapper();

    protected void decorateIterationWrapper(final IW iterationWrapper,
            final int currentPos) throws RunIterationException {
        iterationWrapper.setResume(isResume);
    }

    protected void submitIterationRunnable(final IR iterationRunnable) {
        // we do not accept new runnables if this run has been terminated
        // before.
        if (this.terminated) {
            return;
        }
        this.iterationRunnables.add(iterationRunnable);

        final IScheduler runScheduler;
        if (this.getRun().getRepository() instanceof RunResultRepository) {
            runScheduler = this.getRun().getRepository().getParent()
                    .getSupervisorThread().getRunScheduler();
        } else {
            runScheduler = this.getRun().getRepository().getSupervisorThread()
                    .getRunScheduler();
        }
        Future<?> f = runScheduler.registerIterationRunnable(iterationRunnable);
        this.futures.add(f);
        this.futureToIterationRunnable.put(f, iterationRunnable);
    }
}

/**
 * This class is responsible for reading and emptying the streams of the started
 * thread. This has to be done in order to avoid overflowing streams and thus
 * possible termination of the thread by the operating system.
 *
 */
class StreamGobbler extends Thread {

    InputStream is;
    BufferedWriter bw;

    public StreamGobbler(InputStream is, BufferedWriter bw) {
        super();
        this.setName(this.getName().replace("Thread",
                this.getClass().getSimpleName()));
        // TODO this.setPriority(NORM_PRIORITY-1);
        this.is = is;
        this.bw = bw;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                synchronized (bw) {
                    bw.append(line);
                    bw.newLine();
                    bw.flush();
                }
            }
        } catch (IOException ioe) {
            // stream closed
            // ioe.printStackTrace();
        }
    }
}
