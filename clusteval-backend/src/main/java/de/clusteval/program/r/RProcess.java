/**
 * 
 */
package de.clusteval.program.r;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author Christian Wiwie
 * 
 */
public class RProcess extends Process {

	protected RProgramThread rProgramThread;

	public RProcess(RProgramThread rProgramThread) {
		super();
		this.rProgramThread = rProgramThread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#getErrorStream()
	 */
	@Override
	public InputStream getErrorStream() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#waitFor()
	 */
	@Override
	public int waitFor() throws InterruptedException {
		this.rProgramThread.join();
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#exitValue()
	 */
	@Override
	public int exitValue() {
		// TODO: is this needed?
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#destroy()
	 */
	@Override
	public void destroy() {
		this.rProgramThread.interrupt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#destroyForcibly()
	 */
	@Override
	public Process destroyForcibly() {
		this.destroy();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Process#waitFor(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean waitFor(long timeout, TimeUnit unit)
			throws InterruptedException {
		long ms = TimeUnit.MILLISECONDS.convert(timeout, unit);
		if (ms > 0)
			this.rProgramThread.join(ms);
		return !this.rProgramThread.isAlive();
	}

}
