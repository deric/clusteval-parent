/**
 * 
 */
package de.clusteval.run.runnable;

import java.io.File;

import de.clusteval.data.DataConfig;

public class IterationWrapper {

	/**
	 * A temporary variable holding a file object pointing to the absolute path
	 * of the current log output file during execution of the runnable
	 */
	protected File logfile;

	protected RunRunnable runnable;

	protected DataConfig dataConfig;

	protected boolean isResume;

	
	public boolean isResume() {
		return isResume;
	}

	
	public void setResume(boolean isResume) {
		this.isResume = isResume;
	}

	public IterationWrapper() {
		super();
	}

	protected File getLogfile() {
		return logfile;
	}

	protected void setLogfile(File logfile) {
		this.logfile = logfile;
	}

	protected DataConfig getDataConfig() {
		return dataConfig;
	}

	protected void setDataConfig(DataConfig dataConfig) {
		this.dataConfig = dataConfig;
	}

	protected RunRunnable getRunnable() {
		return runnable;
	}

	protected void setRunnable(RunRunnable runnable) {
		this.runnable = runnable;
	}
}