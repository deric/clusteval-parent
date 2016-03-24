package de.wiwie.wiutils.utils;

import java.io.Serializable;

/**
 * @author Christian Wiwie
 *
 * @param <T>
 * @param <U>
 * @param <V>
 */
public final class Triple<T, U, V> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8420971786137038441L;
    protected T e1;
    protected U e2;
    protected V e3;

    /**
     * @param el1
     * @param el2
     * @param el3
     */
    public Triple(final T el1, final U el2, final V el3) {
        this.e1 = el1;
        this.e2 = el2;
        this.e3 = el3;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object p) {
        if (p instanceof Triple) {
            final Triple<T, U, V> other = (Triple<T, U, V>) p;
            return other.e1.equals(this.e1) && other.e2.equals(this.e2)
                    && other.e3.equals(this.e3);
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

    public V getThird() {
        return this.e3;
    }

    public void setThird(final V newThird) {
        this.e3 = newThird;
    }

    @Override
    public int hashCode() {
        return this.e1.hashCode() + this.e2.hashCode() + this.e3.hashCode();
    }

    @Override
    public String toString() {
        return "(" + this.e1.toString() + "\t" + this.e2.toString() + "\t"
                + this.e3.toString() + ")";
    }

    /**
     * @param first
     * @param second
     * @param third
     * @return
     */
    public static <A, B, C> Triple<A, B, C> getTriple(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }
}
