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
package de.clusteval.data.dataset;

import java.io.File;
import java.util.Iterator;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.FileFinder;
import de.clusteval.utils.SubDirectoryIterator;

/**
 * Objects of this class look for new run-files in the run-directory defined in
 * the corresponding repository.
 * 
 * @author Christian Wiwie
 * 
 * 
 */
public class DataSetFinder extends FileFinder<DataSet> {

	/**
	 * Instantiates a new run finder.
	 * 
	 * @param repository
	 *            the repository
	 * @throws RegisterException
	 */
	public DataSetFinder(final Repository repository) throws RegisterException {
		super(repository, DataSet.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
	 */
	@Override
	protected boolean checkFile(File file) {
		try {
			DataSetAttributeParser p;
			if (file.isFile()
					&& !file.getParentFile().getName().equals("configs")) {
				p = new DataSetAttributeParser(file.getAbsolutePath());
				p.process();
				return p.getAttributeValues().size() > 0;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.Finder#getIterator()
	 */
	@Override
	protected Iterator<File> getIterator() {
		return new SubDirectoryIterator(getBaseDir());
	}
}
