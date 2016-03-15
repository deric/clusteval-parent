/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api;

import de.clusteval.api.data.IGoldStandardConfig;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.run.IRun;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author deric
 */
public interface Database {

    boolean register(final IRepositoryObject object, final boolean updateOnly);

    boolean unregister(final IRepositoryObject object);

    boolean register(final Class<? extends IRepositoryObject> c);

    boolean unregister(final Class<? extends IRepositoryObject> c);

    /**
     * Initializes the database: 1) establishes a connection 2) tells the
     * database to delete this repository and all corresponding entries
     * (cascading) and recreate a new and empty repository
     *
     * @throws DatabaseConnectException
     */
    void initDB() throws DatabaseConnectException;

    void commitDB();

    /**
     * IDs of objects in DB
     *
     * @return
     */
    Map<IRepositoryObject, Integer> getObjectIds();

    int getParameterOptimizationMethodId(final String name) throws SQLException;

    int getDataSetFormatId(final String dataSetFormatClassSimpleName) throws SQLException;

    int getRunAnalysisId(final int run_id) throws SQLException;

    int getRunExecutionId(final int run_id) throws SQLException;

    int register(IGoldStandardConfig object, final boolean updateOnly);

    int getRunId(final IRun run) throws SQLException;

    int getRunResultExecutionId(int runResultId) throws SQLException;

    int getRunResultFormatId(String runResultFormatSimpleName) throws SQLException;

    int getRunResultId(String uniqueRunIdentifier) throws SQLException;

    int getRunTypeId(String name) throws SQLException;

    int getStatisticId(String statisticsName) throws SQLException;

    int getDataSetTypeId(String dataSetTypeClassSimpleName) throws SQLException;

    int getRepositoryId(String absPath) throws SQLException;

    int getRunResultAnalysisId(int run_results_id) throws SQLException;

    int getRunResultRunAnalysisId(int run_results_analysis_id) throws SQLException;

    /**
     * @param run
     *                  The run which changed its status.
     * @param runStatus
     *                  The new run status.
     * @return True, if the status of the run was updated successfully.
     */
    boolean updateStatusOfRun(final IRun run, String runStatus);
}
