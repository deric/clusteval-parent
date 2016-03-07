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
package de.clusteval.cluster.quality;

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
public class ClusteringQualityMeasureFinder
		extends
			JARFinder<ClusteringQualityMeasure> {

	/**
	 * Instantiates a new clustering quality measure finder.
	 * 
	 * @param repository
	 *            The repository to register the new data configurations at.
	 * @throws RegisterException
	 */
	public ClusteringQualityMeasureFinder(final Repository repository)
			throws RegisterException {
		super(repository, ClusteringQualityMeasure.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#classForJARFile(java.io.File)
	 */
	@Override
	protected String[] classNamesForJARFile(File f) {
		return new String[]{"de.clusteval.cluster.quality."
				+ f.getName().replace(".jar", "")};
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
		return new ClusteringQualityMeasureURLClassLoader(this, new URL[]{url},
				parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.JARFinder#isJARLoaded(java.io.File)
	 */
	@Override
	protected boolean isJARLoaded(File f) {
		return super.isJARLoaded(f)
				&& this.repository.isClassRegistered(
						ClusteringQualityMeasure.class,
						classNamesForJARFile(f)[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
	 */
	@Override
	protected boolean checkFile(File file) {
		return file.getName().endsWith("ClusteringQualityMeasure.jar");
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (this.getClass().toString() + this.repository).hashCode();
	}
}

class ClusteringQualityMeasureURLClassLoader extends URLClassLoader {

	protected ClusteringQualityMeasureFinder parent;

	/**
	 * @param urls
	 * @param parent
	 * @param loaderParent
	 */
	public ClusteringQualityMeasureURLClassLoader(
			ClusteringQualityMeasureFinder parent, URL[] urls,
			ClassLoader loaderParent) {
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

		if (name.startsWith("de.clusteval.cluster.quality")) {
			if (!name
					.equals("de.clusteval.cluster.quality.ClusteringQualityMeasure")
					&& !name.equals("de.clusteval.cluster.quality.ClusteringQualityMeasureR")
					&& name.endsWith("ClusteringQualityMeasure")) {
				@SuppressWarnings("unchecked")
				Class<? extends ClusteringQualityMeasure> qualityMeasure = (Class<? extends ClusteringQualityMeasure>) result;

				this.parent.getRepository().registerClass(
						ClusteringQualityMeasure.class, qualityMeasure);
			}
		}
		return result;
	}
}
