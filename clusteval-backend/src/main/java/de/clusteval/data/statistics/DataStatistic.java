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
package de.clusteval.data.statistics;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.run.DataAnalysisRun;
import de.clusteval.utils.Statistic;

/**
 * A data statistic is a {@link Statistic}, which summarizes properties of
 * data sets. Data statistics are assessed by a
 * {@link DataAnalysisRun}.
 * <p/>
 * 
 * 
 * {@code
 * 
 * 
 * A data statistic MyDataStatistic can be added to ClustEval by
 * 
 * 1. extending the class :java:ref:`DataStatistic` with your own class MyDataStatistic. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your class.
 * 
 *   * :java:ref:`DataStatistic(Repository, boolean, long, File)` : The constructor for your data statistic. This constructor has to be implemented and public.
 *   * :java:ref:`DataStatistic(MyDataStatistic)` : The copy constructor for your data statistic. This constructor has to be implemented and public.
 *   * :java:ref:`Statistic.getAlias()` : See :java:ref:`Statistic.getAlias()`.
 *   * :java:ref:`Statistic.parseFromString(String)` : See :java:ref:`Statistic.parseFromString(String)`.
 *   
 * 2. extending the class :java:ref:`DataStatisticCalculator` with your own class MyDataStatisticCalculator . You have to provide your own implementations for the following methods.
 * 
 *   * :java:ref:`DataStatisticCalculator(Repository, long, File, DataConfig)` : The constructor for your data statistic calculator. This constructor has to be implemented and public.
 *   * :java:ref:`DataStatisticCalculator(MyDataStatisticCalculator)` : The copy constructor for your data statistic calculator. This constructor has to be implemented and public.
 *   * :java:ref:`DataStatisticCalculator.calculateResult()`: See :java:ref:`StatisticCalculator.calculateResult()`.
 *   * :java:ref:`StatisticCalculator.writeOutputTo(File)`: See :java:ref:`StatisticCalculator.writeOutputTo(File)`.
 *   
 * 3. Creating a jar file named MyDataStatisticCalculator.jar containing the MyDataStatistic.class and MyDataStatisticCalculator.class compiled on your machine in the correct folder structure corresponding to the packages:
 * 
 *   * de/clusteval/run/statistics/MyDataStatistic.class
 *   * de/clusteval/run/statistics/MyDataStatisticCalculator.class
 *   
 * 4. Putting the MyDataStatistic.jar into the data statistics folder of the repository:
 * 
 *   * <REPOSITORY ROOT>/supp/statistics/data
 *   * The backend server will recognize and try to load the new data statistics automatically the next time, the DataStatisticFinderThread checks the filesystem.
 * 
 * }
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class DataStatistic extends Statistic {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public DataStatistic(Repository repository, boolean register,
			long changeDate, File absPath) throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * The copy constructor of data statistics.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public DataStatistic(final DataStatistic other) throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public final DataStatistic clone() {
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
	 * This method parses a string and maps it to an instance of a
	 * {@link DataStatistic} looking its class up in the given repository.
	 * 
	 * @param repository
	 *            The repository to look for the classes.
	 * @param dataStatistic
	 *            The string representation of a data statistic subclass.
	 * @return A subclass of {@link DataStatistic}.
	 * @throws UnknownDataStatisticException
	 */
	public static DataStatistic parseFromString(final Repository repository,
			String dataStatistic) throws UnknownDataStatisticException {
		Class<? extends DataStatistic> c = repository.getRegisteredClass(
				DataStatistic.class, "de.clusteval.data.statistics."
						+ dataStatistic);

		try {
			DataStatistic statistic = c.getConstructor(Repository.class,
					boolean.class, long.class, File.class).newInstance(
					repository, false, System.currentTimeMillis(),
					new File(dataStatistic));

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
		throw new UnknownDataStatisticException("\"" + dataStatistic
				+ "\" is not a known data statistic.");
	}

	/**
	 * This method parses several strings and maps them to instances of
	 * {@link DataStatistic} looking their classes up in the given repository.
	 * 
	 * @param repo
	 *            The repository to look for the classes.
	 * @param dataStatistics
	 *            The string representation of a data statistic subclass.
	 * @return A subclass of {@link DataStatistic}.
	 * @throws UnknownDataStatisticException
	 */
	public static List<DataStatistic> parseFromString(final Repository repo,
			String[] dataStatistics) throws UnknownDataStatisticException {
		List<DataStatistic> result = new LinkedList<DataStatistic>();
		for (String dataStatistic : dataStatistics) {
			result.add(parseFromString(repo, dataStatistic));
		}
		return result;
	}

	/**
	 * @return True, if this data statistic requires a goldstandard to be
	 *         assessed.
	 */
	public abstract boolean requiresGoldStandard();
}
