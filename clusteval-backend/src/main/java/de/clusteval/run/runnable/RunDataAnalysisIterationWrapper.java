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

import de.clusteval.api.stats.RunDataStatistic;

/**
 * @author Christian Wiwie
 *
 */
public class RunDataAnalysisIterationWrapper extends AnalysisIterationWrapper<RunDataStatistic> {

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
