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
import de.clusteval.api.stats.IDataStatistic;
import java.util.List;
import java.util.Set;

/**
 *
 * @author deric
 */
public interface IAnalysisRun extends IRunResult {

    /**
     * @return The data configurations encapsulating the datasets that were
     *         analysed.
     */
    Set<IDataConfig> getDataConfigs();

    /**
     * @param dataConfig
     *                   The data configuration for which we want to know which data
     *                   statistics were evaluated.
     * @return The data statistics that were assessed for the given data
     *         configuration.
     */
    List<IDataStatistic> getDataStatistics(final IDataConfig dataConfig);
}
