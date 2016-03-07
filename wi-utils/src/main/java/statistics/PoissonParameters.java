/**
 * 
 */
package statistics;

import java.util.Arrays;

/**
 * @author Christian Wiwie
 * 
 */
public class PoissonParameters implements DistributionParameters {

	protected double lambda;
	protected int xmin;
	protected double[] xMinDistribution;
	protected double ksDistance;

	public PoissonParameters(double lambda, int xmin,
			double[] xMinDistribution, double ksDistance) {
		super();
		this.lambda = lambda;
		this.xmin = xmin;
		this.xMinDistribution = xMinDistribution;
		this.ksDistance = ksDistance;
	}

	public double getLambda() {
		return this.lambda;
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
		return "[xmin=" + xmin + ",xMinDistribution="
				+ Arrays.toString(xMinDistribution) + ",lambda=" + lambda
				+ ",D=" + ksDistance + "]";
	}
}
