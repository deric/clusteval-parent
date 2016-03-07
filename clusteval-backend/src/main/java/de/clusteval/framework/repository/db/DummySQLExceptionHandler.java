/**
 * 
 */
package de.clusteval.framework.repository.db;

import java.sql.SQLException;

/**
 * @author Christian Wiwie
 *
 */
public class DummySQLExceptionHandler extends SQLExceptionHandler {

	public DummySQLExceptionHandler() {
		super(null);
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
	}

}
