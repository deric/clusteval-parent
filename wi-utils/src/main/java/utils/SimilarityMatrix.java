/**
 *
 */
package utils;

import cern.colt.function.tdouble.DoubleProcedure;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
public class SimilarityMatrix {

    public enum NUMBER_PRECISION {
        DOUBLE, FLOAT, SHORT
    }

    protected Map<String, Integer> ids;

    protected AbstractSimilarityMatrix similarities;

    protected SparseDoubleMatrix2D sparseSimilarities;

    protected double minSimilarity = Double.MAX_VALUE;

    protected double maxSimilarity = Double.MIN_VALUE;

    protected double similaritySum;

    /**
     * @param rows
     * @param columns
     */
    public SimilarityMatrix(final int rows, final int columns) {
        this(null, rows, columns, NUMBER_PRECISION.DOUBLE);
    }

    public SimilarityMatrix(final int rows, final int columns,
            final NUMBER_PRECISION precision) {
        this(null, rows, columns, precision);
    }

    public SimilarityMatrix(final int rows, final int columns,
            final NUMBER_PRECISION precision, final boolean isSymmetric) {
        this(null, rows, columns, precision, isSymmetric);
    }

    public SimilarityMatrix(final String[] ids, final int rows,
            final int columns, final NUMBER_PRECISION precision) {
        this(ids, rows, columns, precision, false);
    }

    /**
     * @param ids
     * @param rows
     * @param columns
     * @param precision
     * @param isSymmetric
     */
    public SimilarityMatrix(final String[] ids, final int rows,
            final int columns, final NUMBER_PRECISION precision,
            final boolean isSymmetric) {
        super();

        if (ids != null) {
            this.ids = new HashMap<>();
            for (String id : ids) {
                this.ids.put(id, this.ids.size());
            }
        }

        try {
            switch (precision) {
                case DOUBLE:
                    this.similarities = new SimilarityMatrixDouble(rows, columns);
                    break;
                case FLOAT:
                    this.similarities = new SimilarityMatrixFloat(rows, columns);
                    break;
                case SHORT:
                    this.similarities = new SimilarityMatrixInt(rows, columns);
                    break;
                default:
                    break;
            }
        } catch (OutOfMemoryError e) {
            // in this case we try to parse the file into a sparse matrix data
            // structure
            this.sparseSimilarities = new SparseDoubleMatrix2D(rows, columns);
        }
    }

    /**
     * @param similarities
     */
    public SimilarityMatrix(final double[][] similarities) {
        this(similarities, NUMBER_PRECISION.DOUBLE);
    }

    public SimilarityMatrix(final double[][] similarities,
            final NUMBER_PRECISION precision) {
        this(null, similarities, precision);
    }

    public SimilarityMatrix(final String[] ids, final double[][] similarities) {
        this(ids, similarities, NUMBER_PRECISION.DOUBLE);
    }

    /**
     * @param ids
     * @param similarities
     * @param precision
     */
    public SimilarityMatrix(final String[] ids, final double[][] similarities,
            final NUMBER_PRECISION precision) {
        super();

        if (ids != null) {
            this.ids = new HashMap<>();
            for (String id : ids) {
                this.ids.put(id, this.ids.size());
            }
        }
        try {
            // TODO
            switch (precision) {
                case DOUBLE:
                    this.similarities = new SimilarityMatrixDouble(similarities.length,
                            similarities[0].length);
                    break;
                case FLOAT:
                    this.similarities = new SimilarityMatrixFloat(similarities.length,
                            similarities[0].length);
                    break;
                case SHORT:
                    this.similarities = new SimilarityMatrixInt(similarities.length,
                            similarities[0].length);
                    break;
                default:
                    break;
            }
        } catch (OutOfMemoryError e) {
            // in this case we try to parse the file into a sparse matrix data
            // structure
            this.sparseSimilarities = new SparseDoubleMatrix2D(
                    similarities.length, similarities[0].length);
        }
        for (int i = 0; i < similarities.length; i++) {
            for (int j = 0; j < similarities[i].length; j++) {
                this.setSimilarity(i, j, similarities[i][j]);
            }
        }
    }

