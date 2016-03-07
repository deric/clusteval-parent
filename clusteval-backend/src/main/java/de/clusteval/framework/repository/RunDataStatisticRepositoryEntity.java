/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.Map;

import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.data.statistics.DataStatisticCalculator;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunDataStatisticCalculator;

/**
 * @author Christian Wiwie
 * 
 */
public class RunDataStatisticRepositoryEntity
		extends
			DynamicRepositoryEntity<RunDataStatistic> {

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
	 *            The name of the class of the run-data statistic.
	 * @return The class of the run-data statistic calculator for the given
	 *         name, or null if it does not exist.
	 */
	public Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> getRunDataStatisticCalculator(
			final String runDataStatisticClassName) {
		Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> result = this.runDataStatisticCalculatorClasses
				.get(runDataStatisticClassName);
		if (result == null && parent != null)
			result = ((RunDataStatisticRepositoryEntity) this.parent)
					.getRunDataStatisticCalculator(runDataStatisticClassName);
		return result;
	}

	/**
	 * This method registers a new run-data statistic calculator class.
	 * 
	 * @param object
	 *            The new class to register.
	 * @return True, if the new class replaced an old one.
	 */
	public boolean registerRunDataStatisticCalculator(
			final Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> object) {
		return this.runDataStatisticCalculatorClasses.put(object.getName()
				.replace("Calculator", ""), object) != null;
	}
}
