/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.framework.repository.db;

import de.clusteval.api.SQLConfig;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRun;
import de.clusteval.api.program.ProgramConfig;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultSQLCommunicator extends DefaultSQLCommunicator {

    /**
     * @param repository
     * @param mysqlConfig
     * @throws DatabaseConnectException
     */
    public RunResultSQLCommunicator(IRepository repository,
            final SQLConfig mysqlConfig) throws DatabaseConnectException {
        super(repository, mysqlConfig);
        this.objectIds = repository.getParent().getDb().getObjectIds();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.wiwie.wiutils.utils.DefaultSQLCommunicator#getParameterOptimizationMethodId(java.lang
     * .String)
     */
    @Override
    public int getParameterOptimizationMethodId(String name)
            throws SQLException {
        return repository.getParent().getDb().getParameterOptimizationMethodId(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getDataSetFormatId(java.lang.String)
     */
    @Override
    public int getDataSetFormatId(String dataSetFormatClassSimpleName)
            throws SQLException {
        return this.repository.getParent().getDb()
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
                        this.queryBuilder.getConnectionstring(),
                        getDBUsername(), getDBPassword());
                conn.setAutoCommit(false);
            }
            Statement stmt = this.queryBuilder.createStatement(conn);

            ResultSet rs = stmt.executeQuery(this.queryBuilder.select(this
                    .getTableRepositories(), "id", new String[]{"base_path"},
                    new String[]{this.repository.getParent().getBasePath()}));
            rs.first();
            int parent_repository_id = rs.getInt("id");

            String repositoryType = this.repository.getClass().getSimpleName();
            // Get repository_type_id
            rs = stmt.executeQuery(this.queryBuilder.select(
                    this.getTableRepositoryTypes(), "id", new String[]{"name"},
                    new String[]{repositoryType}));
            rs.first();
            int repository_type_id = rs.getInt("id");

            try {
                // Get repositoryId
                rs = stmt.executeQuery(this.queryBuilder.select(
                        this.getTableRepositories(), "id",
                        new String[]{"base_path"},
                        new String[]{this.repository.getBasePath()}));
                if (!rs.last() || rs.getRow() == 0) {

                    stmt.execute(this.queryBuilder.insert(
                            this.getTableRepositories(), new String[]{
                                "base_path", "repository_type_id",
                                "repository_id"}, new String[]{
                                this.repository.getBasePath(),
                                repository_type_id + "",
                                parent_repository_id + ""}));
                }

                rs = stmt.executeQuery(this.queryBuilder.select(
                        this.getTableRepositories(), "id",
                        new String[]{"base_path"},
                        new String[]{this.repository.getBasePath()}));

                rs.first();
                this.setRepositoryId(rs.getInt("id"));
            } catch (SQLException ex) {
                this.exceptionHandler.handleException(ex);
                ex.printStackTrace();
            }
            conn.commit();
        } catch (SQLException e1) {
            this.exceptionHandler.handleException(e1);
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
    public int getRunAnalysisId(int runId) throws SQLException {
        try {
            return super.getRunAnalysisId(runId);
        } catch (SQLException e) {
            this.exceptionHandler.handleException(e);
            return this.repository.getParent().getDb().getRunAnalysisId(runId);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunExecutionId(int)
     */
    @Override
    public int getRunExecutionId(int runId) throws SQLException {
        try {
            return super.getRunExecutionId(runId);
        } catch (SQLException e) {
            this.exceptionHandler.handleException(e);
            return this.repository.getParent().getDb().getRunExecutionId(runId);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunId(java.lang.String)
     */
    @Override
    public int getRunId(final IRun run) throws SQLException {
        try {
            return super.getRunId(run);
        } catch (SQLException e) {
            this.exceptionHandler.handleException(e);
            return this.repository.getParent().getDb().getRunId(run);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunResultExecutionId(int)
     */
    @Override
    public int getRunResultExecutionId(int runResultId) throws SQLException {
        return this.repository.getParent().getDb()
                .getRunResultExecutionId(runResultId);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunResultFormatId(java.lang.String)
     */
    @Override
    public int getRunResultFormatId(String runResultFormatSimpleName) throws SQLException {
        return this.repository.getParent().getDb()
                .getRunResultFormatId(runResultFormatSimpleName);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunResultId(java.lang.String)
     */
    @Override
    public int getRunResultId(String uniqueRunIdentifier) throws SQLException {
        return this.repository.getParent().getDb()
                .getRunResultId(uniqueRunIdentifier);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getRunTypeId(java.lang.String)
     */
    @Override
    public int getRunTypeId(String name) throws SQLException {
        return this.repository.getParent().getDb().getRunTypeId(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getStatisticId(java.lang.String)
     */
    @Override
    public int getStatisticId(String statisticsName) throws SQLException {
        return this.repository.getParent().getDb().getStatisticId(statisticsName);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.DefaultSQLCommunicator#getDataSetFormatId(java.lang.String)
     */
    @Override
    public int getDataSetTypeId(String dataSetTypeClassSimpleName) throws SQLException {
        return this.repository.getParent().getDb()
                .getDataSetTypeId(dataSetTypeClassSimpleName);
    }
}
