/**
 * 
 */
package de.wiwie.wiutils.utils;

import java.util.Iterator;

/**
 * @author Christian Wiwie
 * @param <T>
 * 
 */
public class ArrayIterator<T> implements Iterator<T> {

	protected T[] array;
	protected int pos;

	/**
	 * @param array
	 */
	public ArrayIterator(final T[] array) {
		super();
		this.array = array;
		this.pos = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return pos < this.array.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		return this.array[pos++];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		// not supported
	}

}
