/**
 * 
 */
package utils;

/*
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A simple class that implements a growable array of ints, and knows how to
 * serialize itself as efficiently as a non-growable array.
 */
public final class LongList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2292093290851284478L;

	protected long[] data = new long[8]; // An array to store the numbers.

	protected transient int size = 0; // Index of next unused element of array

	/** Add an int to the array, growing the array if necessary */
	public void add(final long x) {
		if (this.data.length == this.size) {
			this.resize(this.data.length * 2); // Grow array if needed.
		}
		this.data[this.size++] = x; // Store the int in it.
	}

	public boolean contains(final long no) {
		for (int i = 0; i < this.size; i++) {
			if (this.data[i] == no) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Does this object contain the same values as the object o? We override
	 * this Object method so we can test the class.
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof IntList)) {
			return false;
		}
		final LongList that = (LongList) o;
		if (this.size != that.size) {
			return false;
		}
		for (int i = 0; i < this.size; i++) {
			if (this.data[i] != that.data[i]) {
				return false;
			}
		}
		return true;
	}

	/** Return an element of the array */
	public long get(final int index) throws ArrayIndexOutOfBoundsException {
		if (index >= this.size) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return this.data[index];
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}

	public int length() {
		return this.size();
	}

	/** Compute the transient size field after deserializing the array */
	private void readObject(final ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject(); // Read the array normally.
		this.size = this.data.length; // Restore the transient field.
	}

	public void remove(final long object) {
		for (int pos = 0; pos < this.size; pos++) {
			if (this.data[pos] == object) {
				this.removeAtIndex(pos);
				return;
			}
		}
	}

	public void removeAtIndex(final int index) {
		for (int pos = index; pos < this.size - 1; pos++) {
			this.data[pos] = this.data[pos + 1];
		}
		this.size--;
	}

	/** An internal method to change the allocated size of the array */
	protected void resize(final int newsize) {
		final long[] newdata = new long[newsize]; // Create a new array
		System.arraycopy(this.data, 0, newdata, 0, this.size); // Copy array
																// elements.
		this.data = newdata; // Replace old array
	}

	/**
	 * Set the specified position to the given int Returns the old value of the
	 * position
	 */
	public long set(final int pos, final long val) {
		final long result = this.get(pos);
		this.data[pos] = val;
		return result;
	}

	public int size() {
		return this.size;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < this.size; i++) {
			sb.append(this.data[i]);
			sb.append(", ");
		}
		if (sb.length() > 1) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString();
	}

	/** Get rid of unused array elements before serializing the array */
	private void writeObject(final ObjectOutputStream out) throws IOException {
		if (this.data.length > this.size) {
			this.resize(this.size); // Compact the array.
		}
		out.defaultWriteObject(); // Then write it out normally.
	}
}