/**
 * 
 */
package de.clusteval.framework.repository.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
public class PostgreSQLQueryBuilder extends SQLQueryBuilder {

	/**
	 * @param sqlConfig
	 * 
	 */
	public PostgreSQLQueryBuilder(final SQLConfig sqlConfig) {
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
		return String
				.format("jdbc:postgresql://%s/%s?useServerPrepStmts=false&rewriteBatchedStatements=true",
						sqlConfig.getHost(), sqlConfig.getDatabase());
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
		return String.format("SELECT id FROM %s WHERE base_path='%s';", table,
				column, value);
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
		return String.format("CREATE TABLE %s (LIKE %s);", table, otherTable);
	}

	@Override
	protected String delete(final String tableName, final String value,
			final String columnName) {
		StringBuilder sb = new StringBuilder();

		sb.append("DELETE FROM ");
		sb.append(tableName);
		sb.append(" WHERE ");
		sb.append(columnName);
		sb.append("='");
		sb.append(value);
		sb.append("';");

		return sb.toString();
	}

	@Override
	protected String deleteWhereIn(final String tableName,
			final String[] value, final String columnName) {
		StringBuilder sb = new StringBuilder();

		sb.append("DELETE FROM ");
		sb.append(tableName);
		sb.append(" WHERE ");
		sb.append(columnName);
		sb.append(" IN (");
		for (String s : value) {
			sb.append("'");
			sb.append(s);
			sb.append("'");
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(");");

		return sb.toString();
	}

	@Override
	protected String delete(final String tableName, final String value[],
			final String columnName[]) {
		StringBuilder sb = new StringBuilder();

		sb.append("DELETE FROM ");
		sb.append(tableName);
		sb.append(" WHERE ");
		for (int i = 0; i < value.length; i++) {
			sb.append(columnName[i]);
			sb.append("='");
			sb.append(value[i]);
			sb.append("' ");
			if (i < value.length - 1)
				sb.append("AND ");
		}
		sb.append(";");

		return sb.toString();
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
		return String.format("DROP TABLE %s;", table);
	}

	@Override
	protected String insert(final String tableName, final String[] columnNames,
			final List<String[]> values) {

		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		sb.append(tableName);
		sb.append(" (");
		for (String s : columnNames) {
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

		return sb.toString();
	}

	@Override
	protected String insert(final String tableName, final String[] columnNames,
			final String[] values) {

		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		sb.append(tableName);
		sb.append(" (");
		for (String s : columnNames) {
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

		return sb.toString();
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
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String s : values) {
			sb.append(s);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return String.format(
				"INSERT INTO %s SELECT * FROM %s WHERE NOT %s in %s;",
				tableName, selectTable, columnName, sb.toString());
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
		return String.format("ALTER TABLE %s RENAME TO %s;", table,
				newTableName);
	}

	@Override
	protected String select(final String tableName, final String columnName,
			final String[] columnNames, final String[] values) {

		StringBuilder sb = new StringBuilder();

		sb.append("SELECT " + columnName + " FROM ");
		sb.append(tableName);
		sb.append(" WHERE ");
		for (int c = 0; c < columnNames.length; c++) {
			String s = columnNames[c];
			String v = values[c];
			sb.append(s);
			sb.append("='");
			sb.append(v);
			sb.append("'");
			if (c < columnNames.length - 1)
				sb.append(" AND ");
		}
		sb.append(";");

		return sb.toString();
	}

	@Override
	protected String update(final String tableName, final String[] columnNames,
			final String[] values, final int rowId) {

		StringBuilder sb = new StringBuilder();

		sb.append("UPDATE ");
		sb.append(tableName);
		sb.append(" SET ");
		for (int i = 0; i < columnNames.length; i++) {
			String s = columnNames[i];
			String v = values[i];

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

		return sb.toString();
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
		return conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
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
		return String.format("REFRESH MATERIALIZED VIEW %s;", view);
	}
}
