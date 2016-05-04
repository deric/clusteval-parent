/**
 * 
 */
package de.clusteval.run.runnable;

import de.clusteval.api.stats.RunStatistic;

/**
 * @author Christian Wiwie
 *
 */
public class RunAnalysisIterationWrapper
		extends
			AnalysisIterationWrapper<RunStatistic> {

	protected String uniqueRunAnalysisRunIdentifier;

	public String getRunIdentifier() {
		return this.uniqueRunAnalysisRunIdentifier;
	}

	public void setRunIdentifier(final String runIdentifier) {
		this.uniqueRunAnalysisRunIdentifier = runIdentifier;
	}
}
