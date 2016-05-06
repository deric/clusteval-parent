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
package de.clusteval.framework.repository;

import de.clusteval.api.repository.DynamicRepositoryEntity;
import de.clusteval.api.stats.RunDataStatistic;
import de.clusteval.run.statistics.RunDataStatisticCalculator;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public class RunDataStatisticRepositoryEntity extends DynamicRepositoryEntity<RunDataStatistic> {

    /**
     * A map containing all classes of run data statistic calculators registered
     * in this repository.
     */
    protected Map<String, Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>>> runDataStatisticCalculatorClasses;

    /**
     * @param repository
     * @param parent
     * @param basePath
     */
    public RunDataStatisticRepositoryEntity(Repository repository,
            RunDataStatisticRepositoryEntity parent, String basePath) {
        super(repository, parent, basePath);
    }

    /**
     * This method looks up and returns (if it exists) the class of the run-data
     * statistic calculator corresponding to the run-data-statistic with the
     * given name.
     *
     * @param runDataStatisticClassName
     *                                  The name of the class of the run-data statistic.
     * @return The class of the run-data statistic calculator for the given
     *         name, or null if it does not exist.
     */
    public Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> getRunDataStatisticCalculator(
            final String runDataStatisticClassName) {
        Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> result = this.runDataStatisticCalculatorClasses
                .get(runDataStatisticClassName);
        if (result == null && parent != null) {
            result = ((RunDataStatisticRepositoryEntity) this.parent)
                    .getRunDataStatisticCalculator(runDataStatisticClassName);
        }
        return result;
    }

    /**
     * This method registers a new run-data statistic calculator class.
     *
     * @param object
     *               The new class to register.
     * @return True, if the new class replaced an old one.
     */
    public boolean registerRunDataStatisticCalculator(
            final Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> object) {
        return this.runDataStatisticCalculatorClasses.put(object.getName()
                .replace("Calculator", ""), object) != null;
    }
}
