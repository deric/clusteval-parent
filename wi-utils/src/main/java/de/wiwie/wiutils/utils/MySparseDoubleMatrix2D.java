/**
 * 
 */
package de.wiwie.wiutils.utils;

import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

/**
 * @author Christian Wiwie
 * 
 */
public class MySparseDoubleMatrix2D extends SparseDoubleMatrix2D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 233743232077965868L;

	// private List<Long> nonSparseEntries;
	private SparseDoubleMatrix2D nonSparseEntries;

	/**
	 * @param rows
	 * @param columns
	 */
	public MySparseDoubleMatrix2D(int rows, int columns) {
		super(rows, columns);
		this.nonSparseEntries = new SparseDoubleMatrix2D(rows, columns);
	}

	/**
	 * @param values
	 */
	public MySparseDoubleMatrix2D(double[][] values) {
		this(values.length, values.length == 0 ? 0 : values[0].length);
		assign(values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cern.colt.matrix.tdouble.DoubleMatrix2D#set(int, int, double)
	 */
	@Override
	public void set(int row, int column, double value) {
		super.set(row, column, value);
		this.nonSparseEntries.set(row, column, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D#setQuick(int,
	 * int, double)
	 */
	@Override
	public synchronized void setQuick(int row, int column, double value) {
		super.setQuick(row, column, value);
		this.nonSparseEntries.set(row, column, 1);
	}

	/**
	 * @param row
	 * @param column
	 * @return
	 */
	public boolean isSparse(int row, int column) {
		return this.nonSparseEntries.get(row, column) == 0.0;
	}
}
