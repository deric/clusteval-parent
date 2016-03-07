/**
 * 
 */
package statistics;

/**
 * @author wiwie
 * 
 */
public class StudentsTParameters implements DistributionParameters {

	protected int df;
	protected int xmin;
	protected int shifting;
	protected double scaling;
	protected double ksDistance;

	public StudentsTParameters(int df, int xmin, int shifting, double scaling,
			double ksDistance) {
		super();
		this.df = df;
		this.xmin = xmin;
		this.shifting = shifting;
		this.scaling = scaling;
		this.ksDistance = ksDistance;
	}

	public int getDf() {
		return this.df;
	}

	public int getXMin() {
		return this.xmin;
	}

	public int getShifting() {
		return this.shifting;
	}

	public double getScaling() {
		return this.scaling;
	}

	public double getKsDistance() {
		return this.ksDistance;
	}

	public String toString() {
		return "[df=" + this.df + ",xmin=" + this.xmin + ",shifting="
				+ this.shifting + ",scaling=" + this.scaling + ",D="
				+ ksDistance + "]";
	}
}