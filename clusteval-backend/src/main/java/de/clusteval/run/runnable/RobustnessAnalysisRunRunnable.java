/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.run.runnable;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IScheduler;
import java.util.Map;

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
     * @param runParams
     */
    public RobustnessAnalysisRunRunnable(IScheduler runScheduler,
            IRun run, IProgramConfig programConfig, IDataConfig dataConfig,
            String runIdentString, boolean isResume,
            Map<IProgramParameter<?>, String> runParams) {
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
