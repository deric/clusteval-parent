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
package de.clusteval.framework.repository.config;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.db.SQLConfig;
import de.clusteval.framework.repository.db.SQLConfig.DB_TYPE;

/**
 * A repository configuration determines certain settings and options for a
 * {@link Repository} and also for the complete backend. This includes for
 * example whether an sql database should be used or how often the supervising
 * threads of the repository should scan for changes.
 * 
 * @author Christian Wiwie
 * 
 */
public class RepositoryConfig {

	/**
	 * This map holds the sleeping times for all threads that check the
	 * repository for changes.
	 */
	protected Map<String, Long> threadingSleepingTimes;

	/**
	 * This method parses a repository configuration from the file at the given
	 * absolute path.
	 * 
	 * <p>
	 * A repository configuration contains several sections and possible
	 * options:
	 * <ul>
	 * <li><b>[mysql]</b></li>
	 * <ul>
	 * <li><b>host</b>: The ip address of the mysql host server.</li>
	 * <li><b>database</b>: The mysql database name.</li>
	 * <li><b>user</b>: The username used to connect to the database.</li>
	 * <li><b>password</b>: The mysql password used to connect to the database.
	 * The password is prompted from the console and not parsed from the file.</li>
	 * </ul>
	 * <li><b>[threading]</b></li>
	 * <li><b>NameOfTheThreadSleepTime</b>: Sleeping time of the thread
	 * 'NameOfTheThread'. This option can be used to control the frequency, with
	 * which the threads check for changes on the filesystem.</li> </ul>
	 * 
	 * @param absConfigPath
	 *            The absolute path of the repository configuration file.
	 * @return The parsed repository configuration.
	 * @throws RepositoryConfigNotFoundException
	 * @throws RepositoryConfigurationException
	 */
	public static RepositoryConfig parseFromFile(final File absConfigPath)
			throws RepositoryConfigNotFoundException,
			RepositoryConfigurationException {
		if (!absConfigPath.exists())
			throw new RepositoryConfigNotFoundException("Repository config \""
					+ absConfigPath + "\" does not exist!");

		Logger log = LoggerFactory.getLogger(RepositoryConfig.class);

		log.debug("Parsing repository config \"" + absConfigPath + "\"");

		try {

			HierarchicalINIConfiguration props = new HierarchicalINIConfiguration(
					absConfigPath);
			props.setThrowExceptionOnMissing(true);

			boolean usesMysql = false;
			SQLConfig mysqlConfig = null;

			if (props.getSections().contains("mysql")
					&& !ClustevalBackendServer.getBackendServerConfiguration()
							.getNoDatabase()) {
				usesMysql = true;
				String mysqlUsername, mysqlDatabase, mysqlHost;
				boolean usesPassword;
				SubnodeConfiguration mysql = props.getSection("mysql");
				mysqlUsername = mysql.getString("user");

				mysqlDatabase = mysql.getString("database");
				mysqlHost = mysql.getString("host");
				usesPassword = !mysql.containsKey("password");
				mysqlConfig = new SQLConfig(usesMysql, DB_TYPE.MYSQL,
						mysqlUsername, mysqlDatabase, mysqlHost, usesPassword);
			} else if (props.getSections().contains("postgresql")
					&& !ClustevalBackendServer.getBackendServerConfiguration()
							.getNoDatabase()) {
				usesMysql = true;
				String username, db, host;
				boolean usesPassword;
				SubnodeConfiguration mysql = props.getSection("postgresql");
				username = mysql.getString("user");

				db = mysql.getString("database");
				host = mysql.getString("host");
				usesPassword = !mysql.containsKey("password");
				mysqlConfig = new SQLConfig(usesMysql, DB_TYPE.POSTGRESQL,
						username, db, host, usesPassword);
			} else
				mysqlConfig = new SQLConfig(false, DB_TYPE.NONE, "", "", "",
						false);

			Map<String, Long> threadingSleepTimes = new HashMap<String, Long>();

			if (props.getSections().contains("threading")) {
				SubnodeConfiguration threading = props.getSection("threading");
				Iterator<String> it = threading.getKeys();
				while (it.hasNext()) {
					String key = it.next();
					if (key.endsWith("SleepTime")) {
						String subKey = key.substring(0,
								key.indexOf("SleepTime"));
						try {
							threadingSleepTimes.put(subKey,
									threading.getLong(key));
						} catch (Exception e) {
							// in case anything goes wrong, we just ignore this
							// option
							e.printStackTrace();
						}
					}
				}
			}

			return new RepositoryConfig(mysqlConfig, threadingSleepTimes);
		} catch (ConfigurationException e) {
			throw new RepositoryConfigurationException(e.getMessage());
		} catch (NoSuchElementException e) {
			throw new RepositoryConfigurationException(e.getMessage());
		}
	}

	/**
	 * The configuration of the mysql connection of the repository.
	 */
	protected SQLConfig mysqlConfig;

	/**
	 * Creates a new repository configuration.
	 * 
	 * @param mysqlConfig
	 *            The mysql configuration for the repository.
	 * @param threadingSleepTimes
	 *            The sleep times of the threads created for the repository.
	 */
	public RepositoryConfig(final SQLConfig mysqlConfig,
			final Map<String, Long> threadingSleepTimes) {
		super();
		this.mysqlConfig = mysqlConfig;
		this.threadingSleepingTimes = threadingSleepTimes;
	}

	/**
	 * @return The mysql configuration of this repository.
	 */
	public SQLConfig getMysqlConfig() {
		return this.mysqlConfig;
	}

	/**
	 * Override the mysql configuration of this repository.
	 * 
	 * @param mysqlConfig
	 *            The new mysql configuration.
	 */
	public void setMysqlConfig(final SQLConfig mysqlConfig) {
		this.mysqlConfig = mysqlConfig;
	}

	/**
	 * @return The thread sleep times for the repository.
	 * @see #threadingSleepingTimes
	 */
	public Map<String, Long> getThreadSleepTimes() {
		return this.threadingSleepingTimes;
	}

}
