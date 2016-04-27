/**
 * 
 */
package de.wiwie.wiutils.statistics;

import de.clusteval.utils.ArraysExt;

/**
 * @author wiwie
 * 
 */
public class Distribution {

	protected double[] distr;
	protected int minValue;

	public Distribution(int[] values) {
		this(values, 0);
	}

	public Distribution(int[] values, int minValue) {
		super();
		this.minValue = minValue;
		this.distr = new double[(ArraysExt.max(values) - minValue) + 1];
		for (int val : values) {
			this.distr[val - minValue]++;
		}

		this.distr = ArraysExt.scaleBy(this.distr, ArraysExt.sum(this.distr));
	}

	public double get(int x) {
		return distr[x - minValue];
	}
}
