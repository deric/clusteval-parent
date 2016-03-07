/**
 * 
 */
package de.clusteval.program.r;

import java.util.Map;

import org.rosuda.REngine.Rserve.RserveException;

import de.clusteval.data.DataConfig;
import de.clusteval.program.ProgramConfig;

/**
 * @author Christian Wiwie
 *
 */

public class RProgramThread extends Thread {

	protected Thread poolThread;
	protected Exception ex;
	protected RProgram rProgram;
	protected DataConfig dataConfig;
	protected ProgramConfig programConfig;
	protected String[] invocationLine;
	protected Map<String, String> effectiveParams;
	protected Map<String, String> internalParams;

	/**
	 * @param rProgram
	 * @param dataConfig
	 * @param programConfig
	 * @param invocationLine
	 * @param effectiveParams
	 * @param internalParams
	 * @throws RserveException
	 */
	public RProgramThread(final Thread t, final RProgram rProgram,
			final DataConfig dataConfig, final ProgramConfig programConfig,
			final String[] invocationLine,
			final Map<String, String> effectiveParams,
			final Map<String, String> internalParams) throws RserveException {
		super();

		this.poolThread = t;
		// we need to clone here, as each thread needs its own object
		this.rProgram = rProgram.clone();
		this.dataConfig = dataConfig;
		this.programConfig = programConfig;
		this.invocationLine = invocationLine;
		this.effectiveParams = effectiveParams;
		this.internalParams = internalParams;
	}

	@Override
	public void run() {
		// we initialize the rEngine here and take the one from the thread pool
		// thread
		try {
			this.rProgram.rEngine = this.rProgram.getRepository().getRengine(
					this.poolThread);
			try {
				this.rProgram.beforeExec(dataConfig, programConfig,
						invocationLine, effectiveParams, internalParams);
				if (this.isInterrupted())
					throw new InterruptedException();
				this.rProgram.doExec(dataConfig, programConfig, invocationLine,
						effectiveParams, internalParams);
				if (this.isInterrupted())
					throw new InterruptedException();
				this.rProgram.afterExec(dataConfig, programConfig,
						invocationLine, effectiveParams, internalParams);
			} catch (Exception e) {
				ex = e;
			}
		} catch (RserveException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {
		super.interrupt();
		try {
			if (this.rProgram.rEngine != null)
				this.rProgram.rEngine.interrupt();
		} finally {
			this.rProgram.getRepository().clearRengine(poolThread);
		}
	}

	public Exception getException() {
		return this.ex;
	}
}