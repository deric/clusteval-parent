package de.wiwie.wiutils.utils;

import java.io.Serializable;

/**
 * @author Christian Wiwie
 *
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 988759581464896997L;
	protected T e1;
	protected U e2;

	/**
	 * @param el1
	 * @param el2
	 */
	public Pair(final T el1, final U el2) {
		this.e1 = el1;
		this.e2 = el2;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object p) {
		if (p instanceof Pair) {
			final Pair<T, U> other = (Pair<T, U>) p;
			return other.e1.equals(this.e1) && other.e2.equals(this.e2);
		}
		return false;
	}

	public T getFirst() {
		return this.e1;
	}

	public void setFirst(final T newFirst) {
		this.e1 = newFirst;
	}

	public U getSecond() {
		return this.e2;
	}

	public void setSecond(final U newSecond) {
		this.e2 = newSecond;
	}

	@Override
	public int hashCode() {
		return this.e1.hashCode() + this.e2.hashCode();
	}

	@Override
	public String toString() {
		return "(" + this.e1.toString() + "\t" + this.e2.toString() + ")";
	}

	/**
	 * @param first
	 * @param second
	 * @return
	 */
	public static <A, B> Pair<A, B> getPair(A first, B second) {
		return new Pair<A, B>(first, second);
	}
}