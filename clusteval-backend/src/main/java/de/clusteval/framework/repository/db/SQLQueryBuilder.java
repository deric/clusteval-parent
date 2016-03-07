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
public abstract class SQLQueryBuilder {

	protected SQLConfig sqlConfig;

	/**
	 * @param sqlConfig
	 */
	public SQLQueryBuilder(final SQLConfig sqlConfig) {
		super();
		this.sqlConfig = sqlConfig;
	}

	/**
	 * @param config
	 * @return
	 */
	public abstract String getConnectionstring();

	protected abstract Statement createStatement(Connection conn)
			throws SQLException;

	protected abstract String insert(final String tableName,
			final String[] columnNames, final List<String[]> values);

	protected abstract String insert(final String tableName,
			final String[] columnNames, final String[] values);

	protected abstract String insertSelectWhereNotIn(final String tableName,
			final String selectTable, final String columnName,
			final String[] values);

	protected abstract String update(String tableName, String[] columnNames,
			String[] values, int rowId);

	protected abstract String delete(final String tableName,
			final String value, final String columnName);

	protected abstract String deleteWhereIn(final String tableName,
			final String[] value, final String columnName);

	protected abstract String delete(final String tableName,
			final String value[], final String columnName[]);

	protected abstract String select(String tableName, String columnName,
			String[] columnNames, String[] values);

	protected abstract String checkIfPresent(final String table,
			final String column, final String value);

	protected abstract String createTableLike(final String table,
			final String otherTable);

	protected abstract String renameTable(final String table,
			final String newTableName);

	protected abstract String dropTable(final String table);

	protected abstract String refreshMaterializedView(final String view);
}
