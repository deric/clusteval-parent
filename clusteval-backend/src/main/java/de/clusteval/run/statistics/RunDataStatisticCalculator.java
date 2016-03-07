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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.clusteval.data.statistics.RunDataStatisticCalculateException;
import de.clusteval.data.statistics.StatisticCalculateException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.StatisticCalculator;

/**
 * @author Christian Wiwie
 * @param <T>
 * 
 */
public abstract class RunDataStatisticCalculator<T extends RunDataStatistic>
		extends
			StatisticCalculator<T> {

	/**
	 * This method parses a string and maps it to a subclass of
	 * {@link RunDataStatistic} looking it up in the given repository.
	 * 
	 * @param repository
	 *            The repository to look for the classes.
	 * @param runDataStatistic
	 *            The string representation of a run data statistic subclass.
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
					repository, true, System.currentTimeMillis(),
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
				+ "\" is not a known run data statistic.");
	}

	/**
	 * This method parses several strings and maps them to subclasses of
	 * {@link RunDataStatistic} looking them up in the given repository.
	 * 
	 * @param repo
	 *            The repository to look for the classes.
	 * @param runStatistics
	 *            The string representation of a run data statistic subclass.
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

	protected List<String> uniqueRunIdentifiers;
	protected List<String> uniqueDataIdentifiers;

	/**
	 * @param repository
	 * @param changeDate
	 * @param absPath
	 * @param uniqueRunIdentifiers
	 * @param uniqueDataIdentifiers
	 * @throws RegisterException
	 */
	public RunDataStatisticCalculator(Repository repository, long changeDate,
			File absPath, final List<String> uniqueRunIdentifiers,
			final List<String> uniqueDataIdentifiers) throws RegisterException {
		super(repository, changeDate, absPath);
		this.uniqueRunIdentifiers = uniqueRunIdentifiers;
		this.uniqueDataIdentifiers = uniqueDataIdentifiers;
	}

	/**
	 * The copy constructor of run data statistic calculators.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public RunDataStatisticCalculator(final RunDataStatisticCalculator<T> other)
			throws RegisterException {
		super(other);

		this.uniqueRunIdentifiers = new ArrayList<String>(
				other.uniqueRunIdentifiers);
		this.uniqueDataIdentifiers = new ArrayList<String>(
				other.uniqueDataIdentifiers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.StatisticCalculator#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RunDataStatisticCalculator<T> clone() {
		try {
			return this.getClass()
					.getConstructor(RunDataStatisticCalculator.class)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.StatisticCalculator#calculate()
	 */
	@Override
	public T calculate() throws StatisticCalculateException {
		return super.calculate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.StatisticCalculator#calculateResult()
	 */
	@Override
	protected abstract T calculateResult()
			throws RunDataStatisticCalculateException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.StatisticCalculator#getStatistic()
	 */
	@Override
	public abstract T getStatistic();
}
