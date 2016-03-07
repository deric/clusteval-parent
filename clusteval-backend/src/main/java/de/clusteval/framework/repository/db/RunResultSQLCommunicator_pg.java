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

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.clusteval.framework.repository.Repository;
import de.clusteval.program.ProgramConfig;
import de.clusteval.run.Run;

/**
 * @author Christian Wiwie
 * 
 */
public class RunResultSQLCommunicator_pg extends DefaultSQLCommunicator {

	/**
	 * @param repository
	 * @param mysqlConfig
	 * @throws DatabaseConnectException
	 */
	public RunResultSQLCommunicator_pg(Repository repository,
			final SQLConfig mysqlConfig) throws DatabaseConnectException {
		super(repository, mysqlConfig);
		this.objectIds = repository.getParent().getSqlCommunicator().objectIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wiwie.wiutils.utils.DefaultSQLCommunicator#getParameterOptimizationMethodId(java.lang
	 * .String)
	 */
	@Override
	protected int getParameterOptimizationMethodId(String name)
			throws SQLException {
		return repository.getParent().getSqlCommunicator()
				.getParameterOptimizationMethodId(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getDataSetFormatId(java.lang.String)
	 */
	@Override
	protected int getDataSetFormatId(String dataSetFormatClassSimpleName)
			throws SQLException {
		return this.repository.getParent().getSqlCommunicator()
				.getDataSetFormatId(dataSetFormatClassSimpleName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.repository.SQLCommunicator#initDB()
	 */
	@Override
	public void initDB() {
		try {
			if (conn == null) {
				conn = DriverManager.getConnection(
						// "jdbc:mysql://"
						// + getServer() + "/" + getDatabase() + "?",
						// getDBUsername(), getDBPassword());
						"jdbc:postgresql://" + getServer() + "/"
								+ getDatabase(), getDBUsername(),
						getDBPassword());
				conn.setAutoCommit(false);
			}
			Statement stmt = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			ResultSet rs = stmt.executeQuery("SELECT id FROM "
					+ this.getTableRepositories() + " WHERE base_path='"
					+ this.repository.getParent().getBasePath() + "';");
			rs.first();
			int parent_repository_id = rs.getInt("id");

			String repositoryType = this.repository.getClass().getSimpleName();
			// Get repository_type_id
			rs = stmt.executeQuery("SELECT id FROM "
					+ this.getTableRepositoryTypes() + " WHERE name='"
					+ repositoryType + "';");
			rs.first();
			int repository_type_id = rs.getInt("id");

			try {
				// Get repositoryId
				rs = stmt.executeQuery("SELECT id FROM "
						+ this.getTableRepositories() + " WHERE base_path='"
						+ this.repository.getBasePath() + "';");
				if (!rs.last() || rs.getRow() == 0) {
					stmt.execute("INSERT INTO "
							+ this.getTableRepositories()
							+ " (base_path,repository_type_id,repository_id) VALUES ('"
							+ this.repository.getBasePath() + "','"
							+ repository_type_id + "','" + parent_repository_id
							+ "');");
				}

				rs = stmt.executeQuery("SELECT id FROM "
						+ this.getTableRepositories() + " WHERE base_path='"
						+ this.repository.getBasePath() + "';");

				rs.first();
				this.setRepositoryId(rs.getInt("id"));
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			conn.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#register(program.ProgramConfig)
	 */
	@Override
	protected int register(ProgramConfig object, final boolean updateOnly) {
		return super.register(object, updateOnly);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunAnalysisId(int)
	 */
	@Override
	protected int getRunAnalysisId(int runId) throws SQLException {
		try {
			return super.getRunAnalysisId(runId);
		} catch (SQLException e) {
			return this.repository.getParent().getSqlCommunicator()
					.getRunAnalysisId(runId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunExecutionId(int)
	 */
	@Override
	protected int getRunExecutionId(int runId) throws SQLException {
		try {
			return super.getRunExecutionId(runId);
		} catch (SQLException e) {
			return this.repository.getParent().getSqlCommunicator()
					.getRunExecutionId(runId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunId(java.lang.String)
	 */
	@Override
	protected int getRunId(final Run run) throws SQLException {
		try {
			return super.getRunId(run);
		} catch (SQLException e) {
			return this.repository.getParent().getSqlCommunicator()
					.getRunId(run);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunResultExecutionId(int)
	 */
	@Override
	protected int getRunResultExecutionId(int runResultId) throws SQLException {
		return this.repository.getParent().getSqlCommunicator()
				.getRunResultExecutionId(runResultId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunResultFormatId(java.lang.String)
	 */
	@Override
	protected int getRunResultFormatId(String runResultFormatSimpleName)
			throws SQLException {
		return this.repository.getParent().getSqlCommunicator()
				.getRunResultFormatId(runResultFormatSimpleName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunResultId(java.lang.String)
	 */
	@Override
	protected int getRunResultId(String uniqueRunIdentifier)
			throws SQLException {
		return this.repository.getParent().getSqlCommunicator()
				.getRunResultId(uniqueRunIdentifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunTypeId(java.lang.String)
	 */
	@Override
	protected int getRunTypeId(String name) throws SQLException {
		return this.repository.getParent().getSqlCommunicator()
				.getRunTypeId(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getStatisticId(java.lang.String)
	 */
	@Override
	protected int getStatisticId(String statisticsName) throws SQLException {
		return this.repository.getParent().getSqlCommunicator()
				.getStatisticId(statisticsName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getDataSetFormatId(java.lang.String)
	 */
	@Override
	protected int getDataSetTypeId(String dataSetTypeClassSimpleName)
			throws SQLException {
		return this.repository.getParent().getSqlCommunicator()
				.getDataSetTypeId(dataSetTypeClassSimpleName);
	}
}
