/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.utils;

import java.util.HashMap;
import java.util.Map;

import junitx.framework.ArrayAssert;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import de.wiwie.wiutils.utils.Pair;
import de.wiwie.wiutils.utils.SimilarityMatrix;

/**
 * @author Christian Wiwie
 * 
 */
public class TestSimilarityMatrix {

	@Rule
	public TestName name = new TestName();

	protected SimilarityMatrix matrix;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println("################## Testcase: "
				+ this.getClass().getSimpleName() + "." + name.getMethodName());
		matrix = new SimilarityMatrix(new String[]{"0", "1", "2"},
				new double[][]{{1.0, 0.2, 0.3}, {0.2, 1.0, 0.7},
						{0.3, 0.7, 1.0}});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Map<Double, Integer> distribution = matrix.toDistribution(10);
		
		Map<Double, Integer> expectedMap = new HashMap<Double, Integer>();
		expectedMap.put(0.2, 2);
		expectedMap.put(0.27999999999999997, 2);
		expectedMap.put(0.36, 0);
		expectedMap.put(0.44000000000000006, 0);
		expectedMap.put(0.52, 0);
		expectedMap.put(0.6, 0);
		expectedMap.put(0.68, 2);
		expectedMap.put(0.76, 0);
		expectedMap.put(0.8400000000000001, 0);
		expectedMap.put(0.9199999999999999, 3);

		Assert.assertEquals(expectedMap, distribution);
	}

	@Test
	public void testToClassDistributionArray() {
		/*
		 * Test for one class only
		 */
		Pair<double[], int[][]> distribution = matrix.toClassDistributionArray(
				10, new String[][]{new String[]{"0", "1", "2"}});

		double[] expected1 = new double[]{0.200000, 0.288889, 0.377778,
				0.466667, 0.555556, 0.644444, 0.733333, 0.822222, 0.911111,
				1.000000};
		int[][] expected2 = new int[][]{new int[]{2, 2, 0, 0, 0, 2, 0, 0, 0, 3}};
		ArrayAssert.assertEquals(expected1, distribution.getFirst(), 0.000001);
		Assert.assertArrayEquals(expected2, distribution.getSecond());

		/*
		 * Test for two classes (e.g. intra vs inter)
		 */
		distribution = matrix.toClassDistributionArray(10, new String[][]{
				new String[]{"0", "1"}, new String[]{"2"}});

		expected1 = new double[]{0.200000, 0.288889, 0.377778, 0.466667,
				0.555556, 0.644444, 0.733333, 0.822222, 0.911111, 1.000000};
		expected2 = new int[][]{new int[]{2, 0, 0, 0, 0, 0, 0, 0, 0, 2},
				new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}};
		ArrayAssert.assertEquals(expected1, distribution.getFirst(), 0.000001);
		Assert.assertArrayEquals(expected2, distribution.getSecond());
	}

	@Test
	public void testIntraInterDistributionArray() {
		/*
		 * test for one class
		 */
		Map<String, Integer> idToClass = new HashMap<String, Integer>();
		idToClass.put("0", 0);
		idToClass.put("1", 0);
		idToClass.put("2", 0);
		Pair<double[], int[][]> distribution = matrix
				.toIntraInterDistributionArray(10, idToClass);

		double[] expected1 = new double[]{0.200000, 0.288889, 0.377778,
				0.466667, 0.555556, 0.644444, 0.733333, 0.822222, 0.911111,
				1.000000};
		int[][] expected2 = new int[][]{
				new int[]{2, 2, 0, 0, 0, 2, 0, 0, 0, 3},
				new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
		ArrayAssert.assertEquals(expected1, distribution.getFirst(), 0.000001);
		Assert.assertArrayEquals(expected2, distribution.getSecond());

		/*
		 * test for two classes
		 */
		idToClass = new HashMap<String, Integer>();
		idToClass.put("0", 0);
		idToClass.put("1", 0);
		idToClass.put("2", 1);
		distribution = matrix.toIntraInterDistributionArray(10, idToClass);

		expected1 = new double[]{0.200000, 0.288889, 0.377778, 0.466667,
				0.555556, 0.644444, 0.733333, 0.822222, 0.911111, 1.000000};
		expected2 = new int[][]{new int[]{2, 0, 0, 0, 0, 0, 0, 0, 0, 3},
				new int[]{0, 2, 0, 0, 0, 2, 0, 0, 0, 0}};
		ArrayAssert.assertEquals(expected1, distribution.getFirst(), 0.000001);
		Assert.assertArrayEquals(expected2, distribution.getSecond());

	}
}
