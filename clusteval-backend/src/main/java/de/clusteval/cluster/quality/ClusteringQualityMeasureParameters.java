/**
 * 
 */
package de.clusteval.cluster.quality;

import java.util.HashMap;

/**
 * @author Christian Wiwie
 *
 */
public class ClusteringQualityMeasureParameters extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6949276396401908242L;

	/**
	 * 
	 */
	public ClusteringQualityMeasureParameters() {
		super();
	}

	/**
	 * @param other
	 */
	public ClusteringQualityMeasureParameters(
			final ClusteringQualityMeasureParameters other) {
		super();
		this.putAll(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#clone()
	 */
	@Override
	public ClusteringQualityMeasureParameters clone() {
		return new ClusteringQualityMeasureParameters(this);
	}

}
