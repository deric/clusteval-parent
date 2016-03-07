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
package de.clusteval.data.preprocessing;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.clusteval.data.dataset.DataSet;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.program.r.RLibraryInferior;

/**
 * 
 * <p>
 * {@code
 * 
 * A data preprocessor MyDataPreprocessor can be added to ClustEval by
 * 
 * 1. extending this class with your own class MyDataPreprocessor. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your runresult format.
 * 
 *   * :java:ref:`DataPreprocessor(Repository, boolean, long, File)`: The constructor of your class. This constructor has to be implemented and public, otherwise the framework will not be able to load your runresult format.
 *   * :java:ref:`DataPreprocessor(DataPreprocessor)`: The copy constructor of your class taking another instance of your class. This constructor has to be implemented and public.
 *   * :java:ref:`getCompatibleDataSetFormats()`: Returns a set of data formats that this preprocessor can be applied to.
 *   * :java:Ref:`preprocess()`: This is the core of your preprocessor; it takes the input data set and returns a wrapper object of the preprocessed one.
 *   
 * 2. Creating a jar file named MyDataPreprocessor.jar containing the MyDataPreprocessor.class compiled on your machine in the correct folder structure corresponding to the packages:
 * 
 *   * de/clusteval/data/preprocessing/MyDataPreprocessor.class
 *   
 * 3. Putting the MyRunResultFormat.jar into the corresponding folder of the repository:
 * 
 *   * <REPOSITORY ROOT>/supp/preprocessing
 *   * The backend server will recognize and try to load the new runresult format automatically the next time, the :java:ref:`DataPreprocessorFinderThread` checks the filesystem.
 * 
 * }
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class DataPreprocessor extends RepositoryObject
		implements
			RLibraryInferior {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public DataPreprocessor(Repository repository, boolean register,
			long changeDate, File absPath) throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * The copy constructor of data preprocessors.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public DataPreprocessor(DataPreprocessor other) throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.repository.RepositoryObject#clone()
	 */
	@Override
	public DataPreprocessor clone() {
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
	 * Parses a list of data preprocessors from a string array.
	 * 
	 * @param repository
	 *            the repository
	 * @param dataPreprocessors
	 *            The array containing simple names of the data preprocessor
	 *            class.
	 * @return A list containing data preprocessors.
	 * @throws UnknownDataPreprocessorException
	 */
	public static List<DataPreprocessor> parseFromString(
			final Repository repository, String[] dataPreprocessors)
			throws UnknownDataPreprocessorException {
		List<DataPreprocessor> result = new ArrayList<DataPreprocessor>();

		for (String s : dataPreprocessors)
			result.add(parseFromString(repository, s));

		return result;
	}

	/**
	 * Parses a data preprocessor from string.
	 * 
	 * @param repository
	 *            the repository
	 * @param dataPreprocessor
	 *            The simple name of the data preprocessor class.
	 * @return the data preprocessor
	 * @throws UnknownDataPreprocessorException
	 */
	public static DataPreprocessor parseFromString(final Repository repository,
			String dataPreprocessor) throws UnknownDataPreprocessorException {

		Class<? extends DataPreprocessor> c = repository.getRegisteredClass(
				DataPreprocessor.class, "de.clusteval.data.preprocessing."
						+ dataPreprocessor);
		try {
			DataPreprocessor preprocessor = c.getConstructor(Repository.class,
					boolean.class, long.class, File.class).newInstance(
					repository, true, System.currentTimeMillis(),
					new File(dataPreprocessor));
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
		throw new UnknownDataPreprocessorException("\"" + dataPreprocessor
				+ "\" is not a known data preprocessor.");
	}

	/**
	 * @return A set with simple names of all classes, this preprocessor is
	 *         compatible to.
	 */
	public abstract Set<String> getCompatibleDataSetFormats();

	/**
	 * This method is reponsible for preprocessing the passed data and creating
	 * a new dataset object corresponding to the newly created preprocessed
	 * dataset.
	 * 
	 * @param dataSet
	 *            The dataset to be preprocessed.
	 * @return The preprocessed dataset.
	 * @throws InterruptedException
	 */
	public abstract DataSet preprocess(final DataSet dataSet)
			throws InterruptedException;
}
