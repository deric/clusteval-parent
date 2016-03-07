/**
 * 
 */
package de.wiwie.wiutils.statistics;

/**
 * @author wiwie
 * 
 */
public class CDF {

	protected double[] cdf;
	protected int minValue;

	public CDF(Distribution d) {
		super();
		minValue = d.minValue;

		this.cdf = new double[d.distr.length];
		double sum = 0.0;
		for (int i = 0; i < this.cdf.length; i++) {
			sum += d.distr[i];
			this.cdf[i] = sum;
		}
	}

	public double get(int x) {
		return cdf[x - minValue];
	}

	public int getMaxValue() {
		return this.cdf.length - 1 - minValue;
	}
}
