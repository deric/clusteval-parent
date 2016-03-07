/**
 * 
 */
package de.clusteval.framework.repository.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Christian Wiwie
 *
 */
public abstract class SQLExceptionHandler {

	protected SQLCommunicator sqlCommunicator;

	public SQLExceptionHandler(final SQLCommunicator sqlCommunicator) {
		super();

		this.sqlCommunicator = sqlCommunicator;
	}

	public abstract void handleException(final SQLException e);
}
