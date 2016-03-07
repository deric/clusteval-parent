/**
 * 
 */
package de.wiwie.wiutils.statistics;

import java.util.Arrays;


/**
 * @author wiwie
 *
 */
public class ShiftedPowerLawParameters extends PowerLawParameters {

	protected int xmin2;
	
	/**
	 * @param alpha
	 * @param xmin
	 * @param xMinDistribution
	 * @param ksDistance
	 */
	public ShiftedPowerLawParameters(double alpha, int xmin1, int xmin2,
			double[] xMinDistribution, double ksDistance) {
		super(alpha, xmin1, xMinDistribution, ksDistance);
		this.xmin2 = xmin2;
	}

	public int getShifting() {
		return this.xmin2;
	}
	
	/* (non-Javadoc)
	 * @see statistics.PowerLawParameters#toString()
	 */
	@Override
	public String toString() {
		return "[xmin=" + xmin + ",xMinDistr="
				+ Arrays.toString(this.xMinDistribution) + ",alpha=" + alpha
				+ ",shifting=" + this.xmin2 + ",D=" + ksDistance + "]";
	}
}
