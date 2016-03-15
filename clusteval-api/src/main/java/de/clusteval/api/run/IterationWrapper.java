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
package de.clusteval.api.run;

import de.clusteval.api.data.IDataConfig;
import java.io.File;

public class IterationWrapper {

    /**
     * A temporary variable holding a file object pointing to the absolute path
     * of the current log output file during execution of the runnable
     */
    protected File logfile;

    protected RunRunnable runnable;

    protected IDataConfig dataConfig;

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

    protected IDataConfig getDataConfig() {
        return dataConfig;
    }

    protected void setDataConfig(IDataConfig dataConfig) {
        this.dataConfig = dataConfig;
    }

    public RunRunnable getRunnable() {
        return runnable;
    }

    public void setRunnable(RunRunnable runnable) {
        this.runnable = runnable;
    }
}
