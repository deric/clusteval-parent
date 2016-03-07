/**
 * 
 */
package de.clusteval.run.runnable;

import de.clusteval.data.DataConfig;
import de.clusteval.data.statistics.DataStatistic;

/**
 * @author Christian Wiwie
 *
 */
public class DataAnalysisIterationWrapper
		extends
			AnalysisIterationWrapper<DataStatistic> {

	protected DataConfig dataConfig;

	public DataConfig getDataConfig() {
		return this.dataConfig;
	}

	public void setDataConfig(final DataConfig dataConfig) {
		this.dataConfig = dataConfig;
	}
}
