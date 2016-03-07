/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.run.statistics;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.run.RunDataAnalysisRun;
import de.clusteval.utils.Statistic;

/**
 * A run-data statistic is a {@link Statistic}, which summarizes relationships
 * of clustering run results and data set properties. Run-data statistics are
 * assessed by a {@link RunDataAnalysisRun}.
 * <p/>
 * 
 * {@code
 * 
 * A run-data statistic MyRunDataStatistic can be added to ClustEval by
 * 
 * 1. extending the class :java:ref:`RunDataStatistic` with your own class MyRunDataStatistic. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your class.
 * 
 *   * :java:ref:`RunDataStatistic(Repository, boolean, long, File)` : The constructor for your run-data statistic. This constructor has to be implemented and public.
 *   * :java:ref:`RunDataStatistic(MyRunDataStatistic)` : The copy constructor for your run-data statistic. This constructor has to be implemented and public.
 *   * :java:ref:`Statistic.getAlias()` : See :java:ref:`Statistic.getAlias()`.
 *   * :java:ref:`Statistic.parseFromString(String)` : See :java:ref:`Statistic.parseFromString(String)`.
 *   
 * 2. extending the class :java:ref:`RunDataStatisticCalculator` with your own class MyRunDataStatisticCalculator . You have to provide your own implementations for the following methods.
 * 
 *   * :java:ref:`RunDataStatisticCalculator(Repository, long, File, DataConfig)` : The constructor for your run-data statistic calculator. This constructor has to be implemented and public.
 *   * :java:ref:`RunDataStatisticCalculator(MyRunDataStatisticCalculator)` : The copy constructor for your run-data statistic calculator. This constructor has to be implemented and public.
 *   * :java:ref:`RunDataStatisticCalculator.calculateResult()`: See :java:ref:`StatisticCalculator.calculateResult()`.
 *   * :java:ref:`StatisticCalculator.writeOutputTo(File)`: See :java:ref:`StatisticCalculator.writeOutputTo(File)`.
 *   
 * 3. Creating a jar file named MyRunDataStatisticCalculator.jar containing the MyRunDataStatistic.class and MyRunDataStatisticCalculator.class compiled on your machine in the correct folder structure corresponding to the packages:
 * 
 *   * de/clusteval/run/statistics/MyRunDataStatistic.class
 *   * de/clusteval/run/statistics/MyRunDataStatisticCalculator.class
 *   
 * 4. Putting the MyRunDataStatistic.jar into the run-data statistics folder of the repository:
 * 
 *   * <REPOSITORY ROOT>/supp/statistics/rundata
 *   * The backend server will recognize and try to load the new run statistics automatically the next time, the RunDataStatisticFinderThread checks the filesystem.
 * 
 * }
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class RunDataStatistic extends Statistic {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public RunDataStatistic(Repository repository, boolean register,
			long changeDate, File absPath) throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * The copy constructor of run data statistics.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public RunDataStatistic(final RunDataStatistic other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public final RunDataStatistic clone() {
		try {
			return this.getClass().getConstructor(this.getClass())
					.newInstance(this);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		this.log.warn("Cloning instance of class "
				+ this.getClass().getSimpleName() + " failed");
		return null;
	}

	/**
	 * This method parses a string and maps it to a subclass of
	 * {@link RunDataStatistic} looking it up in the given repository.
	 * 
	 * @param repository
	 *            The repository to look for the classes.
	 * @param runDataStatistic
	 *            The string representation of a run-data statistic subclass.
	 * @return A subclass of {@link RunDataStatistic}.
	 * @throws UnknownRunDataStatisticException
	 */
	public static RunDataStatistic parseFromString(final Repository repository,
			String runDataStatistic) throws UnknownRunDataStatisticException {
		Class<? extends RunDataStatistic> c = repository.getRegisteredClass(
				RunDataStatistic.class, "de.clusteval.run.statistics."
						+ runDataStatistic);

		try {
			RunDataStatistic statistic = c.getConstructor(Repository.class,
					boolean.class, long.class, File.class).newInstance(
					repository, false, System.currentTimeMillis(),
					new File(runDataStatistic));
			return statistic;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {

		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		throw new UnknownRunDataStatisticException("\"" + runDataStatistic
				+ "\" is not a known RunDataStatistic.");
	}

	/**
	 * This method parses several strings and maps them to subclasses of
	 * {@link RunDataStatistic} looking them up in the given repository.
	 * 
	 * @param repo
	 *            The repository to look for the classes.
	 * @param runStatistics
	 *            The string representation of a run-data statistic subclass.
	 * @return A subclass of {@link RunDataStatistic}.
	 * @throws UnknownRunDataStatisticException
	 */
	public static List<RunDataStatistic> parseFromString(final Repository repo,
			String[] runStatistics) throws UnknownRunDataStatisticException {
		List<RunDataStatistic> result = new LinkedList<RunDataStatistic>();
		for (String runStatistic : runStatistics) {
			result.add(parseFromString(repo, runStatistic));
		}
		return result;
	}
}
