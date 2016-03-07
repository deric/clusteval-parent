/**
 * 
 */
package statistics;

import java.util.Arrays;

/**
 * @author wiwie
 * 
 */
public class ShiftedScaledPowerLawParameters extends ShiftedPowerLawParameters {

	protected double scaling;

	/**
	 * @param alpha
	 * @param xmin
	 * @param xMinDistribution
	 * @param ksDistance
	 */
	public ShiftedScaledPowerLawParameters(double alpha, int xmin1, int xmin2,
			double scaling, double[] xMinDistribution, double ksDistance) {
		super(alpha, xmin1, xmin2, xMinDistribution, ksDistance);
		this.scaling = scaling;
	}

	public double getScaling() {
		return this.scaling;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see statistics.PowerLawParameters#toString()
	 */
	@Override
	public String toString() {
		return "[xmin=" + xmin + ",xMinDistr="
				+ Arrays.toString(this.xMinDistribution) + ",alpha=" + alpha
				+ ",shifting=" + this.xmin2 + ",scaling=" + this.scaling
				+ ",D=" + ksDistance + "]";
	}
}