    protected void updateSimilarityStatistics() {
        this.maxSimilarity = Double.MIN_VALUE;
        this.minSimilarity = Double.MAX_VALUE;
        this.similaritySum = 0.0;

        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getColumns(); j++) {
                double val = this.getSimilarity(i, j);
                if (val > this.maxSimilarity) {
                    this.maxSimilarity = val;
                }
                if (val < this.minSimilarity) {
                    this.minSimilarity = val;
                }
                this.similaritySum += val;
            }
        }
    }

    protected void updateSimilarityStatistics(final double newSimilarity) {
        if (newSimilarity > this.maxSimilarity) {
            this.maxSimilarity = newSimilarity;
        }
        if (newSimilarity < this.minSimilarity) {
            this.minSimilarity = newSimilarity;
        }
        this.similaritySum += newSimilarity;
    }

    /**
     * @param id1
     * @param id2
     * @return
     */
    public double getSimilarity(final int id1, final int id2) {
        if (this.similarities != null) {
            return similarities.get(id1, id2);
        } else {
            return this.sparseSimilarities.get(id1, id2);
        }
    }

    /**
     * @param id1
     * @param id2
     * @return
     */
    public double getSimilarity(final String id1, final String id2) {
        if (this.ids == null) {
            throw new IllegalArgumentException(
                    "No ids were set for this matrix!");
        }
        return this.getSimilarity(this.ids.get(id1), this.ids.get(id2));
    }

    /**
     * @param id1
     * @param id2
     * @param similarity
     */
    public void setSimilarity(final int id1, final int id2,
            final double similarity) {
        if (this.similarities != null) {
            this.similarities.set(id1, id2, similarity);
        } else {
            this.sparseSimilarities.set(id1, id2, similarity);
        }

        this.updateSimilarityStatistics(similarity);
    }

    /**
     * @param id1
     * @param id2
     * @param similarity
     */
    public void setSimilarity(final String id1, final String id2,
            final double similarity) {
        if (this.ids == null) {
            throw new IllegalArgumentException(
                    "No ids were set for this matrix!");
        }
        this.setSimilarity(this.ids.get(id1), this.ids.get(id2), similarity);
    }

    /**
     * @return
     */
    public int getRows() {
        return this.similarities != null
               ? this.similarities.getRows()
               : this.sparseSimilarities.rows();
    }

    /**
     * @return
     */
    public int getColumns() {
        return this.similarities != null
               ? this.similarities.getRows()
               : this.sparseSimilarities.rows();
    }

    /**
     * @return
     */
    public double getMaxValue() {
        return this.maxSimilarity;
    }

    /**
     * @return
     */
    public double getMinValue() {
        return this.minSimilarity;
    }

    /**
     * @return
     */
    public double getMean() {
        return this.similaritySum / (this.getRows() * this.getColumns());
    }

    public void invert() {
        double oldMin = this.getMinValue();
        double sum = this.similaritySum;

        double maxVal = this.getMaxValue();
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < (this.similarities != null
                    && this.similarities.isSymmetric() ? i + 1 : getColumns()); j++) {
                double old = this.getSimilarity(i, j);
                this.setSimilarity(i, j, maxVal - old);
            }
        }

        this.minSimilarity = 0.0;
        this.maxSimilarity = maxVal - oldMin;
        this.similaritySum = maxVal * this.getColumns() * this.getRows() - sum;
    }

    public void normalize() {
        this.subtract(this.getMinValue());
        this.scaleBy(this.getMaxValue());
    }

    public void subtract(double x) {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                double old = this.getSimilarity(i, j);
                this.setSimilarity(i, j, old - x);
            }
        }
    }

    public void scaleBy(double factor) {
        this.scaleBy(factor, true);
    }

    public void scaleBy(double factor, boolean down) {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getColumns(); j++) {
                double old = this.getSimilarity(i, j);
                this.setSimilarity(i, j, down ? (old / factor) : (old * factor));
            }
        }
    }

    /**
     * @param array
     */
    public void setIds(String[] array) {
        this.ids = new HashMap<String, Integer>();
        for (int pos = 0; pos < array.length; pos++) {
            String id = array[pos];
            this.ids.put(id, pos);
        }
    }

    /**
     * @return
     */
    public Map<String, Integer> getIds() {
        return this.ids;
    }

    public String[] getIdsArray() {
        String[] result = new String[this.ids.size()];
        for (Map.Entry<String, Integer> e : this.ids.entrySet()) {
            result[e.getValue()] = e.getKey();
        }
        return result;
    }

    /**
     * @param row
     * @param column
     * @return
     */
    public boolean isSparse(int row, int column) {
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SimilarityMatrix)) {
            return false;
        }

        SimilarityMatrix other = (SimilarityMatrix) obj;

        return ((this.ids == null && other.ids == null) || this.ids
                .equals(other.ids))
                && ((this.similarities == null && other.similarities == null || this.similarities
                .equals(other.similarities)))
                && ((this.sparseSimilarities == null
                && other.sparseSimilarities == null || this.sparseSimilarities
                .equals(other.sparseSimilarities)));
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (this.ids + "" + this.similarities != null
                ? this.similarities
                : this.sparseSimilarities).hashCode();
    }

    /**
     * @param numberBuckets
     * @return
     *
     */
    public Map<Double, Integer> toDistribution(int numberBuckets) {

        DistributionBuilder builder = new DistributionBuilder(this,
                numberBuckets);

        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getColumns(); j++) {
                builder.apply(this.getSimilarity(i, j));
            }
        }

        return builder.getResult();
    }

    /**
     * @return
     */
    public double[][] toArray() {
        double[][] result = new double[this.getRows()][this.getColumns()];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = this.getSimilarity(i, j);
            }
        }

        return result;
    }

    /**
     * @return
     */
    public List<Double> toOrderedList() {
        List<Double> distr = new ArrayList<Double>();
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getColumns(); j++) {
                distr.add(this.getSimilarity(i, j));
            }
        }
        Collections.sort(distr);
        return distr;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see java.lang.Object#toString()
    // */
    // @Override
    // public String toString() {
    // StringBuilder sb = new StringBuilder();
    //
    // for (int i = 0; i < getRows(); i++) {
    // for (int j = 0; j < getColumns(); j++) {
    // sb.append(this.getSimilarity(i, j));
    // sb.append("\t");
    // }
    // sb.append(System.getProperty("line.separator"));
    // }
    //
    // return sb.toString();
    // }
    /**
     * @param numberOfQuantiles
     * @return
     * @throws RangeCreationException
     */
    public double[] getQuantiles(final int numberOfQuantiles)
            throws RangeCreationException {
        List<Double> orderedList = toOrderedList();
        int[] range = ArraysExt.range(0, orderedList.size() - 1,
                numberOfQuantiles, true);
        double[] result = new double[range.length];
        for (int i = 0; i < range.length; i++) {
            result[i] = orderedList.get(range[i]);
        }
        return result;
    }

    /**
     * @param numberBuckets
     * @return
     */
    public Pair<double[], int[]> toDistributionArray(int numberBuckets) {

        DistributionBuilder builder = new DistributionBuilder(this,
                numberBuckets);

        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getColumns(); j++) {
                builder.apply(this.getSimilarity(i, j));
            }
        }

        return builder.getResultAsArray();
    }

    /**
     * @param numberBuckets
     * @param classIds
     * @return
     */
    // TODO: added 25th 07 2012
    public Pair<double[], int[][]> toClassDistributionArray(int numberBuckets,
            String[][] classIds) {

        ClassDistributionBuilder builder = new ClassDistributionBuilder(this,
                numberBuckets, classIds);

        builder.fillArray(this);

        return builder.getResultAsArray();
    }

    /**
     * @param numberBuckets
     * @param idToClass
     * @return
     */
    // TODO: added 25th 07 2012
    public Pair<double[], int[][]> toIntraInterDistributionArray(
            int numberBuckets, Map<String, Integer> idToClass) {

        IntraInterDistributionBuilder builder = new IntraInterDistributionBuilder(
                this, numberBuckets, idToClass);

        builder.fillArray(this);

        return builder.getResultAsArray();
    }
}

