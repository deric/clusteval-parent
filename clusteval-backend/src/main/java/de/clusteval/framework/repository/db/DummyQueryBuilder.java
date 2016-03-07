/**
 * 
 */
package de.clusteval.framework.repository.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
public class DummyQueryBuilder extends SQLQueryBuilder {

	/**
	 * @param sqlConfig
	 * 
	 */
	public DummyQueryBuilder(final SQLConfig sqlConfig) {
		super(sqlConfig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#getConnectionstring
	 * ()
	 */
	@Override
	public String getConnectionstring() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#checkIfPresent(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected String checkIfPresent(String table, String column, String value) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#createTableLike(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	protected String createTableLike(String table, String otherTable) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#delete(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	protected String delete(String tableName, String value, String columnName) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#delete(java.lang
	 * .String, java.lang.String[], java.lang.String[])
	 */
	@Override
	protected String delete(String tableName, String[] value,
			String[] columnName) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#dropTable(java.lang
	 * .String)
	 */
	@Override
	protected String dropTable(String table) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#insert(java.lang
	 * .String, java.lang.String[], java.util.List)
	 */
	@Override
	protected String insert(String tableName, String[] columnNames,
			List<String[]> values) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#insert(java.lang
	 * .String, java.lang.String[], java.lang.String[])
	 */
	@Override
	protected String insert(String tableName, String[] columnNames,
			String[] values) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#insertSelectWhereNotIn
	 * (java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	protected String insertSelectWhereNotIn(String tableName,
			String selectTable, String columnName, String[] values) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#renameTable(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	protected String renameTable(String table, String newTableName) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#select(java.lang
	 * .String, java.lang.String, java.lang.String[], java.lang.String[])
	 */
	@Override
	protected String select(String tableName, String columnName,
			String[] columnNames, String[] values) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#update(java.lang
	 * .String, java.lang.String[], java.lang.String[], int)
	 */
	@Override
	protected String update(String tableName, String[] columnNames,
			String[] values, int rowId) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#createStatement(
	 * java.sql.Connection)
	 */
	@Override
	protected Statement createStatement(Connection conn) throws SQLException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#deleteWhereIn(java
	 * .lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	protected String deleteWhereIn(String tableName, String[] value,
			String columnName) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLQueryBuilder#refreshMaterializedView
	 * (java.lang.String)
	 */
	@Override
	protected String refreshMaterializedView(String view) {
		return null;
	}
}
