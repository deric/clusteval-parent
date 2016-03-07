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
package de.clusteval.data.dataset.type;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;

/**
 * Dataset types are used to classify datasets into different thematic groups.
 * <p>
 * 
 * {@code
 * 
 * Data set types can be added to ClustEval by 
 * 
 * 1. extending this class with your own class MyDataSetType. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your class. 
 * 
 *   * :java:ref:`DataSetType(Repository, boolean,long, File)`: The constructor of your class. This constructor has to be implemented and public, otherwise the framework will not be able to load your class. 
 *   * :java:ref:`DataSetType(MyDataSetType)`: The copy constructor of your class taking another instance of your class. This constructor has to be im- plemented and public. 
 *   * :java:ref:`getAlias()`: This alias is used whenever this program is visually represented and a readable name is needed. This is used to represent your program on the website for example.
 * 
 * 2. Creating a jar file named MyDataSetType.jar containing the MyDataSetType.class compiled on your machine in the correct folder structure corresponding to the packages:
 * 
 *   * de/clusteval/data/dataset/type/MyDataSetType.class
 * 
 * 3. Putting the MyDataSetType.jar into the dataset types folder of the repository:
 * 
 *   * <REPOSITORY ROOT>/supp/types/dataset
 *   * The backend server will recognize and try to load the new dataset type automatically the next time, the :java:ref:`DataSetTypeFinderThread` checks the filesystem.
 * 
 * }
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class DataSetType extends RepositoryObject {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 * 
	 */
	public DataSetType(final Repository repository, final boolean register,
			final long changeDate, final File absPath) throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * The copy constructor for dataset types.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public DataSetType(final DataSetType other) throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.repository.RepositoryObject#clone()
	 */
	@Override
	public final DataSetType clone() {
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
	 * Parses the from string.
	 * 
	 * @param repository
	 *            the repository
	 * @param datasetType
	 *            the dataset type
	 * @return the data set format
	 * @throws UnknownDataSetTypeException
	 *             the unknown data set type exception
	 */
	public static DataSetType parseFromString(final Repository repository,
			String datasetType) throws UnknownDataSetTypeException {
		Class<? extends DataSetType> c = repository.getRegisteredClass(
				DataSetType.class, "de.clusteval.data.dataset.type."
						+ datasetType);
		try {
			Constructor<? extends DataSetType> constr = c.getConstructor(
					Repository.class, boolean.class, long.class, File.class);
			// changed 21.03.2013: do not register dataset types here
			return constr.newInstance(repository, false,
					System.currentTimeMillis(), new File(datasetType));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new UnknownDataSetTypeException("\"" + datasetType
				+ "\" is not a known dataset type.");
	}

	/**
	 * Parses the from string.
	 * 
	 * @param repo
	 *            the repo
	 * @param datasetTypes
	 *            the dataset Types
	 * @return the list
	 * @throws UnknownDataSetTypeException
	 *             the unknown data set type exception
	 */
	public static List<DataSetType> parseFromString(final Repository repo,
			String[] datasetTypes) throws UnknownDataSetTypeException {
		List<DataSetType> result = new LinkedList<DataSetType>();
		for (String dsType : datasetTypes) {
			result.add(parseFromString(repo, dsType));
		}
		return result;
	}

	/**
	 * This alias is used whenever this dataset type is visually represented and
	 * a readable name is needed.
	 * 
	 * @return The alias of this dataset type.
	 */
	public abstract String getAlias();
}
