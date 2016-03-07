/**
 * 
 */
package statistics;

import java.util.Arrays;

/**
 * @author Christian Wiwie
 * 
 */
public class BinomialParameters implements DistributionParameters {

	protected int xmin;
	protected int n, shifting;
	protected double p;
	protected double[] xMinDistribution;
	protected double ksDistance;

	public BinomialParameters(int xmin,
			double[] xMinDistribution, double p, int n, int shifting, double ksDistance) {
		super();
		this.xmin = xmin;
		this.n = n;
		this.shifting = shifting;
		this.p = p;
		this.xMinDistribution = xMinDistribution;
		this.ksDistance = ksDistance;
	}

	public int getXMin() {
		return this.xmin;
	}

	public int getN() {
		return this.n;
	}

	public int getShifting() {
		return this.shifting;
	}

	public double getP() {
		return this.p;
	}

	public double[] getXMinDistribution() {
		return this.xMinDistribution;
	}

	public double getKsDistance() {
		return this.ksDistance;
	}

	public String toString() {
		return "[xmin=" + xmin + ",xMinDistribution="
				+ Arrays.toString(xMinDistribution) + ",p=" + p
				+ ",n=" + n + ",shifting=" + shifting + ",D=" + ksDistance + "]";
	}
}
