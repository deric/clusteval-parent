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
public class MySQLExceptionHandler extends SQLExceptionHandler {

	/**
	 * @param conn
	 */
	public MySQLExceptionHandler(SQLCommunicator sqlCommunicator) {
		super(sqlCommunicator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.db.SQLExceptionHandler#handleException
	 * (java.sql.SQLException)
	 */
	@Override
	public void handleException(SQLException e) {
		// we do not need to do anything
	}

}
