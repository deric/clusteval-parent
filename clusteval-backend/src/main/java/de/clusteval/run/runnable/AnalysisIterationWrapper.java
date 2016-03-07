/**
 * 
 */
package de.clusteval.run.runnable;

import de.clusteval.utils.Statistic;

/**
 * @author Christian Wiwie
 *
 */
public class AnalysisIterationWrapper<S extends Statistic>
		extends
			IterationWrapper {

	protected S statistic;

	protected String analysesFolder;

	public String getAnalysesFolder() {
		return analysesFolder;
	}

	public void setAnalysesFolder(final String analysesFolder) {
		this.analysesFolder = analysesFolder;
	}

	public S getStatistic() {
		return statistic;
	}

	public void setStatistic(S statistic) {
		this.statistic = statistic;
	}
}
