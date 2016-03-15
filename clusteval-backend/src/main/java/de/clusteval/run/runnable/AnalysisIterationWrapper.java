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

import de.clusteval.api.run.IterationWrapper;
import de.clusteval.utils.Statistic;

/**
 * @author Christian Wiwie
 * @param <S>
 *
 */
public class AnalysisIterationWrapper<S extends Statistic> extends IterationWrapper {

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
