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

import de.clusteval.api.repository.RegisterException;
import de.clusteval.framework.repository.Repository;


/**
 * @author Christian Wiwie
 * 
 */
public class APRunResultFormat extends RunResultFormat {

	/**
	 * @param repo
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public APRunResultFormat(Repository repo, boolean register,
			long changeDate, File absPath) throws RegisterException {
		super(repo, register, changeDate, absPath);
	}

	/**
	 * @param other
	 * @throws RegisterException
	 */
	public APRunResultFormat(final APRunResultFormat other)
			throws RegisterException {
		super(other);
	}
}
