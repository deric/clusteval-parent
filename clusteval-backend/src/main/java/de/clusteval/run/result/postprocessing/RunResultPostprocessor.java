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
package de.clusteval.run.result.postprocessing;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import de.clusteval.cluster.Clustering;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.program.r.RLibraryInferior;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class RunResultPostprocessor extends RepositoryObject
		implements
			RLibraryInferior {

	protected RunResultPostprocessorParameters parameters;

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public RunResultPostprocessor(Repository repository, boolean register,
			long changeDate, File absPath,
			RunResultPostprocessorParameters parameters)
			throws RegisterException {
		super(repository, register, changeDate, absPath);
		this.parameters = parameters;
	}

	/**
	 * The copy constructor of data preprocessors.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public RunResultPostprocessor(RunResultPostprocessor other)
			throws RegisterException {
		super(other);
		this.parameters = other.parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.repository.RepositoryObject#clone()
	 */
	@Override
	public RunResultPostprocessor clone() {
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
	 * Parses a list of runresult postprocessor from a string array.
	 * 
	 * @param repository
	 *            the repository
	 * @param runResultPostprocessor
	 *            The array containing simple names of the runresult
	 *            postprocessor class.
	 * @return A list containing runresult postprocessor.
	 * @throws UnknownRunResultPostprocessorException
	 */
	public static List<RunResultPostprocessor> parseFromString(
			final Repository repository, String[] runResultPostprocessor,
			RunResultPostprocessorParameters[] parameters)
			throws UnknownRunResultPostprocessorException {
		List<RunResultPostprocessor> result = new ArrayList<RunResultPostprocessor>();

		for (int i = 0; i < runResultPostprocessor.length; i++) {
			result.add(parseFromString(repository, runResultPostprocessor[i],
					parameters[i]));
		}

		return result;
	}

	/**
	 * Parses a data preprocessor from string.
	 * 
	 * @param repository
	 *            the repository
	 * @param runResultPostProcessor
	 *            The simple name of the data preprocessor class.
	 * @return the data preprocessor
	 * @throws UnknownRunResultPostprocessorException
	 */
	public static RunResultPostprocessor parseFromString(
			final Repository repository, String runResultPostProcessor,
			RunResultPostprocessorParameters parameters)
			throws UnknownRunResultPostprocessorException {

		Class<? extends RunResultPostprocessor> c = repository
				.getRegisteredClass(RunResultPostprocessor.class,
						"de.clusteval.run.result.postprocessing."
								+ runResultPostProcessor);
		try {
			RunResultPostprocessor preprocessor = c.getConstructor(
					Repository.class, boolean.class, long.class, File.class,
					RunResultPostprocessorParameters.class).newInstance(
					repository, true, System.currentTimeMillis(),
					new File(runResultPostProcessor), parameters);
			return preprocessor;

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
		throw new UnknownRunResultPostprocessorException("\""
				+ runResultPostProcessor
				+ "\" is not a known runresult postprocessor.");
	}

	/**
	 * This method is reponsible for postprocessing the passed clustering and
	 * creating a new clustering object corresponding to the newly created
	 * postprocessed clustering.
	 * 
	 * @param clustering
	 *            The clustering to be postprocessed.
	 * @return The postprocessed clustering.
	 */
	public abstract Clustering postprocess(final Clustering clustering);
}
