/**
 * 
 */
package de.clusteval.run.runnable;

import de.clusteval.run.statistics.RunDataStatistic;

/**
 * @author Christian Wiwie
 *
 */
public class RunDataAnalysisIterationWrapper
		extends
			AnalysisIterationWrapper<RunDataStatistic> {

	protected String uniqueRunAnalysisRunIdentifier;

	protected String uniqueDataAnalysisRunIdentifier;

	public String getUniqueDataAnalysisRunIdentifier() {
		return uniqueDataAnalysisRunIdentifier;
	}

	public void setUniqueDataAnalysisRunIdentifier(
			String uniqueDataAnalysisRunIdentifier) {
		this.uniqueDataAnalysisRunIdentifier = uniqueDataAnalysisRunIdentifier;
	}

	public String getRunIdentifier() {
		return this.uniqueRunAnalysisRunIdentifier;
	}

	public void setRunIdentifier(final String runIdentifier) {
		this.uniqueRunAnalysisRunIdentifier = runIdentifier;
	}
}
