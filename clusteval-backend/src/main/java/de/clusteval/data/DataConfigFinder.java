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
package de.clusteval.data;

import java.io.File;
import java.util.Iterator;

import de.wiwie.wiutils.utils.ArrayIterator;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.FileFinder;

/**
 * Objects of this class look for new data configuration-files in the directory
 * defined in the corresponding repository (see
 * {@link Repository#dataConfigBasePath}).
 * 
 * @author Christian Wiwie
 * 
 * 
 */
public class DataConfigFinder extends FileFinder<DataConfig> {

	/**
	 * Instantiates a new data configuration finder.
	 * 
	 * @param repository
	 *            The repository to register the new data configurations at.
	 * @throws RegisterException
	 */
	public DataConfigFinder(final Repository repository)
			throws RegisterException {
		super(repository, DataConfig.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
	 */
	@Override
	protected boolean checkFile(File file) {
		return file.isFile() && file.getName().endsWith(".dataconfig");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#getIterator()
	 */
	@Override
	protected Iterator<File> getIterator() {
		return new ArrayIterator<File>(getBaseDir().listFiles());
	}
}
