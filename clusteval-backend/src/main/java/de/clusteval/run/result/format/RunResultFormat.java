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
package de.clusteval.run.result.format;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;

/**
 * Run results (e.g. clusterings) can have different formats. For all kinds of
 * operations the framework needs to know which format a runresult has and how
 * it can be converted to an understandable (standard) format.
 * 
 * <p>
 * Every runresult format comes together with a parser class (see
 * {@link RunResultFormatParser}).
 * 
 * <p>
 * {@code
 * 
 * A runresult format MyRunResultFormat can be added to ClustEval by
 * 
 * 1. extending this class with your own class MyRunResultFormat. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your runresult format.
 * 
 *   * :java:ref:`RunResultFormat(Repository, boolean, long, File)`: The constructor of your runresult format class. This constructor has to be implemented and public, otherwise the framework will not be able to load your runresult format.
 *   * :java:ref:`RunResultFormat(RunResultFormat)`: The copy constructor of your class taking another instance of your class. This constructor has to be implemented and public.
 *   
 * 2. extending the class :java:ref:`RunResultFormatParser` with your own class MyRunResultFormatParser. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your class.
 * 
 *   * :java:ref:`convertToStandardFormat()`: This method converts the given runresult to the standard runresult format of the framework. The converted runresult has to be named exactly as the input file postfixed with the extension ".conv". The original runresult <REPOSITORY ROOT>/results/<runIdentifier>/clusters/TransClust sfld.1.result has to be converted to <REPOSITORY ROOT>/results/<runIdentifier>/clusters/TransClust sfld.1.result.conv by this method. A wrapper object for the converted runresult has be stored in the result attribute.
 *   
 * 3. Creating a jar file named MyRunResultFormat.jar containing the MyRunResultFormat.class and MyRunResultFormatParser.class compiled on your machine in the correct folder structure corresponding to the packages:
 * 
 *   * de/clusteval/run/result/format/MyRunResultFormat.class
 *   * de/clusteval/run/result/format/MyRunResultFormatParser.class
 *   
 * 4. Putting the MyRunResultFormat.jar into the runresult formats folder of the repository:
 * 
 *   * <REPOSITORY ROOT>/supp/formats/runresult
 *   * The backend server will recognize and try to load the new runresult format automatically the next time, the :java:ref:`RunResultFormatFinderThread` checks the filesystem.
 * 
 * }
 * 
 * @author Christian Wiwie
 */
public abstract class RunResultFormat extends RepositoryObject {

	/**
	 * This method parses a runresult format from the given string, containing a
	 * runresult format class name.
	 * 
	 * @param repository
	 *            The repository where to look up the runresult format class.
	 * @param runResultFormat
	 *            The runresult format class name as string.
	 * @return The parsed runresult format.
	 * @throws UnknownRunResultFormatException
	 */
	public static RunResultFormat parseFromString(final Repository repository,
			String runResultFormat) throws UnknownRunResultFormatException {

		Class<? extends RunResultFormat> c = repository.getRegisteredClass(
				RunResultFormat.class, "de.clusteval.run.result.format."
						+ runResultFormat);
		try {
			return c.getConstructor(Repository.class, boolean.class,
					long.class, File.class).newInstance(repository, false,
					System.currentTimeMillis(), new File(runResultFormat));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		throw new UnknownRunResultFormatException("The format \""
				+ runResultFormat + "\" is unknown.");
	}

	/**
	 * Instantiates a new runresult format.
	 * 
	 * @param repo
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public RunResultFormat(final Repository repo, final boolean register,
			final long changeDate, final File absPath) throws RegisterException {
		super(repo, register, changeDate, absPath);
	}

	/**
	 * The copy constructor of runresult formats.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public RunResultFormat(final RunResultFormat other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RunResultFormat))
			return false;

		RunResultFormat other = (RunResultFormat) obj;

		return this.getClass().equals(other.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public final RunResultFormat clone() {
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
}