class DistributionBuilder implements DoubleProcedure {

    protected HashMap<Double, Integer> result;
    protected int[] resultArray;
    protected double[] range;

    public DistributionBuilder(final SimilarityMatrix matrix, int numberBuckets) {
        super();

        this.result = new HashMap<Double, Integer>();

        double minValue = matrix.getMinValue();
        double maxValue = matrix.getMaxValue();

        this.range = ArraysExt.range(minValue, maxValue, numberBuckets, false);

        this.resultArray = new int[this.range.length];

        for (double d : range) {
            result.put(d, 0);
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see cern.colt.function.tdouble.DoubleFunction#apply(double)
     */
    @Override
    public boolean apply(double argument) {
        /*
		 * Do binary search for bucket
         */
        int lower = 0;
        int upper = this.range.length - 1;

        while (lower < upper) {
            int middle = lower + (int) Math.round((upper - lower) / 2.0);

            if (lower == upper - 1) {
                if (argument < this.range[upper]) {
                    upper = lower;
                } else {
                    lower = upper;
                }
            } else if (argument < this.range[middle]) {
                upper = middle;
            } else {
                lower = middle;
            }
        }

        this.result.put(this.range[lower],
                this.result.get(this.range[lower]) + 1);
        this.resultArray[lower]++;
        return true;
    }

    public HashMap<Double, Integer> getResult() {
        return this.result;
    }

    /**
     * @return
     */
    public Pair<double[], int[]> getResultAsArray() {
        return Pair.getPair(this.range, this.resultArray);
    }
}

class ClassDistributionBuilder {

    protected List<Map<Double, Integer>> result;
    protected int[][] resultArray;
    protected double[] range;
    protected String[][] classIds;

    public ClassDistributionBuilder(final SimilarityMatrix matrix,
            int numberBuckets, String[][] classIds) {
        super();

        this.classIds = classIds;

        this.result = new ArrayList<Map<Double, Integer>>();

        double minValue = matrix.getMinValue();
        double maxValue = matrix.getMaxValue();

        this.range = ArraysExt.range(minValue, maxValue, numberBuckets, true);

        this.resultArray = new int[classIds.length][this.range.length];

        for (int c = 0; c < classIds.length; c++) {
            Map<Double, Integer> tmp = new HashMap<Double, Integer>();
            for (double d : range) {
                tmp.put(d, 0);
            }
            result.add(tmp);
        }
    }

    public boolean fillArray(SimilarityMatrix matrix) {
        for (int c = 0; c < this.classIds.length; c++) {
            for (int x = 0; x < this.classIds[c].length; x++) {
                for (int y = 0; y < this.classIds[c].length; y++) {
                    double argument = matrix.getSimilarity(this.classIds[c][x],
                            this.classIds[c][y]);

                    /*
					 * Do binary search for bucket
                     */
                    int lower = 0;
                    int upper = this.range.length - 1;

                    while (lower < upper) {
                        int middle = lower
                                + (int) Math.round((upper - lower) / 2.0);

                        if (lower == upper - 1) {
                            if (argument < this.range[upper]) {
                                upper = lower;
                            } else {
                                lower = upper;
                            }
                        } else if (argument < this.range[middle]) {
                            upper = middle;
                        } else {
                            lower = middle;
                        }
                    }

                    Map<Double, Integer> tmp = this.result.get(c);
                    tmp.put(this.range[lower], tmp.get(this.range[lower]) + 1);
                    this.resultArray[c][lower]++;
                }
            }
        }

        return true;
    }

    public List<Map<Double, Integer>> getResult() {
        return this.result;
    }

    /**
     * @return
     */
    public Pair<double[], int[][]> getResultAsArray() {
        return Pair.getPair(this.range, this.resultArray);
    }
}

class IntraInterDistributionBuilder {

    protected List<Map<Double, Integer>> result;
    protected int[][] resultArray;
    protected double[] range;
    protected Map<String, Integer> idToClass;

    public IntraInterDistributionBuilder(final SimilarityMatrix matrix,
            int numberBuckets, Map<String, Integer> idToClass) {
        super();

        this.idToClass = idToClass;

        this.result = new ArrayList<Map<Double, Integer>>();

        double minValue = matrix.getMinValue();
        double maxValue = matrix.getMaxValue();

        this.range = ArraysExt.range(minValue, maxValue, numberBuckets, true);

        this.resultArray = new int[2][this.range.length];

        /*
		 * Add one for intra, and one for inter distribution
         */
        for (int c = 0; c < this.resultArray.length; c++) {
            Map<Double, Integer> tmp = new HashMap<Double, Integer>();
            for (double d : range) {
                tmp.put(d, 0);
            }
            result.add(tmp);
        }
    }

    public boolean fillArray(SimilarityMatrix matrix) {
        Set<String> ids = matrix.getIds().keySet();

        Iterator<String> idIterator = ids.iterator();
        while (idIterator.hasNext()) {
            String id1 = idIterator.next();
            Iterator<String> idIterator2 = ids.iterator();
            while (idIterator2.hasNext()) {
                String id2 = idIterator2.next();
                double argument = matrix.getSimilarity(id1, id2);

                /*
				 * Do binary search for bucket
                 */
                int lower = 0;
                int upper = this.range.length - 1;

                while (lower < upper) {
                    int middle = lower
                            + (int) Math.round((upper - lower) / 2.0);

                    if (lower == upper - 1) {
                        if (argument < this.range[upper]) {
                            upper = lower;
                        } else {
                            lower = upper;
                        }
                    } else if (argument < this.range[middle]) {
                        upper = middle;
                    } else {
                        lower = middle;
                    }
                }

                Map<Double, Integer> tmp;
                if (this.idToClass.get(id1).equals(this.idToClass.get(id2))) {
                    tmp = this.result.get(0);
                    this.resultArray[0][lower]++;
                } else {
                    tmp = this.result.get(1);
                    this.resultArray[1][lower]++;
                }

                tmp.put(this.range[lower], tmp.get(this.range[lower]) + 1);
            }
        }

        return true;
    }

    public List<Map<Double, Integer>> getResult() {
        return this.result;
    }

    /**
     * @return
     */
    public Pair<double[], int[][]> getResultAsArray() {
        return Pair.getPair(this.range, this.resultArray);
    }

    class TwoDimensionalArray {

        protected double[][] array;

        public TwoDimensionalArray() {
            super();
        }

    }
}
