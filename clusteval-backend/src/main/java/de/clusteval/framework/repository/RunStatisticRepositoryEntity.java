/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.HashMap;
import java.util.Map;

import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.run.statistics.RunStatisticCalculator;

/**
 * @author Christian Wiwie
 * 
 */
public class RunStatisticRepositoryEntity
		extends
			DynamicRepositoryEntity<RunStatistic> {

	/**
	 * A map containing all classes of run data statistic calculators registered
	 * in this repository.
	 */
	protected Map<String, Class<? extends RunStatisticCalculator<? extends RunStatistic>>> runStatisticCalculatorClasses;

	/**
	 * @param repository
	 * @param parent
	 * @param basePath
	 */
	public RunStatisticRepositoryEntity(Repository repository,
			RunStatisticRepositoryEntity parent, String basePath) {
		super(repository, parent, basePath);

		this.runStatisticCalculatorClasses = new HashMap<String, Class<? extends RunStatisticCalculator<? extends RunStatistic>>>();
	}

	/**
	 * This method looks up and returns (if it exists) the class of the run-data
	 * statistic calculator corresponding to the run-data-statistic with the
	 * given name.
	 * 
	 * @param runStatisticClassName
	 *            The name of the class of the run-data statistic.
	 * @return The class of the run-data statistic calculator for the given
	 *         name, or null if it does not exist.
	 */
	public Class<? extends RunStatisticCalculator<? extends RunStatistic>> getRunStatisticCalculator(
			final String runStatisticClassName) {
		Class<? extends RunStatisticCalculator<? extends RunStatistic>> result = this.runStatisticCalculatorClasses
				.get(runStatisticClassName);
		if (result == null && parent != null)
			result = ((RunStatisticRepositoryEntity) this.parent)
					.getRunStatisticCalculator(runStatisticClassName);
		return result;
	}

	/**
	 * This method registers a new run-data statistic calculator class.
	 * 
	 * @param object
	 *            The new class to register.
	 * @return True, if the new class replaced an old one.
	 */
	public boolean registerRunStatisticCalculator(
			final Class<? extends RunStatisticCalculator<? extends RunStatistic>> object) {
		return this.runStatisticCalculatorClasses.put(
				object.getName().replace("Calculator", ""), object) != null;
	}
}
