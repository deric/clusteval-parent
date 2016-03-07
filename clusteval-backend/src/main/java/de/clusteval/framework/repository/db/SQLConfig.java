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
package de.clusteval.framework.repository.db;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Wrapper class to store a sql connection configuration.
 * 
 * @author Christian Wiwie
 * 
 */
public class SQLConfig {

	public static SQLConfig DUMMY_CONFIG = new SQLConfig(false, DB_TYPE.NONE,
			null, null, null, false);

	protected boolean usesSql;
	protected DB_TYPE dbType;
	protected String username;
	protected String database;
	protected String host;
	protected boolean usesPassword;

	/**
	 * @author Christian Wiwie
	 *
	 */
	public enum DB_TYPE {
		NONE, MYSQL, POSTGRESQL
	}

	/**
	 * @param usesSql
	 * @param dbType
	 * @param username
	 * @param database
	 * @param host
	 * @param usesPassword
	 *            Whether the sql connection uses a password to connect and thus
	 *            prompt for it when connecting.
	 */
	public SQLConfig(final boolean usesSql, final DB_TYPE dbType,
			final String username, final String database, final String host,
			final boolean usesPassword) {
		super();
		this.usesSql = usesSql;
		this.dbType = dbType;
		this.username = username;
		this.database = database;
		this.host = host;
		this.usesPassword = usesPassword;
	}

	/**
	 * @return A boolean indicating, whether the repository that this sql
	 *         configuration corresponds to uses sql.
	 */
	public boolean usesSql() {
		return this.usesSql;
	}

	/**
	 * @return
	 */
	public DB_TYPE getDatabaseType() {
		return this.dbType;
	}

	/**
	 * 
	 * @return The username used to connect to the sql database.
	 */
	public String getUsername() {
		return this.username;
	}

	public boolean usesPassword() {
		return this.usesPassword;
	}

	/**
	 * @return The password used to connect to the sql database.
	 */
	public String getPassword() {
		String password;
		Console c = System.console();
		if (c != null) {
			password = new String(System.console().readPassword(
					"SQL password for '%s'@%s: ", username, host));
		}
		// handling for eclipse launching
		else {
			password = "";
			System.out.printf("SQL password for '%s'@%s: ", username, host);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			try {
				password = bufferedReader.readLine();
			} catch (IOException e) {
				// Ignore
			}
		}
		return password;
	}

	/**
	 * 
	 * @return The name of the database to connect to.
	 */
	public String getDatabase() {
		return this.database;
	}

	/**
	 * 
	 * @return The host address to connect to.
	 */
	public String getHost() {
		return this.host;
	}
}
