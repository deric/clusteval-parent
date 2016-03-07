/**
 * 
 */
package de.clusteval.run.runnable;

import java.util.Map;

import de.clusteval.data.DataConfig;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.Run;

/**
 * @author Christian Wiwie
 *
 */
public class RobustnessAnalysisRunRunnable extends ClusteringRunRunnable {

	/**
	 * @param runScheduler
	 * @param run
	 * @param programConfig
	 * @param dataConfig
	 * @param runIdentString
	 * @param isResume
	 */
	public RobustnessAnalysisRunRunnable(RunSchedulerThread runScheduler,
			Run run, ProgramConfig programConfig, DataConfig dataConfig,
			String runIdentString, boolean isResume,
			Map<ProgramParameter<?>, String> runParams) {
		super(runScheduler, run, programConfig, dataConfig, runIdentString,
				isResume, runParams);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// de.clusteval.run.runnable.ExecutionRunRunnable#getRunParametersFromRun()
	// */
	// @Override
	// protected void getRunParametersFromRun() {
	// for (int p = 0; p < this.getRun().getRunPairs().size(); p++) {
	// Pair<ProgramConfig, DataConfig> pair = this.getRun()
	// .getRunPairs().get(p);
	// DataConfig dc = pair.getSecond();
	// ProgramConfig pc = pair.getFirst();
	// if (pc.getName().equals(this.programConfig.getName())
	// && dc.getName().equals(this.dataConfig.getName())) {
	// this.runParams = this.getRun().getParameterValues().get(p);
	// break;
	// }
	// }
	// }
}
