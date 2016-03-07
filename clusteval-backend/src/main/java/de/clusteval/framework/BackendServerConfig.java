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
package de.clusteval.framework;

/**
 * @author Christian Wiwie
 * 
 */
public class BackendServerConfig {

	protected int numberOfThreads;

	protected boolean checkForRunResults;

	protected boolean noDatabase;

	protected String rServeHost;

	protected int rServePort;

	/**
	 * 
	 */
	public BackendServerConfig() {
		super();

		// default values
		this.numberOfThreads = Math.max((int) (Runtime.getRuntime()
				.availableProcessors() / 2.0), 1);
		this.checkForRunResults = true;
		this.noDatabase = false;
		this.rServeHost = "127.0.0.1";
		this.rServePort = 6311;
	}

	/**
	 * @return The maximal number of threads.
	 */
	public int getNumberOfThreads() {
		return this.numberOfThreads;
	}

	/**
	 * 
	 * @return True, if this backend server should check for run results in its
	 *         repository.
	 */
	public boolean getCheckForRunResults() {
		return this.checkForRunResults;
	}

	/**
	 * @param checkForRunResults
	 *            True, if this backend server should check for run results in
	 *            its repository.
	 */
	public void setCheckForRunResults(final boolean checkForRunResults) {
		this.checkForRunResults = checkForRunResults;
	}

	/**
	 * @param noDatabase
	 *            True, if this backend server should use a database.
	 */
	public void setNoDatabase(final boolean noDatabase) {
		this.noDatabase = noDatabase;
	}

	public boolean getNoDatabase() {
		return this.noDatabase;
	}

	public String getRserveHost() {
		return this.rServeHost;
	}

	public void setRserveHost(final String host) {
		this.rServeHost = host;
	}

	public int getRservePort() {
		return this.rServePort;
	}

	public void setRservePort(final int port) {
		this.rServePort = port;
	}
}
