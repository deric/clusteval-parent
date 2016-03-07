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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.JARFinder;
import de.clusteval.utils.RecursiveSubDirectoryIterator;

/**
 * @author Christian Wiwie
 */
public class RunDataStatisticFinder extends JARFinder<RunDataStatistic> {

	/**
	 * Instantiates a new data set format finder.
	 * 
	 * @param repository
	 *            the repository
	 * @throws RegisterException
	 */
	public RunDataStatisticFinder(final Repository repository)
			throws RegisterException {
		super(repository, RunDataStatistic.class);
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
		// load corresponding classes of URLs in list
		return new RunDataStatisticURLClassLoader(this, new URL[]{url}, parent);
	}

	protected List<URL> search(final File f) throws MalformedURLException {
		List<URL> result = new ArrayList<URL>();
		if (f.isDirectory())
			for (File child : f.listFiles())
				result.addAll(search(child));
		else if (f.getName().endsWith("RunDataStatistic.jar"))
			result.add(f.toURI().toURL());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
	 */
	@Override
	protected boolean checkFile(File file) {
		return file.getName().endsWith("RunDataStatistic.jar");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#classNamesForJARFile(java.io.File)
	 */
	@Override
	protected String[] classNamesForJARFile(File f) {
		return new String[]{
				"de.clusteval.run.statistics."
						+ f.getName().replace(".jar", ""),
				"de.clusteval.run.statistics."
						+ f.getName().replace(".jar", "Calculator")};
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
				&& this.repository
						.isClassRegistered(classNamesForJARFile(f)[0]);
	}
}

class RunDataStatisticURLClassLoader extends URLClassLoader {

	protected RunDataStatisticFinder parent;

	/**
	 * @param urls
	 * @param parent
	 * @param loaderParent
	 */
	public RunDataStatisticURLClassLoader(RunDataStatisticFinder parent,
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
		Class<?> result = super.loadClass(name);

		if (name.startsWith("de.clusteval.run.statistics")
				&& !name.equals("de.clusteval.run.statistics.RunDataStatistic")) {
			if (name.endsWith("RunDataStatistic")) {
				@SuppressWarnings("unchecked")
				Class<? extends RunDataStatistic> runStatistic = (Class<? extends RunDataStatistic>) result;
				this.parent.getRepository().registerClass(
						RunDataStatistic.class, runStatistic);

			} else if (name.endsWith("RunDataStatisticCalculator")
					&& !name.equals("de.clusteval.run.statistics.RunDataStatisticRCalculator")) {
				@SuppressWarnings("unchecked")
				Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> runStatisticCalculator = (Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>>) result;
				this.parent.getRepository().registerRunDataStatisticCalculator(
						runStatisticCalculator);
			}
		}
		return result;
	}
}
