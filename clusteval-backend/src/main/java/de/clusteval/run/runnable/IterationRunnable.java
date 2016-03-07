/**
 * 
 */
package de.clusteval.run.runnable;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.clusteval.framework.RLibraryNotLoadedException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.Run;
import de.clusteval.utils.RNotAvailableException;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class IterationRunnable<IW extends IterationWrapper>
		implements
			Runnable {

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

		Repository repo = getRun().getRepository();
		if (repo instanceof RunResultRepository)
			repo = repo.getParent();
		RunSchedulerThread scheduler = repo.getSupervisorThread()
				.getRunScheduler();
		scheduler
				.informOnStartedIterationRunnable(Thread.currentThread(), this);
	}

	protected abstract void doRun() throws InterruptedException;

	protected void afterRun() {
		Repository repo = getRun().getRepository();
		if (repo instanceof RunResultRepository)
			repo = repo.getParent();
		RunSchedulerThread scheduler = repo.getSupervisorThread()
				.getRunScheduler();
		scheduler.informOnFinishedIterationRunnable(Thread.currentThread(),
				this);
	}

	public long getStartTime() {
		return this.startTime;
	}
}
