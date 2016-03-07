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

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wiwie.wiutils.utils.Formatter;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.type.DataSetType;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.program.DoubleProgramParameter;
import de.clusteval.program.IntegerProgramParameter;
import de.clusteval.program.Program;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.program.StringProgramParameter;
import de.clusteval.run.AnalysisRun;
import de.clusteval.run.ClusteringRun;
import de.clusteval.run.DataAnalysisRun;
import de.clusteval.run.ExecutionRun;
import de.clusteval.run.InternalParameterOptimizationRun;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.Run;
import de.clusteval.run.RunAnalysisRun;
import de.clusteval.run.RunDataAnalysisRun;
import de.clusteval.run.result.AnalysisRunResult;
import de.clusteval.run.result.ClusteringRunResult;
import de.clusteval.run.result.DataAnalysisRunResult;
import de.clusteval.run.result.ExecutionRunResult;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunAnalysisRunResult;
import de.clusteval.run.result.RunDataAnalysisRunResult;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.utils.Statistic;

/**
 * The sql communicator is responsible for the communication between
 * {@link Repository} and mysql database.
 * 
 * <p>
 * Therefore a sql communicator has a connection {@link #conn} and a
 * {@link #repository}
 * 
 * @author Christian Wiwie
 * 
 */
@SuppressWarnings({"rawtypes"})
public abstract class SQLCommunicator_pg {

	/*
	 * Stores the connection to the database
	 */
	protected static Connection conn;

	/*
	 * One SQLCommunicator belongs to exactly one repository
	 */
	protected Repository repository;

	/*
	 * Stores the id of the repository in the DB
	 */
	private int repositoryId;

	protected Map<RepositoryObject, Integer> objectIds;

	/**
	 * @param repository
	 */
	public SQLCommunicator_pg(final Repository repository) {
		super();
		this.repository = repository;
		this.objectIds = new HashMap<RepositoryObject, Integer>();
	}

	protected abstract String getServer();

	protected abstract String getDatabase();

	protected abstract String getDBUsername();

	protected abstract String getDBPassword();

	protected int getObjectId(final RepositoryObject object) {
		if (this.objectIds.containsKey(object))
			return this.objectIds.get(object);
		return -1;
	}

	protected int insert(final String tableName, final String[] columnNames,
			final List<String[]> values) throws SQLException {

		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		// sb.append(this.getDatabase());
		// sb.append(".");
		sb.append(tableName);
		sb.append(" (");
		for (String s : columnNames) {
			sb.append("");
			sb.append(s);
			sb.append(",");
		}
		// remove last comma
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") VALUES");
		for (String[] vals : values) {
			sb.append(" (");
			for (String s : vals) {
				if (s == null)
					sb.append("null,");
				else {
					sb.append("'");
					sb.append(s);
					sb.append("',");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("),");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(";");

		PreparedStatement prepStmt = conn.prepareStatement(sb.toString(),
				Statement.RETURN_GENERATED_KEYS);
		try {
			prepStmt.executeUpdate();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);
		} finally {
			prepStmt.close();
		}
	}

	protected int insert(final String tableName, final String[] columnNames,
			final String[] values) throws SQLException {

		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		// sb.append(this.getDatabase());
		// sb.append(".");
		sb.append(tableName);
		sb.append(" (");
		for (String s : columnNames) {
			sb.append("");
			sb.append(s);
			sb.append(",");
		}
		// remove last comma
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") VALUES (");
		for (String s : values) {
			if (s == null)
				sb.append("null,");
			else {
				sb.append("'");
				sb.append(s);
				sb.append("',");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(");");

		PreparedStatement prepStmt = conn.prepareStatement(sb.toString(),
				Statement.RETURN_GENERATED_KEYS);
		try {
			prepStmt.executeUpdate();
			ResultSet rs = prepStmt.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);
		} finally {
			prepStmt.close();
		}
	}

	public void disableKeys(final String tableName) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ");
		// sb.append(this.getDatabase());
		// sb.append(".");
		sb.append(tableName);
		sb.append(" DISABLE KEYS");
		PreparedStatement prepStmt = conn.prepareStatement(sb.toString(),
				Statement.NO_GENERATED_KEYS);
		try {
			prepStmt.executeUpdate();
		} finally {
			prepStmt.close();
		}
	}

