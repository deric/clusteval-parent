/**
 *
 */
package de.clusteval.framework.repository;

import de.clusteval.api.repository.DynamicRepositoryEntity;
import de.clusteval.api.stats.DataStatistic;
import de.clusteval.api.stats.DataStatisticCalculator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public class DataStatisticRepositoryEntity extends DynamicRepositoryEntity<DataStatistic> {

    /**
     * A map containing all classes of data statistic calculators registered in
     * this repository.
     */
    protected Map<String, Class<? extends DataStatisticCalculator<? extends DataStatistic>>> dataStatisticCalculatorClasses;

    /**
     * @param repository
     * @param parent
     * @param basePath
     */
    public DataStatisticRepositoryEntity(Repository repository,
            DataStatisticRepositoryEntity parent, String basePath) {
        super(repository, parent, basePath);

        this.dataStatisticCalculatorClasses = new HashMap<>();
    }

    /**
     * This method looks up and returns (if it exists) the class of the data
     * statistic calculator for the datastatistic class with the given name.
     *
     * @param dataStatisticClassName
     *                               The name of the datastatistic class.
     *
     * @return The class of the data statistic calculator with the given name or
     *         null, if it does not exist.
     */
    public Class<? extends DataStatisticCalculator<? extends DataStatistic>> getDataStatisticCalculator(
            final String dataStatisticClassName) {
        Class<? extends DataStatisticCalculator<? extends DataStatistic>> result = this.dataStatisticCalculatorClasses
                .get(dataStatisticClassName);
        if (result == null && parent != null) {
            result = ((DataStatisticRepositoryEntity) this.parent)
                    .getDataStatisticCalculator(dataStatisticClassName);
        }
        return result;
    }

    public boolean registerDataStatisticCalculator(
            Class<? extends DataStatisticCalculator<? extends DataStatistic>> dataStatisticCalculator) {
        return this.dataStatisticCalculatorClasses.put(dataStatisticCalculator
                .getName().replace("Calculator", ""), dataStatisticCalculator) != null;
    }
}
