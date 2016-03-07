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
package de.clusteval.data.distance;

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
 * 
 */
public class DistanceMeasureFinder extends JARFinder<DistanceMeasure> {

	/**
	 * @param repository
	 * @throws RegisterException
	 */
	public DistanceMeasureFinder(Repository repository)
			throws RegisterException {
		super(repository, DistanceMeasure.class);
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
	 * @see de.wiwie.wiutils.utils.JARFinder#getURLClassLoader0(java.io.File)
	 */
	@Override
	protected URLClassLoader getURLClassLoader0(File f, final ClassLoader parent)
			throws MalformedURLException {
		URL url = f.toURI().toURL();
		return new DistanceMeasureURLClassLoader(this, new URL[]{url}, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
	 */
	@Override
	protected boolean checkFile(File file) {
		return file.getName().endsWith("DistanceMeasure.jar");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#classNamesForJARFile(java.io.File)
	 */
	@Override
	protected String[] classNamesForJARFile(File f) {
		return new String[]{"de.clusteval.data.distance."
				+ f.getName().replace(".jar", "")};
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
						.isClassRegistered("de.clusteval.data.distance."
								+ f.getName().replace(".jar", ""));
	}

}

class DistanceMeasureURLClassLoader extends URLClassLoader {

	protected DistanceMeasureFinder parent;

	/**
	 * @param urls
	 * @param parent
	 * @param loaderParent
	 */
	public DistanceMeasureURLClassLoader(DistanceMeasureFinder parent,
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

		if (name.startsWith("de.clusteval.data.distance")
				&& !name.equals("de.clusteval.data.distance.DistanceMeasure")
				&& !name.equals("de.clusteval.data.distance.DistanceMeasureR")) {
			if (name.endsWith("DistanceMeasure")) {
				@SuppressWarnings("unchecked")
				Class<? extends DistanceMeasure> distanceMeasure = (Class<? extends DistanceMeasure>) result;

				this.parent.getRepository().registerClass(
						(Class<? extends DistanceMeasure>) distanceMeasure);
			}
		}
		return result;
	}
}
