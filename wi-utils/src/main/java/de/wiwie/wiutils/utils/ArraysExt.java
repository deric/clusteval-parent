/*
 * Copyright (C) 2015-2016 Christian Wiwie
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.wiwie.wiutils.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
public class ArraysExt {

    /**
     * Returns an array with the absolute values of the input array.
     *
     * @param template The input array
     * @return The array with absolute values.
     */
    public static double[] abs(double[] template) {
        double[] result = new double[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = Math.abs(template[i]);
        }
        return result;
    }

    /**
     * Returns an array with the absolute values of the input array.
     *
     * @param template The input array
     * @return The array with absolute values.
     */
    public static double[][] abs(double[][] template) {
        double[][] result = new double[template.length][];
        for (int i = 0; i < template.length; i++) {
            result[i] = abs(template[i]);
        }
        return result;
    }

    /**
     * Adds a double value to every element of the array, such that
     * result[i]=template[i]+add.
     *
     * @param template The input array
     * @param add The double value to add to the elements of the input array
     * @return The result array
     */
    public static double[] add(double[] template, double add) {
        double[] result = new double[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = template[i] + add;
        }
        return result;
    }

    /**
     * Adds double values to elements of the input array, such that
     * result[i]=template[i]+add[i].
     *
     * @param template The input array
     * @param add The array with double values to add to the elements of the
     * input array
     * @return The result array
     */
    public static double[] add(double[] template, double[] add) {
        double[] result = new double[Math.max(template.length, add.length)];
        for (int i = 0; i < template.length; i++) {
            result[i] += template[i];
        }
        for (int i = 0; i < add.length; i++) {
            result[i] += add[i];
        }
        return result;
    }

    /**
     * Adds a double value to every element of the array, such that
     * result[i]=template[i]+add.
     *
     * @param template The input array
     * @param add The double value to add to the elements of the input array
     * @return The result array
     */
    public static double[][] add(double[][] template, double add) {
        double[][] result = new double[template.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = ArraysExt.add(template[i], add);
        }
        return result;
    }

    /**
     * Adds a double value to every element of the array, such that
     * result[i]=template[i]+add.
     *
     * @param template The input array
     * @param add The double value to add to the elements of the input array
     * @return The result array
     */
    public static int[] add(int[] template, int add) {
        int[] result = new int[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = template[i] + add;
        }
        return result;
    }

    /**
     * Adds double values to elements of the input array, such that
     * result[i]=template[i]+add[i].
     *
     * @param template The input array
     * @param add The array with double values to add to the elements of the
     * input array
     * @return The result array
     */
    public static int[] add(int[] template, int[] add) {
        int[] result = new int[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = template[i] + add[i];
        }
        return result;
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * double values and returns an array.
     *
     * @param text The input string
     * @return The two dimensional double array with the values contained in the
     * string.
     */
    public static double[][] double2DFromSeparatedString(String text) {
        return double2DFromSeparatedString(text, '\t');
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * double values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates double values in the
     * string.
     * @return The two dimensional double array with the values contained in the
     * string.
     */
    public static double[][] double2DFromSeparatedString(String text,
            char separator) {
        return double2DFromSeparatedString(text, separator, '\n');
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * double values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates double values in the
     * string.
     * @param lineSeparator The character that separates the sub-arrays in the
     * string.
     * @return The two dimensional double array with the values contained in the
     * string.
     */
    public static double[][] double2DFromSeparatedString(String text,
            char separator, char lineSeparator) {
        String[] split = text.split(lineSeparator + "");
        double[][] result = new double[split.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = doublesFromSeparatedString(split[i], separator);
        }
        return result;
    }

    /**
     * Interprets the passed string as a one dimensional separated list of
     * double values and returns an array.
     *
     * @param text The input string
     * @return The dimensional double array with the values contained in the
     * string.
     */
    public static double[] doublesFromSeparatedString(String text) {
        return ArraysExt.doublesFromSeparatedString(text, '\t');
    }

    /**
     * Interprets the passed string as a one dimensional separated list of
     * double values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates double values in the
     * string.
     * @return The double array with the values contained in the string.
     */
    public static double[] doublesFromSeparatedString(String text,
            char separator) {
        String[] split = text.split(separator + "");
        double[] result = new double[split.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.valueOf(split[i]);
        }
        return result;
    }

    /**
     * Returns an array containing only those values of the input array passing
     * the filter.
     *
     * @param x The input array
     * @param filter The filter
     * @return The filtered array.
     */
    public static int[] filter(int[] x, Filter filter) {
        List<Integer> result = new LinkedList<>();
        for (int val : x) {
            if (filter.filter(val)) {
                result.add(val);
            }
        }
        return ArraysExt.toPrimitive(result.toArray(new Integer[0]));
    }

    /**
     * Returns an array containing the floored values of the input array.
     *
     * @param array The input array
     * @return The result array
     */
    public static double[] floor(double[] array) {
        double[] result = new double[array.length];
        for (int pos = 0; pos < array.length; pos++) {
            result[pos] = Math.floor(array[pos]);
        }
        return result;
    }

    /**
     * Returns a histogram for the values of the input array.
     *
     * @param min The minimum value of the histogram.
     * @param array The input array.
     * @return The histogram for the input array
     */
    public static long[] hist(int min, int[] array) {
        long[] result = new long[ArraysExt.max(array) + 1 - min];

        for (int i : array) {
            result[i - min]++;
        }

        return result;
    }

    /**
     * Returns a histogram for the values of the input array for x >= 0.
     *
     * @param array The input array.
     * @return The histogram for the input array
     */
    public static long[] hist(int[] array) {
        return hist(0, array);
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @return The two dimensional integer array with the values contained in
     * the string.
     */
    public static int[][] int2DFromSeparatedString(String text) {
        return int2DFromSeparatedString(text, '\t');
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates integer values in the
     * string.
     * @return The two dimensional integer array with the values contained in
     * the string.
     */
    public static int[][] int2DFromSeparatedString(String text, char separator) {
        return int2DFromSeparatedString(text, separator, '\n');
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates integer values in the
     * string.
     * @param lineSeparator The character that separates the sub-arrays in the
     * string.
     * @return The two dimensional integer array with the values contained in
     * the string.
     */
    public static int[][] int2DFromSeparatedString(String text, char separator,
            char lineSeparator) {
        String[] split = text.split(lineSeparator + "");
        int[][] result = new int[split.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = intsFromSeparatedString(split[i], separator);
        }
        return result;
    }

    /**
     * Interprets the passed string as a one dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @return The dimensional integer array with the values contained in the
     * string.
     */
    public static int[] intsFromSeparatedString(String text) {
        return ArraysExt.intsFromSeparatedString(text, '\t');
    }

    /**
     * Interprets the passed string as a one dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates integer values in the
     * string.
     * @return The integer array with the values contained in the string.
     */
    public static int[] intsFromSeparatedString(String text, char separator) {
        String[] split = text.split(separator + "");
        int[] result = new int[split.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.valueOf(split[i]);
        }
        return result;
    }

    /**
     * Returns an array containing the e-logs of the values in the input array.
     *
     * @param array The input array
     * @return The result array
     */
    public static double[] log(double[] array) {
        return ArraysExt.log(array, false, 0.0);
    }

    /**
     * Returns an array containing the e-logs of the values in the input array.
     *
     * @param array The input array
     * @param replaceInfty True, if you want to replace infinity values in the
     * input array
     * @param replacement The value you want to replace infinity values with.
     * @return The result array
     */
    public static double[] log(double[] array, boolean replaceInfty,
            double replacement) {
        double[] result = new double[array.length];
        for (int pos = 0; pos < array.length; pos++) {
            if (array[pos] <= 0 && replaceInfty) {
                result[pos] = replacement;
            } else // result[pos] = Math.log10(array[pos])/Math.log10(2);
            {
                result[pos] = Math.log(array[pos]);
            }
        }
        return result;
    }

    /**
     * Returns an array containing the e-logs of the values in the input array.
     *
     * @param array The input array
     * @return The result array
     */
    public static double[][] log(double[][] array) {
        return ArraysExt.log(array, false, 0.0);
    }

    /**
     * Returns an array containing the e-logs of the values in the input array.
     *
     * @param array The input array
     * @param replaceInfty True, if you want to replace infinity values in the
     * input array
     * @param replacement The value you want to replace infinity values with.
     * @return The result array
     */
    public static double[][] log(double[][] array, boolean replaceInfty,
            double replacement) {
        double[][] result = new double[array.length][];
        for (int pos = 0; pos < array.length; pos++) {
            result[pos] = ArraysExt.log(array[pos], replaceInfty, replacement);
        }
        return result;
    }

    /**
     * Returns an array containing the e-logs of the values in the input array.
     *
     * @param array The input array
     * @return The result array
     */
    public static double[] log(int[] array) {
        return ArraysExt.log(array, false, 0.0);
    }

    /**
     * Returns an array containing the e-logs of the values in the input array.
     *
     * @param array The input array
     * @param replaceInfty True, if you want to replace infinity values in the
     * input array
     * @param replacement The value you want to replace infinity values with.
     * @return The result array
     */
    public static double[] log(int[] array, boolean replaceInfty,
            double replacement) {
        double[] result = new double[array.length];
        for (int pos = 0; pos < array.length; pos++) {
            if (array[pos] <= 0 && replaceInfty) {
                result[pos] = replacement;
            } else {
                result[pos] = Math.log(array[pos]);
            }
        }
        return result;
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @return The two dimensional integer array with the values contained in
     * the string.
     */
    public static long[][] long2DFromSeparatedString(String text) {
        return long2DFromSeparatedString(text, '\t');
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates integer values in the
     * string.
     * @return The two dimensional integer array with the values contained in
     * the string.
     */
    public static long[][] long2DFromSeparatedString(String text, char separator) {
        return long2DFromSeparatedString(text, separator, '\n');
    }

    /**
     * Interprets the passed string as a two dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates integer values in the
     * string.
     * @param lineSeparator The character that separates the sub-arrays in the
     * string.
     * @return The two dimensional integer array with the values contained in
     * the string.
     */
    public static long[][] long2DFromSeparatedString(String text,
            char separator, char lineSeparator) {
        String[] split = text.split(lineSeparator + "");
        long[][] result = new long[split.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = longsFromSeparatedString(split[i], separator);
        }
        return result;
    }

    /**
     * Interprets the passed string as a one dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @return The dimensional integer array with the values contained in the
     * string.
     */
    public static long[] longsFromSeparatedString(String text) {
        return ArraysExt.longsFromSeparatedString(text, '\t');
    }

    /**
     * Interprets the passed string as a one dimensional separated list of
     * integer values and returns an array.
     *
     * @param text The input string
     * @param separator The character that separates integer values in the
     * string.
     * @return The integer array with the values contained in the string.
     */
    public static long[] longsFromSeparatedString(String text, char separator) {
        String[] split = text.split(separator + "");
        long[] result = new long[split.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Long.valueOf(split[i]);
        }
        return result;
    }

    /**
     * Returns the maximal value contained in the input values.
     *
     * @param array The input values
     * @return The maximal value
     */
    public static double max(double... array) {
        return array[maxPos(array)];
    }

    /**
     * Returns the maximal value contained in the input values.
     *
     * @param array The input values
     * @return The maximal value
     */
    public static double max(double[][] array) {
        double result = Double.MIN_VALUE;
        for (double[] subArray : array) {
            result = Math.max(result, max(subArray));
        }
        return result;
    }

    /**
     * Returns the maximal value contained in the input values.
     *
     * @param array The input values
     * @return The maximal value
     */
    public static int max(int... array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty array given");
        }
        int max = 0;
        for (int i : array) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    /**
     * Returns the index of the input array containing the maximal value.If
     * there are several indices containing the maximal value, the first index
     * is returned.
     *
     * @param array The input array
     * @return The index containing the maximal value.
     */
    public static int maxPos(double[] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty array given");
        }
        double max = -Double.MAX_VALUE;
        int maxX = 0;
        for (int pos = 0; pos < array.length; pos++) {
            double val = array[pos];
            if (val > max) {
                max = val;
                maxX = pos;
            }
        }
        return maxX;
    }

    /**
     * @param array The input array.
     * @return An array containing only the entries that are not equal to NaN.
     */
    public static double[] removeNA(final double... array) {
        final List<Double> result = new ArrayList<>();

        for (double d : array) {
            if (!Double.isNaN(d) && !Double.isInfinite(d)) {
                result.add(d);
            }
        }

        return ArraysExt.toPrimitive(result.toArray(new Double[0]));
    }

    /**
     * Returns the mean of the values in the given array.
     *
     * @param array The input array
     * @return The mean of the input array.
     */
    public static double mean(double... array) {
        return mean(true, array);
    }

    /**
     * Returns the mean of the values in the given array.
     *
     * @param removeNA
     *
     * @param array The input array
     * @return The mean of the input array.
     */
    public static double mean(boolean removeNA, double... array) {
        double[] withoutNA = removeNA ? removeNA(array) : array;
        return (sum(withoutNA)) / withoutNA.length;
    }

    /**
     * Returns the mean of the values in the given array.
     *
     * @param arrays The input array
     * @return The mean of the input array.
     */
    public static double[] mean(double[][] arrays) {
        int maxSize = 0;
        for (double[] arr : arrays) {
            if (arr.length > maxSize) {
                maxSize = arr.length;
            }
        }
        return ArraysExt.mean(arrays, maxSize);
    }

    private static double[] mean(double[][] arrays, int targetSize) {
        double[] result = new double[targetSize];

        for (double[] arr : arrays) {
            result = ArraysExt.add(result, arr);
        }

        for (int i = 0; i < result.length; i++) {
            result[i] /= arrays.length;
        }
        return result;
    }

    /**
     * Returns the mean of the values in the given array.
     *
     * @param array The input array
     * @return The mean of the input array.
     */
    public static double mean(int... array) {
        return ((double) sum(array)) / array.length;
    }

    /**
     * Returns the mean of the values in the given array.
     *
     * @param array The input array
     * @return The mean of the input array.
     */
    public static double mean(long... array) {
        return ((double) sum(array)) / array.length;
    }

    /**
     * Returns the variance of the values in the given array.
     *
     * @param array The input array
     * @return The variance of the input array.
     */
    public static double variance(double... array) {
        return variance(true, array);
    }

    /**
     * Returns the variance of the values in the given array.
     *
     * @param removeNA Whether to ignore NaN entries in the input array.
     *
     * @param array The input array
     * @return The variance of the input array.
     */
    public static double variance(boolean removeNA, double... array) {
        double[] withoutNA = removeNA ? removeNA(array) : array;
        double result = 0.0;
        double mean = mean(withoutNA);
        for (double d : withoutNA) {
            result += Math.pow(d - mean, 2.0);
        }
        return result / withoutNA.length;
    }

    /**
     * Append two arrays into one
     *
     * @param array1 First array
     * @param array2 Second array
     * @return The new array, the concatenation of array1 and array2.
     */
    public static String[] merge(String[] array1, String[] array2) {
        String[] result = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    /**
     * Append two arrays into one
     *
     * @param array1 First array
     * @param array2 Second array
     * @return The new array, the concatenation of array1 and array2.
     */
    public static double[] merge(double[] array1, double[] array2) {
        double[] result = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    /**
     * Append two arrays into one
     *
     * @param array1 First array
     * @param array2 Second array
     * @return The new array, the concatenation of array1 and array2.
     */
    public static int[] merge(int[] array1, int[] array2) {
        int[] result = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    /**
     * Returns the minimal value contained in the input values.
     *
     * @param array The input values
     * @return The minimal value
     */
    public static double min(double... array) {
        return array[minPos(array)];
    }

    /**
     * Returns the minimal value contained in the input values.
     *
     * @param array The input values
     * @return The minimal value
     */
    public static double min(double[][] array) {
        double result = Double.MAX_VALUE;
        for (double[] subArray : array) {
            result = Math.min(result, subArray[minPos(subArray)]);
        }
        return result;
    }

    /**
     * Returns the minimal value contained in the input values.
     *
     * @param array The input values
     * @return The minimal value
     */
    public static int min(int... array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty array given");
        }
        int min = 0;
        for (int i : array) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

    /**
     * Returns the index of the input array containing the minimal value. If
     * there are several indices containing the minimal value, the first index
     * is returned.
     *
     * @param array The input array
     * @return The index containing the minimal value.
     */
    public static int minPos(double... array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty array given");
        }
        double min = Double.MAX_VALUE;
        int minX = 0;
        for (int pos = 0; pos < array.length; pos++) {
            double i = array[pos];
            if (i < min) {
                min = i;
                minX = pos;
            }
        }
        return minX;
    }

    /**
     * Returns an array containing the entries powered with an exponent.
     *
     * @param array The input array.
     * @param exp The exponent.
     * @return The array containing the powered entries.
     */
    public static double[] pow(double[] array, double exp) {
        double[] result = new double[array.length];
        for (int pos = 0; pos < result.length; pos++) {
            result[pos] = Math.pow(array[pos], exp);
        }
        return result;
    }

    /**
     * Returns an array containing the entries powered with an exponent.
     *
     * @param array The input array.
     * @param exp The exponent.
     * @return The array containing the powered entries.
     */
    public static double[][] pow(double[][] array, double exp) {
        double[][] result = new double[array.length][];
        for (int pos = 0; pos < result.length; pos++) {
            result[pos] = ArraysExt.pow(array[pos], exp);
        }
        return result;
    }

    /**
     * Returns an array containing the entries powered with an exponent.
     *
     * @param array The input array.
     * @param exp The exponent.
     * @return The array containing the powered entries.
     */
    public static long[] pow(int[] array, double exp) {
        long[] result = new long[array.length];
        for (int pos = 0; pos < result.length; pos++) {
            result[pos] = (long) Math.pow(array[pos], exp);
        }
        return result;
    }

    /**
     * Prints an array to the systems output stream.
     *
     * @param array The input array.
     */
    public static void print(double[] array) {
        System.out.println(ArraysExt.toString(array));
    }

    /**
     * Prints an array to the systems output stream.
     *
     * @param array The input array.
     * @param precision The precision to be used when printing double values.
     */
    public static void print(double[] array, int precision) {
        System.out.println(ArraysExt.toString(array, precision));
    }

    /**
     * Prints a two-dimensional array to the systems output stream. Subarrays
     * are printed row-wise.
     *
     * @param array The input array.
     */
    public static void print(double[][] array) {
        System.out.println("[");
        for (double[] bla : array) {
            print(bla);
        }
        System.out.println("]");
    }

    /**
     * Prints an array to the systems output stream.
     *
     * @param array The input array.
     */
    public static void print(int[] array) {
        System.out.println(Arrays.toString(array));
    }

    /**
     * Prints a two-dimensional array to the systems output stream. Subarrays
     * are printed row-wise.
     *
     * @param array The input array.
     */
    public static void print(int[][] array) {
        System.out.println("[");
        for (int[] bla : array) {
            print(bla);
        }
        System.out.println("]");
    }

    /**
     * Prints an array to the systems output stream.
     *
     * @param array The input array.
     */
    public static void print(long[] array) {
        System.out.println(Arrays.toString(array));
    }

    /**
     * Prints an array to the systems output stream.
     *
     * @param <T>
     * @param array The input array.
     */
    public static <T> void print(T[] array) {
        System.out.println(Arrays.toString(array));
    }

    /**
     * Prints an two-dimensional array to the systems output stream. Subarrays
     * are printed row-wise.
     *
     * @param <T>
     * @param array The input array.
     */
    public static <T> void print(T[][] array) {
        System.out.println("[");
        for (T[] bla : array) {
            print(bla);
        }
        System.out.println("]");
    }

    /**
     * Returns an array containing double values between a start value and an
     * end value with a certain stepsize. TODO: inclusive /exclusive end?
     *
     * @param start The first double value
     * @param end The last double value
     * @param stepsize The step size between double values of the array
     * @return An array containing the double values.
     *
     */
    public static double[] range(double start, double end, double stepsize) {
        double[] result = new double[(int) (end / stepsize)
                - (int) (start / stepsize)];
        for (int i = 0; i < result.length; i++) {
            result[i] = start + i * stepsize;
        }
        return result;
    }

    /**
     * Returns an array containing a certain number of double values between a
     * start value and an end value.
     *
     * @param start The first double value
     * @param end The last double value
     * @param count The number of values in the result array.
     * @param inclusiveEnd If true, the last value of the array will be the end
     * value.
     * @return An array containing the double values.
     *
     */
    public static double[] range(double start, double end, int count,
            boolean inclusiveEnd) {
        double[] result = new double[count];
        double constant = (inclusiveEnd
                           ? start * Math.max(count - 1, 1)
                           : start * count);
        double diff = end - start;
        for (int i = 0; i < result.length; i++) {
            result[i] = (constant + i * diff)
                    / (inclusiveEnd ? Math.max(count - 1, 1) : count);
        }
        return result;
    }

    /**
     * Returns an array with integer values between start and end and a stepsize
     * of 1.
     *
     * @param start The start value
     * @param end The end value
     * @return The result array.
     */
    public static int[] range(int start, int end) {
        return range(start, end, 1);
    }

    /**
     * Returns an array with integer values between start and end and a stepsize
     * of 1.
     *
     * @param start The start value
     * @param end The end value
     * @param stepsize The step size between the values of the result array.
     * @return The result array.
     */
    public static int[] range(int start, int end, int stepsize) {
        int[] result = new int[(end - start) / stepsize];
        for (int i = 0; i < result.length; i++) {
            result[i] = start + i * stepsize;
        }
        return result;
    }

    /**
     * Returns an array containing a certain number of integer values between a
     * start value and an end value without duplicates.
     *
     * @param start The first integer value
     * @param end The last integer value
     * @param count The number of values in the result array.
     * @param inclusiveEnd If true, the last value of the array will be the end
     * value.
     * @return An array containing the integer values.
     * @throws RangeCreationException This exception is thrown, if it is
     * impossible to create an array of integer values with that many values
     * between start and end without duplicates.
     *
     */
    public static int[] range(int start, int end, int count,
            boolean inclusiveEnd) throws RangeCreationException {
        return range(start, end, count, inclusiveEnd, true);
    }

    /**
     * Returns an array containing a certain number of integer values between a
     * start value and an end value.
     *
     * @param start The first integer value
     * @param end The last integer value
     * @param count The number of values in the result array.
     * @param inclusiveEnd If true, the last value of the array will be the end
     * value.
     * @param noDuplicates If true, the result array should not contain
     * duplicate values.
     * @return An array containing the integer values.
     * @throws RangeCreationException This exception is thrown, if it is
     * impossible to create an array of integer values with that many values
     * between start and end.
     *
     */
    public static int[] range(int start, int end, int count,
            boolean inclusiveEnd, boolean noDuplicates)
            throws RangeCreationException {
        if (noDuplicates && end - start < (inclusiveEnd ? count - 1 : count)) {
            throw new RangeCreationException(
                    "Range will contain duplicate values");
        }
        double[] tmp = range((double) start, (double) end, count, inclusiveEnd);
        int[] result = ArraysExt.toIntArray(tmp);
        return result;
    }

    /**
     * Create an array of a value repeated a certain number of times.
     *
     * @param val The double value to be repeated
     * @param times Number of times the integer value should be repeated
     * @return The result array.
     */
    public static int[] rep(int val, int times) {
        int[] result = new int[(times > 0 ? times : 0)];

        for (int i = 0; i < result.length; i++) {
            result[i] = val;
        }

        return result;
    }

    /**
     * Create an array of a value repeated a certain number of times.
     *
     * @param val The double value to be repeated
     * @param times Number of times the double value should be repeated
     * @return The result array.
     */
    public static double[] rep(double val, int times) {
        double[] result = new double[(times > 0 ? times : 0)];

        for (int i = 0; i < result.length; i++) {
            result[i] = val;
        }

        return result;
    }

    /**
     * Returns a scaled version of the input array. All entries of the input
     * array are divided by the given factor.
     *
     * @param array The input array.
     * @param factor The scaling factor.
     * @return The result array.
     */
    public static double[] scaleBy(double[] array, double factor) {
        return ArraysExt.scaleBy(array, factor, true, false);
    }

    /**
     * Returns a scaled version of the input array. All entries of the input
     * array are divided by the given factor.
     *
     * @param array The input array.
     * @param factor The scaling factor.
     * @param insitu Whether to write the subtracted values into the input
     * array.
     * @return The result array.
     */
    public static double[] scaleBy(double[] array, double factor, boolean insitu) {
        return ArraysExt.scaleBy(array, factor, true, insitu);
    }

    /**
     * Returns a scaled version of the input array. All entries of the input
     * array are divided by the given factor.
     *
     * @param array The input array.
     * @param factor The scaling factor.
     * @param down If true, the entries of the array are divided by the factor,
     * otherwise it is multiplied.
     * @param insitu Whether to write the subtracted values into the input
     * array.
     * @return The result array.
     */
    public static double[] scaleBy(double[] array, double factor, boolean down,
            boolean insitu) {
        double[] result;
        if (insitu) {
            result = array;
        } else {
            result = new double[array.length];
        }
        for (int i = 0; i < array.length; i++) {
            if (down) {
                result[i] = array[i] / factor;
            } else {
                result[i] = array[i] * factor;
            }
        }
        return result;
    }

    /**
     * Returns a scaled version of the input array. All entries of the input
     * array are divided by the given factor.
     *
     * @param array The input array.
     * @param factor The scaling factor.
     * @param insitu Whether to write the subtracted values into the input
     * array.
     * @return The result array.
     */
    public static double[][] scaleBy(double[][] array, double factor,
            boolean insitu) {
        double[][] result;
        if (insitu) {
            result = array;
        } else {
            result = new double[array.length][];
        }

        for (int i = 0; i < array.length; i++) {
            result[i] = ArraysExt.scaleBy(array[i], factor, true, insitu);
        }

        return result;
    }

    /**
     * Returns a scaled version of the input array. All entries of the input
     * array are divided by the given factor.
     *
     * @param array The input array.
     * @param factor The scaling factor.
     * @return The result array.
     */
    public static double[] scaleBy(int[] array, double factor) {
        return ArraysExt.scaleBy(ArraysExt.toDoubleArray(array), factor);
    }

    /**
     * Returns a scaled version of the input array. All entries of the input
     * array are divided by the given factor.
     *
     * @param array The input array.
     * @param factor The scaling factor.
     * @param down If true, the values in the input array are divided by the
     * factor, otherwise they are multiplied.
     * @return The result array.
     */
    public static double[] scaleBy(int[] array, double factor, boolean down) {
        return ArraysExt.scaleBy(ArraysExt.toDoubleArray(array), factor, down,
                false);
    }

    /**
     * Returns an array containing the square-roots of the values of the input
     * array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static double[] sqrt(int[] array) {
        double[] result = new double[array.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Math.sqrt(array[i]);
        }

        return result;
    }

    /**
     * Subtracts a double value from every element of the array, such that
     * result[i]=template[i]-subtract.
     *
     * @param template The input array
     * @param subtract The double value to subtract from the elements of the
     * input array
     * @return The result array
     */
    public static double[] subtract(double subtract, double[] template) {
        double[] result = new double[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = subtract - template[i];
        }
        return result;
    }

    /**
     * Subtracts a double value from every element of the array, such that
     * result[i][j]=template[i][j]-subtract.
     *
     * @param template The input array
     * @param subtract The double value to subtract from the elements of the
     * input array
     * @return The result array
     */
    public static double[][] subtract(double subtract, double[][] template) {
        return subtract(subtract, template, false);
    }

    /**
     * Subtracts a double value from every element of the array, such that
     * result[i][j]=template[i][j]-subtract.
     *
     * @param template The input array
     * @param subtract The double value to subtract from the elements of the
     * input array
     * @param insitu Whether to write the subtracted values into the input
     * array.
     * @return The result array
     */
    public static double[][] subtract(double subtract, double[][] template,
            boolean insitu) {
        double[][] result;
        if (insitu) {
            result = template;
        } else {
            result = new double[template.length][];
        }
        for (int i = 0; i < template.length; i++) {
            if (!insitu) {
                result[i] = new double[template[i].length];
            }
            for (int j = 0; j < template[i].length; j++) {
                result[i][j] = subtract - template[i][j];
            }
        }
        return result;
    }

    /**
     * Subtracts a double value from every element of the array, such that
     * result[i]=template[i]-subtract.
     *
     * @param template The input array
     * @param subtract The double value to subtract from the elements of the
     * input array
     * @return The result array
     */
    public static double[] subtract(double[] template, double subtract) {
        double[] result = new double[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = template[i] - subtract;
        }
        return result;
    }

    /**
     * Subtracts double values from elements of the input array, such that
     * result[i]=template[i]-subtract[i].
     *
     * @param template The input array
     * @param subtract The array with double values to add to the elements of
     * the input array
     * @return The result array
     */
    public static double[] subtract(double[] template, double[] subtract) {
        return subtract(template, subtract, false);
    }

    /**
     * Subtracts double values from elements of the input array, such that
     * result[i]=template[i]-subtract[i].
     *
     * @param template The input array
     * @param subtract The array with double values to add to the elements of
     * the input array
     * @param common If true, the length of the result array is the minimum of
     * the lengths of the two arrays.
     * @return The result array
     */
    public static double[] subtract(double[] template, double[] subtract,
            boolean common) {
        double[] result = new double[common ? Math.min(template.length,
                subtract.length) : Math.max(template.length, subtract.length)];
        for (int i = 0; i < result.length && i < template.length; i++) {
            result[i] = template[i];
        }
        for (int i = 0; i < result.length && i < subtract.length; i++) {
            result[i] -= subtract[i];
        }
        return result;
    }

    /**
     * Subtracts a double value from every element of the array, such that
     * result[i][j]=template[i][j]-subtract.
     *
     * @param template The input array
     * @param subtract The double value to subtract from the elements of the
     * input array
     * @return The result array
     */
    public static double[][] subtract(double[][] template, double subtract) {
        return subtract(template, subtract, false);
    }

    /**
     * Subtracts a double value from every element of the array, such that
     * result[i][j]=template[i][j]-subtract.
     *
     * @param template The input array
     * @param subtract The double value to subtract from the elements of the
     * input array
     * @param insitu Whether to write the subtracted values into the input
     * array.
     * @return The result array
     */
    public static double[][] subtract(double[][] template, double subtract,
            boolean insitu) {
        double[][] result;
        if (insitu) {
            result = template;
        } else {
            result = new double[template.length][];
        }
        for (int i = 0; i < result.length; i++) // TODO: make this one insitu as well
        {
            result[i] = ArraysExt.subtract(template[i], subtract);
        }
        return result;
    }

    /**
     * Subtracts double values from elements of the input array, such that
     * result[i][j]=template[i][j]-subtract[i][j].
     *
     * @param template The input array
     * @param subtract The array with double values to add to the elements of
     * the input array
     * @return The result array
     */
    public static double[][] subtract(double[][] template, double[][] subtract) {
        double[][] result = new double[template.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = subtract(template[i], subtract[i], false);
        }
        return result;
    }

    /**
     * Subtracts a integer value from every element of the array, such that
     * result[i]=template[i]-subtract.
     *
     * @param template The input array
     * @param subtract The integer value to subtract from the elements of the
     * input array
     * @return The result array
     */
    public static int[] subtract(int[] template, int subtract) {
        int[] result = new int[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = template[i] - subtract;
        }
        return result;
    }

    /**
     * Subtracts the integer values of a second array from the elements of the
     * first array, such that result[i]=template[i]-subtract[i].
     *
     * @param template The input array
     * @param subtract The array containing the elements to subtract from the
     * elements of the input array
     * @return The result array
     */
    public static int[] subtract(int[] template, int[] subtract) {
        int[] result = new int[template.length];
        for (int i = 0; i < template.length; i++) {
            result[i] = template[i] - subtract[i];
        }
        return result;
    }

    /**
     * Calculates the sum of the input array
     *
     * @param array The input array.
     * @return The sum of the values in the input array.
     */
    public static double sum(double[] array) {
        return sum(true, array);
    }

    /**
     * Calculates the sum of the input array
     *
     * @param removeNA Whether to ignore NaN entries in the array.
     *
     * @param array The input array.
     * @return The sum of the values in the input array.
     */
    public static double sum(boolean removeNA, double[] array) {
        double[] withoutNA = removeNA ? removeNA(array) : array;
        double result = 0.0;
        for (double d : withoutNA) {
            if (!removeNA || d != Double.NaN) {
                result += d;
            }
        }
        return result;
    }

    /**
     * Calculates the product of the input array
     *
     * @param array The input array.
     * @return The product of the values in the input array.
     */
    public static long product(int[] array) {
        long result = 1;
        for (int d : array) {
            result *= d;
        }
        return result;
    }

    /**
     * Calculates the product of the input array
     *
     * @param array The input array.
     * @return The product of the values in the input array.
     */
    public static double product(double[] array) {
        double result = 1.0;
        for (double d : array) {
            result *= d;
        }
        return result;
    }

    /**
     * Calculates the sum of the input array
     *
     * @param array The input array.
     * @return The sum of the values in the input array.
     */
    public static double sum(double[][] array) {
        double result = 0.0;
        for (double[] d : array) {
            result += ArraysExt.sum(d);
        }
        return result;
    }

    /**
     * Calculates the sum of the input array
     *
     * @param array The input array.
     * @return The sum of the values in the input array.
     */
    public static long sum(int[] array) {
        long result = 0;
        for (int i : array) {
            result += i;
        }
        return result;
    }

    /**
     * Calculates the sum of the input array
     *
     * @param array The input array.
     * @return The sum of the values in the input array.
     */
    public static long sum(int[][] array) {
        long result = 0;
        for (int[] i : array) {
            result += ArraysExt.sum(i);
        }
        return result;
    }

    /**
     * Calculates the sum of the input array
     *
     * @param array The input array.
     * @return The sum of the values in the input array.
     */
    public static long sum(long[] array) {
        long result = 0;
        for (long i : array) {
            result += i;
        }
        return result;
    }

    /**
     * Returns an array containing the float casted values of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static float[] toFloatArray(double[] array) {
        float[] result = new float[array.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (float) array[i];
        }
        return result;
    }

    /**
     * Returns an array containing the double casted values of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static double[] toDoubleArray(int[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * Returns an array containing the float casted values of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static float[][] toFloatArray(double[][] array) {
        float[][] result = new float[array.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toFloatArray(array[i]);
        }
        return result;
    }

    /**
     * Returns an array containing the double casted values of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static double[][] toDoubleArray(int[][] array) {
        double[][] result = new double[array.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toDoubleArray(array[i]);
        }
        return result;
    }

    /**
     * Returns an array containing the double casted values of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static double[] toDoubleArray(long[] array) {
        double[] result = new double[array.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * Returns an array containing the double casted values of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static double[][] toDoubleArray(long[][] array) {
        double[][] result = new double[array.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = toDoubleArray(array[i]);
        }
        return result;
    }

    /**
     * Calculates the histogram of the given array with the given number of
     * buckets.
     *
     * @param array The input array.
     * @param buckets The number of buckets.
     * @return The first element of the result pair is a double array containing
     * the x-ticks of the histogram and the second element the number of
     * occurrences of these x-ticks.
     */
    public static Pair<double[], int[]> toHistogram(final double[] array,
            final int buckets) {
        double min = ArraysExt.min(array);
        double max = ArraysExt.max(array);
        double[] minValues = ArraysExt.range(min, max, buckets, false);

        int[] result = new int[buckets];
        for (double d : array) {
            int pos = 0;
            while (pos < minValues.length && minValues[pos] <= d) {
                pos++;
            }
            pos = Math.max(pos - 1, 0);
            result[pos]++;
        }

        return Pair.getPair(minValues, result);
    }

    /**
     * Returns an array containing the integer casted values of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static int[] toIntArray(double[] array) {
        int[] result = new int[array.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = (int) array[i];
        }

        return result;
    }

    /**
     * Returns an array containing the primitive double values of the Double
     * objects of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static double[] toPrimitive(final Double[] array) {
        double[] arr = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            arr[i] = array[i];
        }
        return arr;
    }

    /**
     * Returns an array containing the primitive float values of the Float
     * objects of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static float[] toPrimitive(final Float[] array) {
        float[] arr = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            arr[i] = array[i];
        }
        return arr;
    }

    /**
     * Returns an array containing the primitive integer values of the Integer
     * objects of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static int[] toPrimitive(final Integer[] array) {
        int[] arr = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            arr[i] = array[i];
        }
        return arr;
    }

    /**
     * Returns an array containing the primitive long values of the Long objects
     * of the input array.
     *
     * @param array The input array.
     * @return The result array.
     */
    public static long[] toPrimitive(final Long[] array) {
        long[] arr = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            arr[i] = array[i];
        }
        return arr;
    }

    public static String toSeparatedString(double[] result) {
        return ArraysExt.toSeparatedString(result, '\t');
    }

    public static String toSeparatedString(double[] result, char separator) {
        StringBuilder sb = new StringBuilder();
        for (double d : result) {
            sb.append(d);
            sb.append(separator);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String toSeparatedString(double[][] result) {
        return toSeparatedString(result, '\t');
    }

    /**
     * @param result
     * @param separator
     * @return
     */
    public static String toSeparatedString(double[][] result, char separator) {
        return toSeparatedString(result, separator, '\n');
    }

    /**
     * @param result
     * @param separator
     * @param lineSeparator
     * @return
     */
    public static String toSeparatedString(double[][] result, char separator,
            char lineSeparator) {
        StringBuilder sb = new StringBuilder();

        for (double[] d : result) {
            sb.append(toSeparatedString(d, separator));
            sb.append(lineSeparator);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * @param result
     * @return Returns a tabseparated string
     */
    public static String toSeparatedString(int[] result) {
        return ArraysExt.toSeparatedString(result, '\t');
    }

    public static String toSeparatedString(int[] result, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i : result) {
            sb.append(i);
            sb.append(separator);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String toSeparatedString(int[][] result) {
        return toSeparatedString(result, '\t');
    }

    public static String toSeparatedString(int[][] result, char separator) {
        return toSeparatedString(result, '\t', '\n');
    }

    public static String toSeparatedString(int[][] result, char separator,
            char lineSeparator) {
        StringBuilder sb = new StringBuilder();

        for (int[] i : result) {
            sb.append(toSeparatedString(i, separator));
            sb.append(lineSeparator);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * @param result
     * @return Returns a tabseparated string
     */
    public static String toSeparatedString(long[] result) {
        return ArraysExt.toSeparatedString(result, '\t');
    }

    public static String toSeparatedString(long[] result, char separator) {
        StringBuilder sb = new StringBuilder();
        for (long i : result) {
            sb.append(i);
            sb.append(separator);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String toSeparatedString(long[][] result) {
        return toSeparatedString(result, '\t');
    }

    public static String toSeparatedString(long[][] result, char separator) {
        return toSeparatedString(result, '\t', '\n');
    }

    public static String toSeparatedString(long[][] result, char separator,
            char lineSeparator) {
        StringBuilder sb = new StringBuilder();

        for (long[] i : result) {
            sb.append(toSeparatedString(i, separator));
            sb.append(lineSeparator);
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static String toString(double[] a) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0;; i++) {
            b.append(String.format(Locale.US, "%.6f", a[i]));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    public static String toString(double[] a, int precision) {
        if (a == null) {
            return "null";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "[]";
        }

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0;; i++) {
            b.append(String.format(Locale.US, "%." + precision + "f", a[i]));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    public static String[] toString(Object[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }

    /**
     * Removes all duplicates from the input array.
     *
     * @param array The input array.
     * @return The input array without duplicates.
     */
    public static double[] unique(double[] array) {
        Set<Double> set = new HashSet<Double>();
        List<Double> list = new ArrayList<Double>();
        for (double d : array) {
            if (set.add(d)) {
                list.add(d);
            }
        }
        double[] result = ArraysExt.toPrimitive(list.toArray(new Double[0]));
        return result;
    }

    /**
     * Removes all duplicates from the input array.
     *
     * @param array The input array.
     * @return The input array without duplicates.
     */
    public static int[] unique(int[] array) {
        Set<Integer> set = new HashSet<Integer>();
        List<Integer> list = new ArrayList<Integer>();
        for (int d : array) {
            if (set.add(d)) {
                list.add(d);
            }
        }
        int[] result = ArraysExt.toPrimitive(list.toArray(new Integer[0]));
        return result;
    }
}
