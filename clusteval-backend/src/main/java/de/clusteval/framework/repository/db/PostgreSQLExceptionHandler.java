/**
 * 
 */
package de.clusteval.framework.repository.db;

import java.sql.SQLException;

/**
 * @author Christian Wiwie
 *
 */
public class PostgreSQLExceptionHandler extends SQLExceptionHandler {

	/**
	 * @param conn
	 */
	public PostgreSQLExceptionHandler(SQLCommunicator sqlCommunicator) {
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
		try {
			if (SQLCommunicator.conn != null)
				SQLCommunicator.conn.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}