	public void enableKeys(final String tableName) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ");
		// sb.append(this.getDatabase());
		// sb.append(".");
		sb.append(tableName);
		sb.append(" ENABLE KEYS");
		PreparedStatement prepStmt = conn.prepareStatement(sb.toString(),
				Statement.NO_GENERATED_KEYS);
		try {
			prepStmt.executeUpdate();
		} finally {
			prepStmt.close();
		}
	}

	protected void tryInsert(final String tableName,
			final String[] columnNames, final String[] values) {
		try {
			conn.commit();
			this.insert(tableName, columnNames, values);
		} catch (SQLException e) {
//			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
//				e1.printStackTrace();
			}
		}
	}

	protected boolean update(final String tableName,
			final String[] columnNames, final String[] values, final int rowId)
			throws SQLException {

		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE ");
		// sb.append(this.getDatabase());
		// sb.append(".");
		sb.append(tableName);
		sb.append(" SET ");
		for (int i = 0; i < columnNames.length; i++) {
			String s = columnNames[i];
			String v = values[i];

			sb.append("");
			sb.append(s);
			sb.append("='");
			sb.append(v);
			sb.append("',");
		}
		// remove last comma
		sb.deleteCharAt(sb.length() - 1);
		sb.append(" WHERE id='");
		sb.append(rowId);
		sb.append("';");

		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		try {
			stmt.execute(sb.toString());
			return true;
		} finally {
			stmt.close();
		}
	}

	// protected boolean update(final String tableName,
	// final String[] columnNames, final String[] values, final int rowId)
	// throws SQLException {
	// final String[] newColumns = Arrays.copyOf(columnNames,
	// columnNames.length + 1);
	// newColumns[newColumns.length - 1] = "id";
	// final String[] newValues = Arrays.copyOf(values, values.length + 1);
	// newValues[newValues.length - 1] = "" + rowId;
	// delete(tableName, rowId);
	// insert(tableName, newColumns, newValues);
	// return true;
	// }

	protected boolean delete(final String tableName, final int rowId,
			final String columnName) throws SQLException {
		StringBuilder sb = new StringBuilder();

		sb.append("DELETE FROM ");
		// sb.append(this.getDatabase());
		// sb.append(".");
		sb.append(tableName);
		sb.append(" WHERE ");
		sb.append(columnName);
		sb.append("='");
		sb.append(rowId);
		sb.append("';");

		Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		try {
			stmt.execute(sb.toString());
			return true;
		} finally {
			stmt.close();
		}
	}

	/**
	 * By default we delete rows where id=rowId
	 * 
	 * @param tableName
	 * @param rowId
	 * @return
	 * @throws SQLException
	 */
	protected boolean delete(final String tableName, final int rowId)
			throws SQLException {
		return delete(tableName, rowId, "id");
	}

	protected int select(final String tableName, final String columnName,
			final String[] columnNames, final String[] values)
			throws SQLException {

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT " + columnName + " FROM ");
		// sb.append(this.getDatabase());
		// sb.append(".");
		sb.append(tableName);
		sb.append(" WHERE ");
		for (int c = 0; c < columnNames.length; c++) {
			String s = columnNames[c];
			String v = values[c];
			sb.append("");
			sb.append(s);
			sb.append("='");
			sb.append(v);
			sb.append("'");
			if (c < columnNames.length - 1)
				sb.append(" AND ");
		}
		sb.append(";");

		PreparedStatement prepStmt = conn.prepareStatement(sb.toString(),
				Statement.RETURN_GENERATED_KEYS);
		try {
			ResultSet rs = prepStmt.executeQuery();
			rs.next();
			return rs.getInt(columnName);
		} finally {
			prepStmt.close();
		}
	}

	protected abstract String getTableRepositories();

	protected abstract String getTableRepositoryTypes();

	protected abstract String getTableClusterings();

	protected abstract String getTableClusters();

	protected abstract String getTableClusterObjects();

	protected abstract String getTableClusteringQualityMeasures();

	protected abstract String getTableDataConfigs();

	protected abstract String getTableDataSetConfigs();

	protected abstract String getTableDataSetTypes();

	protected abstract String getTableDatasets();

	protected abstract String getTableGoldStandardConfigs();

	protected abstract String getTableGoldStandards();

	protected abstract String getTableOptimizableProgramParameters();

	protected abstract String getTableParameterOptimizationMethods();

	protected abstract String getTableParameterSets();

	protected abstract String getTableParameterSetParameters();

	protected abstract String getTableParameterSetIterations();

	protected abstract String getTableParameterSetParameterValues();

	protected abstract String getTableParameterOptimizationQualities();

	protected abstract String getTableProgramConfigs();

	protected abstract String getTableProgramParameter();

	protected abstract String getTableProgramParameterType();

	protected abstract String getTablePrograms();

	protected abstract String getTableProgramConfigsCompatibleDataSetFormats();

	protected abstract String getTableRuns();

	protected abstract String getTableRunsAnalysis();

	protected abstract String getTableRunsAnalysisData();

	protected abstract String getTableRunsAnalysisDataDataIdentifiers();

	protected abstract String getTableRunsAnalysisRun();

	protected abstract String getTableRunsAnalysisRunRunIdentifiers();

	protected abstract String getTableRunsAnalysisRunData();

	protected abstract String getTableRunsAnalysisRunDataDataIdentifiers();

	protected abstract String getTableRunsAnalysisRunDataRunIdentifiers();

	protected abstract String getTableRunsAnalysisStatistics();

	protected abstract String getTableRunsClustering();

	protected abstract String getTableRunsExecution();

	protected abstract String getTableRunsExecutionDataConfigs();

	protected abstract String getTableRunsExecutionParameterValues();

	protected abstract String getTableRunsExecutionProgramConfigs();

	protected abstract String getTableRunsExecutionQualityMeasures();

	protected abstract String getTableRunsInternalParameterOptimization();

	protected abstract String getTableRunsParameterOptimization();

	protected abstract String getTableRunsParameterOptimizationMethods();

	protected abstract String getTableRunsParameterOptimizationQualityMeasures();

	protected abstract String getTableRunsParameterOptimizationParameters();

	protected abstract String getTableRunResultsExecution();

	protected abstract String getTableRunResultFormats();

	protected abstract String getTableRunResultsClustering();

	protected abstract String getTableRunResultsClusteringsQuality();

	protected abstract String getTableRunResultsParameterOptimization();

	protected abstract String getTableRunResults();

	protected abstract String getTableRunResultsAnalysis();

	protected abstract String getTableRunResultsDataAnalysis();

	protected abstract String getTableRunResultsRunAnalysis();

	protected abstract String getTableRunResultsRunDataAnalysis();

	protected abstract String getTableRunTypes();

	protected abstract String getTableStatistics();

	protected abstract String getTableStatisticsData();

	protected abstract String getTableStatisticsRun();

	protected abstract String getTableStatisticsRunData();

	protected abstract boolean register(final Run run, final boolean updateOnly);

	protected abstract boolean register(final AnalysisRun<Statistic> run,
			final boolean updateOnly);

	protected abstract int register(final DataAnalysisRun run,
			final boolean updateOnly);

	protected abstract int register(final RunAnalysisRun run,
			final boolean updateOnly);

	protected abstract int register(final RunDataAnalysisRun run,
			final boolean updateOnly);

	protected abstract boolean register(final ExecutionRun run,
			final boolean updateOnly);

	protected abstract int register(final ClusteringRun run,
			final boolean updateOnly);

	protected abstract int register(final ParameterOptimizationRun run,
			final boolean updateOnly);

	protected abstract int register(final InternalParameterOptimizationRun run,
			final boolean updateOnly);

	protected abstract int register(final ProgramConfig object,
			final boolean updateOnly);

	protected abstract int register(final Program object,
			final boolean updateOnly);

	// TODO
	protected boolean register(final RepositoryObject object,
			final boolean updateOnly) {
		int result;
		if (object instanceof DataSet)
			result = this.register((DataSet) object, updateOnly);
		else if (object instanceof ClusteringRun)
			result = this.register((ClusteringRun) object, updateOnly);
		else if (object instanceof ClusteringRunResult) {
			result = this.register((RunResult) object);
		} else if (object instanceof DataAnalysisRun)
			result = this.register((DataAnalysisRun) object, updateOnly);
		else if (object instanceof DataAnalysisRunResult) {
			result = this.register((RunResult) object);
		} else if (object instanceof DataConfig)
			result = this.register((DataConfig) object, updateOnly);
		else if (object instanceof DataSetConfig)
			result = this.register((DataSetConfig) object, updateOnly);
		else if (object instanceof DoubleProgramParameter)
			result = this.register((DoubleProgramParameter) object);
		else if (object instanceof GoldStandard)
			result = this.register((GoldStandard) object, updateOnly);
		else if (object instanceof GoldStandardConfig)
			result = this.register((GoldStandardConfig) object, updateOnly);
		else if (object instanceof IntegerProgramParameter)
			result = this.register((IntegerProgramParameter) object);
		else if (object instanceof InternalParameterOptimizationRun)
			result = this.register((InternalParameterOptimizationRun) object,
					updateOnly);
		else if (object instanceof ParameterOptimizationResult) {
			result = this.register((RunResult) object);
		} else if (object instanceof ParameterOptimizationRun)
			result = this.register((ParameterOptimizationRun) object,
					updateOnly);
		else if (object instanceof Program)
			result = this.register((Program) object, updateOnly);
		else if (object instanceof ProgramConfig)
			result = this.register((ProgramConfig) object, updateOnly);
		else if (object instanceof RunAnalysisRun)
			result = this.register((RunAnalysisRun) object, updateOnly);
		else if (object instanceof RunAnalysisRunResult) {
			result = this.register((RunResult) object);
		} else if (object instanceof RunDataAnalysisRun) {
			result = this.register((RunDataAnalysisRun) object, updateOnly);
		} else if (object instanceof RunDataAnalysisRunResult) {
			result = this.register((RunResult) object);
		} else if (object instanceof StringProgramParameter) {
			result = this.register((StringProgramParameter) object);
		} else if (object instanceof Clustering) {
			result = this.register((Clustering) object);
		} else
			return false;
		if (result != -1)
			this.objectIds.put(object, result);
		return result != -1;
	}

	// TODO
	protected boolean unregister(final RepositoryObject object) {
		int result;
		if (object instanceof DataSet)
			result = this.unregister((DataSet) object);
		else if (object instanceof ClusteringRun)
			result = this.unregister((ClusteringRun) object);
		else if (object instanceof ClusteringRunResult)
			result = this.unregister((ClusteringRunResult) object);
		else if (object instanceof DataAnalysisRun)
			result = this.unregister((DataAnalysisRun) object);
		else if (object instanceof DataAnalysisRunResult)
			result = this.unregister((DataAnalysisRunResult) object);
		else if (object instanceof DataConfig)
			result = this.unregister((DataConfig) object);
		else if (object instanceof DataSetConfig)
			result = this.unregister((DataSetConfig) object);
		else if (object instanceof DoubleProgramParameter)
			result = this.unregister((DoubleProgramParameter) object);
		else if (object instanceof GoldStandard)
			result = this.unregister((GoldStandard) object);
		else if (object instanceof GoldStandardConfig)
			result = this.unregister((GoldStandardConfig) object);
		else if (object instanceof IntegerProgramParameter)
			result = this.unregister((IntegerProgramParameter) object);
		else if (object instanceof InternalParameterOptimizationRun)
			result = this.unregister((InternalParameterOptimizationRun) object);
		else if (object instanceof ParameterOptimizationResult)
			result = this.unregister((ParameterOptimizationResult) object);
		else if (object instanceof Program)
			result = this.unregister((Program) object);
		else if (object instanceof ProgramConfig)
			result = this.unregister((ProgramConfig) object);
		else if (object instanceof RunAnalysisRun)
			result = this.unregister((RunAnalysisRun) object);
		else if (object instanceof RunAnalysisRunResult)
			result = this.unregister((RunAnalysisRunResult) object);
		else if (object instanceof RunDataAnalysisRun)
			result = this.unregister((RunDataAnalysisRun) object);
		else if (object instanceof RunDataAnalysisRunResult)
			result = this.unregister((RunDataAnalysisRunResult) object);
		else if (object instanceof StringProgramParameter)
			result = this.unregister((StringProgramParameter) object);
		else if (object instanceof Clustering)
			result = this.unregister((Clustering) object);
		else
			return false;

		if (result != -1)
			this.objectIds.remove(object);

		return result != -1;
	}

	// TODO
	protected boolean register(final Class<? extends RepositoryObject> c) {
		if (ClusteringQualityMeasure.class.isAssignableFrom(c))
			return this
					.registerClusteringQualityMeasureClass((Class<? extends ClusteringQualityMeasure>) c);
		else if (Context.class.isAssignableFrom(c))
			return this.registerContextClass((Class<? extends Context>) c);
		else if (DataSetFormat.class.isAssignableFrom(c))
			return this
					.registerDataSetFormatClass((Class<? extends DataSetFormat>) c);
		else if (DataSetType.class.isAssignableFrom(c))
			return this
					.registerDataSetTypeClass((Class<? extends DataSetType>) c);
		else if (DataStatistic.class.isAssignableFrom(c))
			return this
					.registerDataStatisticClass((Class<? extends DataStatistic>) c);
		else if (ParameterOptimizationMethod.class.isAssignableFrom(c))
			return this
					.registerParameterOptimizationMethodClass((Class<? extends ParameterOptimizationMethod>) c);
		else if (RunDataStatistic.class.isAssignableFrom(c))
			return this
					.registerRunDataStatisticClass((Class<? extends RunDataStatistic>) c);
		else if (RunResultFormat.class.isAssignableFrom(c))
			return this
					.registerRunResultFormatClass((Class<? extends RunResultFormat>) c);
		else if (RunStatistic.class.isAssignableFrom(c))
			return this
					.registerRunStatisticClass((Class<? extends RunStatistic>) c);
		return false;
	}

	// TODO
	protected boolean unregister(final Class<? extends RepositoryObject> c) {
		if (ClusteringQualityMeasure.class.isAssignableFrom(c))
			return this
					.unregisterClusteringQualityMeasureClass((Class<? extends ClusteringQualityMeasure>) c);
		else if (Context.class.isAssignableFrom(c))
			return this.unregisterContextClass((Class<? extends Context>) c);
		else if (DataSetFormat.class.isAssignableFrom(c))
			return this
					.unregisterDataSetFormatClass((Class<? extends DataSetFormat>) c);
		else if (DataSetType.class.isAssignableFrom(c))
			return this
					.unregisterDataSetTypeClass((Class<? extends DataSetType>) c);
		else if (DataStatistic.class.isAssignableFrom(c))
			return this
					.unregisterDataStatisticClass((Class<? extends DataStatistic>) c);
		else if (ParameterOptimizationMethod.class.isAssignableFrom(c))
			return this
					.unregisterParameterOptimizationMethodClass((Class<? extends ParameterOptimizationMethod>) c);
		else if (RunDataStatistic.class.isAssignableFrom(c))
			return this
					.unregisterRunDataStatisticClass((Class<? extends RunDataStatistic>) c);
		else if (RunResultFormat.class.isAssignableFrom(c))
			return this
					.unregisterRunResultFormat((Class<? extends RunResultFormat>) c);
		else if (RunStatistic.class.isAssignableFrom(c))
			return this
					.unregisterRunStatisticClass((Class<? extends RunStatistic>) c);
		return false;
	}

	protected abstract int register(final GoldStandardConfig object,
			final boolean updateOnly);

	protected abstract int register(final GoldStandard object,
			final boolean updateOnly);

	protected abstract int register(final DoubleProgramParameter object);

	protected abstract int register(final IntegerProgramParameter object);

	protected abstract int register(final StringProgramParameter object);

	protected abstract int register(final DataSet object,
			final boolean updateOnly);

	protected abstract int register(final DataConfig object,
			final boolean updateOnly);

	protected abstract int register(final DataSetConfig object,
			final boolean updateOnly);

	protected abstract int register(final Clustering object);

	protected abstract boolean unregisterRunResultFormat(
			final Class<? extends RunResultFormat> object);

	protected abstract int unregister(final ProgramConfig object);

	protected abstract int unregister(final ProgramParameter<?> programParameter);

	protected abstract int unregister(final Program object);

	protected abstract int unregister(final GoldStandardConfig object);

	protected abstract int unregister(final GoldStandard object);

	protected abstract int unregister(final Clustering object);

	protected abstract boolean unregisterDataSetFormatClass(
			final Class<? extends DataSetFormat> object);

	protected abstract boolean unregisterParameterOptimizationMethodClass(
			final Class<? extends ParameterOptimizationMethod> object);

	protected abstract boolean unregisterClusteringQualityMeasureClass(
			final Class<? extends ClusteringQualityMeasure> object);

	protected abstract boolean unregisterDataStatisticClass(
			final Class<? extends DataStatistic> object);

	protected abstract boolean unregisterRunStatisticClass(
			final Class<? extends RunStatistic> object);

	protected abstract boolean unregisterRunDataStatisticClass(
			final Class<? extends RunDataStatistic> object);

	protected abstract boolean unregisterDataSetTypeClass(
			final Class<? extends DataSetType> object);

	protected abstract int unregister(final DataSet object);

	protected abstract int unregister(final Run object);

	protected abstract int unregister(final RunResult object);

	protected abstract int unregister(final ParameterOptimizationResult object);

	protected abstract int unregister(final DataConfig object);

	protected abstract int unregister(final DataSetConfig object);

	protected abstract int getRunId(final Run run) throws SQLException;

	protected abstract int getClusteringId(final String name)
			throws SQLException;

	protected abstract int getClusterId(final int clusteringId,
			final String name) throws SQLException;

	protected abstract int getClusterObjectId(final int clusterId,
			final String name) throws SQLException;

	protected abstract int getDataSetFormatId(
			final String dataSetFormatClassSimpleName) throws SQLException;

	protected abstract int getParameterOptimizationMethodId(final String name)
			throws SQLException;

	protected abstract int getParameterSetId(final int runResultParamOptId)
			throws SQLException;

	protected abstract int getParameterSetParameterId(final int parameterSetId,
			final int parameterId) throws SQLException;

	protected abstract int getParameterSetParameterValuesId(
			final int parameterSetId, final int parameterId, final int iteration)
			throws SQLException;

	protected abstract int getProgramParameterTypeId(final String typeName)
			throws SQLException;

	protected abstract int getRunAnalysisId(final int runId)
			throws SQLException;

	protected abstract int getRunExecutionId(final int runId)
			throws SQLException;

	protected abstract int getRunResultExecutionId(final int runResultId)
			throws SQLException;

	protected abstract int getRunResultAnalysisId(final int runResultId)
			throws SQLException;

	protected abstract int getRunResultFormatId(
			final String runResultFormatSimpleName) throws SQLException;

	protected abstract int getRunResultId(final String uniqueRunIdentifier)
			throws SQLException;

	protected abstract int getRunResultRunAnalysisId(int runResultAnalysisId)
			throws SQLException;

	protected abstract int getRunTypeId(final String name) throws SQLException;

	protected abstract int getRepositoryId(String absPath) throws SQLException;

	protected abstract int getStatisticId(final String statisticsName)
			throws SQLException;

	/**
	 * @param run
	 *            The run which changed its status.
	 * @param runStatus
	 *            The new run status.
	 * @return True, if the status of the run was updated successfully.
	 */
	public abstract boolean updateStatusOfRun(final Run run,
			final String runStatus);

	/**
	 * Initializes the database: 1) establishes a connection 2) tells the
	 * database to delete this repository and all corresponding entries
	 * (cascading) and recreate a new and empty repository
	 */
	public void initDB() {
		try {
			// SQLException lastException = null;
			String password = getDBPassword();
			/**
			 * While we do not have a connection and the password is wrong,
			 * retry
			 */
			while (conn == null) {
				// first try or wrong password
				try {
					conn = DriverManager.getConnection(
					// "jdbc:mysql://"
					// + getServer()
					// + "/"
					// + getDatabase()
					// +
					// "?useServerPrepStmts=false&rewriteBatchedStatements=true",
							"jdbc:postgresql://" + getServer() + "/"
									+ getDatabase(), getDBUsername(), password);
					conn.setAutoCommit(false);
				} catch (SQLException e) {
					if (e instanceof CommunicationsException
							&& e.getCause() instanceof ConnectException) {
						this.repository.log
								.warn("Could not connect to the database server. Retrying in "
										+ Formatter.formatMsToDuration(5000));
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
						}
						continue;
					}
					switch (e.getErrorCode()) {
					// wrong password
						case 1045 :
							password = getDBPassword();
							break;
						default :
							throw e;
					}
				}
			}

			Logger log = LoggerFactory.getLogger(this.getClass());
			log.info("Initializing MySQL database");

			Statement stmt = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			/*
			 * Check if this repository is already in there. if yes, delete it.
			 */
			ResultSet rs = stmt.executeQuery("SELECT id FROM "
					+ this.getTableRepositories() + " WHERE base_path='"
					+ this.repository.getBasePath() + "';");
			if (rs.last() && rs.getRow() > 0) {
				int repository_id = rs.getInt("id");
				// delete data corresponding to runresult_repositories with this
				// parent repository
				rs = stmt.executeQuery("SELECT id FROM "
						+ this.getTableRepositories()
						+ " WHERE repository_id='" + repository_id + "';");
				List<String> ids = new ArrayList<String>();
				while (rs.next()) {
					int run_result_repository_id = rs.getInt("id");
					ids.add("" + run_result_repository_id);
				}
				deleteFromTable(this.getTableParameterSetParameterValues(),
						"repository_id", ids.toArray(new String[0]));
				deleteFromTable(this.getTableParameterOptimizationQualities(),
						"repository_id", ids.toArray(new String[0]));
				deleteFromTable(this.getTableParameterSetIterations(),
						"repository_id", ids.toArray(new String[0]));
				deleteFromTable(this.getTableParameterSetParameters(),
						"repository_id", ids.toArray(new String[0]));
				deleteFromTable(this.getTableParameterSets(), "repository_id",
						ids.toArray(new String[0]));
				// delete parent repository itself
				stmt.execute("DELETE FROM " + this.getTableRepositories()
						+ " WHERE base_path = '"
						+ this.repository.getBasePath() + "';");
			}

			String repositoryType = this.repository.getClass().getSimpleName();
			// Get repository_type_id
			rs = stmt.executeQuery("SELECT id FROM "
					+ this.getTableRepositoryTypes() + " WHERE name='"
					+ repositoryType + "';");
			rs.first();
			int repository_type_id = rs.getInt("id");

			try {
				// Get repositoryId
				stmt.execute("INSERT INTO " + this.getTableRepositories()
						+ " (base_path,repository_type_id) VALUES ('"
						+ this.repository.getBasePath() + "','"
						+ repository_type_id + "');");

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

	/**
	 * @param string
	 * @throws SQLException
	 */
	protected void deleteFromTable(String tableName, String columnName,
			String[] value) throws SQLException {
		// long start = System.currentTimeMillis();
		if (value.length == 0)
			return;
		Statement stmt2 = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		// try {
		// // stmt2.execute("CREATE TABLE " + tableName + "_new LIKE "
		// // + tableName + ";");
		// stmt2.execute("CREATE TABLE " + tableName + "_new AS "
		// + "SELECT * FROM " + tableName + " LIMIT 0;");
		// } catch (SQLException e) {
		// e.printStackTrace();
		// stmt2.execute("DROP TABLE " + tableName + "_new;");
		// stmt2.execute("CREATE TABLE " + tableName + "_new LIKE "
		// + tableName + ";");
		// }
		// // System.out.print(System.currentTimeMillis() - start + " ");
		// // start = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String s : value) {
			sb.append(s);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		// stmt2.execute("INSERT INTO " + tableName + "_new SELECT * FROM "
		// + tableName + " WHERE NOT " + columnName + " in "
		// + sb.toString() + ";");
		// // System.out.print(System.currentTimeMillis() - start + " ");
		// // start = System.currentTimeMillis();
		// stmt2.execute("ALTER TABLE " + tableName + " RENAME TO " + tableName
		// + "_old;");
		// // System.out.print(System.currentTimeMillis() - start + " ");
		// // start = System.currentTimeMillis();
		// stmt2.execute("ALTER TABLE " + tableName + "_new RENAME TO "
		// + tableName + ";");
		// // System.out.print(System.currentTimeMillis() - start + " ");
		// // start = System.currentTimeMillis();
		// stmt2.execute("DROP TABLE " + tableName + "_old;");
		// // System.out.println(System.currentTimeMillis() - start);

		stmt2.execute(String.format("DELETE FROM %s WHERE %s IN %s", tableName,
				columnName, sb.toString()));
	}

	/**
	 * 
	 */
	public void commitDB() {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected static String replaceNull(final String text, final String replace) {
		return (text == null ? replace : text);
	}

	/**
	 * @param object
	 * @return True, if the runresult was registered successfully.
	 */
	public int register(RunResult object) {
		if (object instanceof ExecutionRunResult) {
			if (object instanceof ClusteringRunResult) {
				return register((ClusteringRunResult) object);
			} else if (object instanceof ParameterOptimizationResult) {
				return register((ParameterOptimizationResult) object);
			}
		} else if (object instanceof AnalysisRunResult) {
			if (object instanceof RunDataAnalysisRunResult) {
				return register((RunDataAnalysisRunResult) object);
			} else if (object instanceof RunAnalysisRunResult) {
				return register((RunAnalysisRunResult) object);
			} else if (object instanceof DataAnalysisRunResult) {
				return register((DataAnalysisRunResult) object);
			}
		}
		return -1;
	}

	/**
	 * @param object
	 * @return True, if the object was registered successfully.
	 */
	public abstract boolean register(ExecutionRunResult object);

	/**
	 * @param object
	 * @return True, if the object was registered successfully.
	 */
	public abstract int register(ClusteringRunResult object);

	/**
	 * @param object
	 * @return True, if the object was registered successfully.
	 */
	public abstract int register(ParameterOptimizationResult object);

	/**
	 * @param object
	 * @return True, if the object was registered successfully.
	 */
	public abstract boolean register(AnalysisRunResult object);

	/**
	 * @param object
	 * @return True, if the object was registered successfully.
	 */
	public abstract int register(RunAnalysisRunResult object);

	/**
	 * @param object
	 * @return True, if the object was registered successfully.
	 */
	public abstract int register(RunDataAnalysisRunResult object);

	/**
	 * @param object
	 * @return True, if the object was registered successfully.
	 */
	public abstract int register(DataAnalysisRunResult object);

	protected abstract boolean registerDataSetFormatClass(
			Class<? extends DataSetFormat> object);

	protected abstract boolean registerParameterOptimizationMethodClass(
			Class<? extends ParameterOptimizationMethod> paramOptMethod);

	protected abstract boolean registerClusteringQualityMeasureClass(
			Class<? extends ClusteringQualityMeasure> clusteringQualityMeasure);

	protected abstract boolean registerDataStatisticClass(
			Class<? extends DataStatistic> dataStatistic);

	protected abstract boolean registerRunStatisticClass(
			Class<? extends RunStatistic> runStatistic);

	protected abstract boolean registerRunDataStatisticClass(
			Class<? extends RunDataStatistic> runDataStatistic);

	protected abstract boolean registerRunResultFormatClass(
			Class<? extends RunResultFormat> runResultFormat);

	protected abstract boolean registerDataSetTypeClass(
			Class<? extends DataSetType> object);

	protected abstract int getDataSetTypeId(
			final String dataSetTypeClassSimpleName) throws SQLException;

	protected abstract String getTableDataSetFormats();

	protected abstract boolean registerContextClass(
			Class<? extends Context> object);

	protected abstract boolean unregisterContextClass(
			final Class<? extends Context> object);

	protected int updateRepositoryId() {
		ResultSet rs;
		try {
			Statement stmt = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery("SELECT id FROM "
					+ this.getTableRepositories() + " WHERE base_path='"
					+ this.repository.getBasePath() + "';");

			rs.first();
			this.setRepositoryId(rs.getInt("id"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return repositoryId;
	}

	protected void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	protected abstract int getRepositoryTypeId(final String repositoryType)
			throws SQLException;

}
