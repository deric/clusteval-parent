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

import de.clusteval.data.statistics.RunStatisticCalculateException;
import de.clusteval.data.statistics.StatisticCalculateException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.StatisticCalculator;

/**
 * @author Christian Wiwie
 * @param <T>
 * 
 */
public abstract class RunStatisticCalculator<T extends RunStatistic>
		extends
			StatisticCalculator<T> {

	protected String uniqueRunIdentifiers;

	/**
	 * @param repository
	 * @param changeDate
	 * @param absPath
	 * @param uniqueRunIdentifiers
	 * @throws RegisterException
	 */
	public RunStatisticCalculator(Repository repository, long changeDate,
			File absPath, final String uniqueRunIdentifiers)
			throws RegisterException {
		super(repository, changeDate, absPath);
		this.uniqueRunIdentifiers = uniqueRunIdentifiers;
	}

	/**
	 * The copy constructor of run statistic calculators.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public RunStatisticCalculator(final RunStatisticCalculator<T> other)
			throws RegisterException {
		super(other);

		this.uniqueRunIdentifiers = other.uniqueRunIdentifiers + "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.StatisticCalculator#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RunStatisticCalculator<T> clone() {
		try {
			return this.getClass().getConstructor(RunStatisticCalculator.class)
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
			throws RunStatisticCalculateException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.StatisticCalculator#getStatistic()
	 */
	@Override
	public abstract T getStatistic();
}
