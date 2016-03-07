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
public class RunResultFormatFinder extends JARFinder<RunResultFormat> {

	/**
	 * Instantiates a new data set format finder.
	 * 
	 * @param repository
	 *            the repository
	 * @throws RegisterException
	 */
	public RunResultFormatFinder(final Repository repository)
			throws RegisterException {
		super(repository, RunResultFormat.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#getURLClassLoader(java.io.File)
	 */
	@SuppressWarnings("unused")
	@Override
	protected URLClassLoader getURLClassLoader0(File f, final ClassLoader parent)
			throws MalformedURLException {

		// add URLS for JARs into list
		List<URL> urls = this.search(new File(this.repository
				.getBasePath(RunResultFormat.class)));
		// load corresponding classes of URLs in list
		return new RunResultFormatURLClassLoader(this,
				urls.toArray(new URL[0]), parent);
	}

	protected List<URL> search(final File f) throws MalformedURLException {
		List<URL> result = new ArrayList<URL>();
		if (f.isDirectory())
			for (File child : f.listFiles())
				result.addAll(search(child));
		else if (f.getName().endsWith("RunResultFormat.jar"))
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
		return file.getName().endsWith("RunResultFormat.jar");
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
				&& this.repository.isClassRegistered(RunResultFormat.class,
						classNamesForJARFile(f)[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#classNamesForJARFile(java.io.File)
	 */
	@Override
	protected String[] classNamesForJARFile(File f) {
		return new String[]{
				"de.clusteval.run.result.format."
						+ f.getName().replace(".jar", ""),
				"de.clusteval.run.result.format."
						+ f.getName().replace(".jar", "Parser")};
	}
}

class RunResultFormatURLClassLoader extends URLClassLoader {

	protected RunResultFormatFinder parent;

	/**
	 * @param urls
	 * @param parent
	 * @param loaderParent
	 */
	public RunResultFormatURLClassLoader(RunResultFormatFinder parent,
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

		if (name.startsWith("de.clusteval.run.result.format")
				&& !name.equals("de.clusteval.run.result.format.RunResultFormat")) {
			if (name.endsWith("RunResultFormat")) {
				@SuppressWarnings("unchecked")
				Class<? extends RunResultFormat> runResultFormat = (Class<? extends RunResultFormat>) result;

				this.parent.getRepository().registerClass(
						RunResultFormat.class, runResultFormat);
				// /*
				// * Set internal runresult format
				// */
				// if (name.endsWith(".TabSeparatedRunResultFormat")) {
				// RunResultFormat.INTERNAL_RUNRESULT_FORMAT = runResultFormat;
				// }

			} else if (name.endsWith("RunResultFormatParser")) {
				@SuppressWarnings("unchecked")
				Class<? extends RunResultFormatParser> runResultFormatParser = (Class<? extends RunResultFormatParser>) result;

				this.parent.getRepository().registerRunResultFormatParser(
						runResultFormatParser);

			}
		}
		return result;
	}
}
