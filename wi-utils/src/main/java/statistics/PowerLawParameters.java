/**
 * 
 */
package statistics;

import java.util.Arrays;

public class PowerLawParameters implements DistributionParameters {

	protected double alpha;
	protected int xmin;
	protected double[] xMinDistribution;
	protected double ksDistance;

	public PowerLawParameters(double alpha, int xmin,
			double[] xMinDistribution, double ksDistance) {
		super();
		this.alpha = alpha;
		this.xmin = xmin;
		this.xMinDistribution = xMinDistribution;
		this.ksDistance = ksDistance;
	}

	public double getAlpha() {
		return this.alpha;
	}

	public int getXMin() {
		return this.xmin;
	}

	public double[] getXMinDistribution() {
		return this.xMinDistribution;
	}

	public double getKsDistance() {
		return this.ksDistance;
	}

	public String toString() {
		return "[xmin=" + xmin + ",xMinDistr="
				+ Arrays.toString(this.xMinDistribution) + ",alpha=" + alpha
				+ ",D=" + ksDistance + "]";
	}
}