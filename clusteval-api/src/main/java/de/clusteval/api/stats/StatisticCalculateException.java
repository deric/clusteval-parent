/**
 *
 */
package de.clusteval.api.stats;

import de.clusteval.api.exceptions.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class StatisticCalculateException extends ClustEvalException {

    /**
     * @param message
     */
    public StatisticCalculateException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public StatisticCalculateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public StatisticCalculateException(Throwable cause) {
        super(cause);
    }

}