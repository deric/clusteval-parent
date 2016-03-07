/**
 * 
 */
package de.clusteval.framework.repository;

import org.rosuda.REngine.REngineException;


/**
 * @author Christian Wiwie
 *
 */
public class RException extends REngineException {
	
	/**
	 * 
	 */
	public RException(final MyRengine rEngine, final String message) {
		super(rEngine.connection, message);
	}
}
