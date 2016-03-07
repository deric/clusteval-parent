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
package de.clusteval.data.dataset.generator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.JARFinder;
import de.clusteval.utils.RecursiveSubDirectoryIterator;

/**
 * @author Christian Wiwie
 */
public class DataSetGeneratorFinder extends JARFinder<DataSetGenerator> {

	/**
	 * Instantiates a new data set generator finder.
	 * 
	 * @param repository
	 *            the repository
	 * @throws RegisterException
	 */
	public DataSetGeneratorFinder(final Repository repository)
			throws RegisterException {
		super(repository, DataSetGenerator.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
	 */
	@Override
	protected boolean checkFile(File file) {
		return file.getName().endsWith("DataSetGenerator.jar");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#classNameForJARFile(java.io.File)
	 */
	@Override
	protected String[] classNamesForJARFile(File f) {
		return new String[]{"de.clusteval.data.dataset.generator."
				+ f.getName().replace(".jar", "")};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#getIterator()
	 */
	@Override
	protected Iterator<File> getIterator() {
		return new RecursiveSubDirectoryIterator(getBaseDir());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#isJARLoaded(java.io.File)
	 */
	@Override
	protected boolean isJARLoaded(File f) {
		return super.isJARLoaded(f)
				&& this.repository.isClassRegistered(DataSetGenerator.class,
						"de.clusteval.data.dataset.generator."
								+ f.getName().replace(".jar", ""));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#getURLClassLoader(java.io.File)
	 */
	@Override
	protected URLClassLoader getURLClassLoader0(File f, final ClassLoader parent)
			throws MalformedURLException {
		URL url = f.toURI().toURL();
		return new DataSetGeneratorURLClassLoader(this, new URL[]{url}, parent);
	}
}

class DataSetGeneratorURLClassLoader extends URLClassLoader {

	protected DataSetGeneratorFinder parent;

	/**
	 * @param urls
	 * @param parent
	 * @param loaderParent
	 */
	public DataSetGeneratorURLClassLoader(DataSetGeneratorFinder parent,
			URL[] urls, ClassLoader loaderParent) {
		super(urls, loaderParent);
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> result = super.loadClass(name, true);

		if (name.startsWith("de.clusteval.data.dataset.generator")
				&& !name.equals("de.clusteval.data.dataset.generator.DataSetGenerator")) {
			if (name.endsWith("DataSetGenerator")) {
				@SuppressWarnings("unchecked")
				Class<? extends DataSetGenerator> dataSetGenerator = (Class<? extends DataSetGenerator>) result;

				this.parent.getRepository().registerClass(
						DataSetGenerator.class, dataSetGenerator);
			}
		}
		return result;
	}
}
