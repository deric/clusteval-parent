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
import de.clusteval.api.stats.IDataStatistic;

/**
 * @author Christian Wiwie
 *
 */
public class DataAnalysisIterationWrapper extends AnalysisIterationWrapper<IDataStatistic> {

    protected IDataConfig dataConfig;

    @Override
    public IDataConfig getDataConfig() {
        return this.dataConfig;
    }

    @Override
    public void setDataConfig(final IDataConfig dataConfig) {
        this.dataConfig = dataConfig;
    }
}
