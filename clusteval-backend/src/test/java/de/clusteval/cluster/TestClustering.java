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
package de.clusteval.cluster;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junitx.framework.ArrayAssert;

import org.junit.Test;

import de.wiwie.wiutils.utils.Pair;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.program.ParameterSet;
import de.clusteval.utils.AbstractClustEvalTest;

/**
 * @author Christian Wiwie
 * 
 */
public class TestClustering extends AbstractClustEvalTest {

	@Test
	public void testParseFromIntArray() throws RegisterException {
		String[] ids = new String[]{"1", "2", "3", "4", "5"};
		int[] clusterIds = new int[]{1, 1, 1, 2, 2};

		Clustering expected = new Clustering(this.getRepository(),
				System.currentTimeMillis(), new File(""));
		Cluster cluster1 = new Cluster("0");
		cluster1.add(new ClusterItem("1"), 1.0f);
		cluster1.add(new ClusterItem("2"), 1.0f);
		cluster1.add(new ClusterItem("3"), 1.0f);
		expected.addCluster(cluster1);
		Cluster cluster2 = new Cluster("1");
		cluster2.add(new ClusterItem("4"), 1.0f);
		cluster2.add(new ClusterItem("5"), 1.0f);
		expected.addCluster(cluster2);

		Clustering clustering = Clustering.parseFromIntArray(
				this.getRepository(), new File(""), ids, clusterIds);
		Assert.assertEquals(expected, clustering);
	}

	@Test
	public void testToFormattedString() {
		String[] ids = new String[]{"1", "2", "3", "4", "5"};
		int[] clusterIds = new int[]{1, 1, 1, 2, 2};

		Clustering clustering = Clustering.parseFromIntArray(
				this.getRepository(), new File(""), ids, clusterIds);
		Assert.assertEquals("1:1.0,2:1.0,3:1.0;4:1.0,5:1.0",
				clustering.toFormattedString());
	}

	@Test
	public void testToFormattedStringEmpty() {
		String[] ids = new String[]{};
		int[] clusterIds = new int[]{};

		Clustering clustering = Clustering.parseFromIntArray(
				this.getRepository(), new File(""), ids, clusterIds);
		Assert.assertEquals("", clustering.toFormattedString());
	}

	@Test
	public void testParseFromIntArrayEmpty() throws RegisterException {
		String[] ids = new String[]{};
		int[] clusterIds = new int[]{};

		Clustering clustering = Clustering.parseFromIntArray(
				this.getRepository(), new File(""), ids, clusterIds);
		Assert.assertEquals(
				new Clustering(this.getRepository(),
						System.currentTimeMillis(), new File("")), clustering);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseFromIntArrayDifferentLength() {
		String[] ids = new String[]{};
		int[] clusterIds = new int[]{1};

		Clustering.parseFromIntArray(this.getRepository(), new File(""), ids,
				clusterIds);
	}

	@Test
	public void testParseFromIntArraySameObjects() {
		String[] ids = new String[]{"1", "2", "3", "4", "5"};
		int[] clusterIds = new int[]{1, 1, 1, 2, 2};

		Clustering clustering = Clustering.parseFromIntArray(
				this.getRepository(), new File(""), ids, clusterIds);
		Assert.assertTrue(clustering
				.getClusterForItem(clustering.getClusterItemWithId("1"))
				.keySet().iterator().next() == clustering
				.getClusterForItem(clustering.getClusterItemWithId("2"))
				.keySet().iterator().next());
		Assert.assertTrue(clustering
				.getClusterForItem(clustering.getClusterItemWithId("2"))
				.keySet().iterator().next() == clustering
				.getClusterForItem(clustering.getClusterItemWithId("3"))
				.keySet().iterator().next());
		Assert.assertTrue(clustering
				.getClusterForItem(clustering.getClusterItemWithId("4"))
				.keySet().iterator().next() == clustering
				.getClusterForItem(clustering.getClusterItemWithId("5"))
				.keySet().iterator().next());
	}

	@Test
	public void testClusterIdsToFuzzyCoeff() {
		int[] clusterIds = new int[]{1, 2, 2, 2, 5, 3, 4, 1, 1};
		float[][] result = Clustering.clusterIdsToFuzzyCoeff(clusterIds);
		float[][] expected = new float[][]{{1f, 0f, 0f, 0f, 0f},
				{0f, 1f, 0f, 0f, 0f}, {0f, 1f, 0f, 0f, 0f},
				{0f, 1f, 0f, 0f, 0f}, {0f, 0f, 1f, 0f, 0f},
				{0f, 0f, 0f, 1f, 0f}, {0f, 0f, 0f, 0f, 1f},
				{1f, 0f, 0f, 0f, 0f}, {1f, 0f, 0f, 0f, 0f}};
		for (int i = 0; i < expected.length; i++)
			ArrayAssert.assertEquals(expected[i], result[i], 0f);
	}

	@Test
	public void testFuzzyClustering() throws IOException,
			ClusteringParseException {
		Pair<ParameterSet, Clustering> p = Clustering
				.parseFromFile(
						null,
						new File(
								"testCaseRepository/results/01_30_2013-21_31_25_tc_vs_DS1/clusters/fuzzyClustering.txt")
								.getAbsoluteFile(), false);
		p.getSecond().loadIntoMemory();
		Set<ClusterItem> clusterItems = new HashSet<ClusterItem>();
		ClusterItem expectedItem1 = new ClusterItem("id1");
		ClusterItem expectedItem2 = new ClusterItem("id2");
		ClusterItem expectedItem3 = new ClusterItem("id3");
		clusterItems.add(expectedItem1);
		clusterItems.add(expectedItem2);
		clusterItems.add(expectedItem3);
		Assert.assertEquals(clusterItems, p.getSecond().getClusterItems());

		Cluster expectedCluster1 = new Cluster("1");
		expectedCluster1.add(expectedItem1, 0.6f);
		expectedCluster1.add(expectedItem2, 0.2f);
		expectedCluster1.add(expectedItem3, 0.9f);
		Cluster expectedCluster2 = new Cluster("2");
		expectedCluster2.add(expectedItem1, 0.3f);
		expectedCluster2.add(expectedItem2, 0.7f);
		expectedCluster2.add(expectedItem3, 0.05f);
		Cluster expectedCluster3 = new Cluster("3");
		expectedCluster3.add(expectedItem1, 0.1f);
		expectedCluster3.add(expectedItem2, 0.1f);
		expectedCluster3.add(expectedItem3, 0.05f);

		Map<ClusterItem, Map<Cluster, Float>> expectedClusters = new HashMap<ClusterItem, Map<Cluster, Float>>();

		Map<Cluster, Float> expectedClusters1 = new HashMap<Cluster, Float>();
		expectedClusters1.put(expectedCluster1, 0.6f);
		expectedClusters1.put(expectedCluster2, 0.3f);
		expectedClusters1.put(expectedCluster3, 0.1f);
		expectedClusters.put(expectedItem1, expectedClusters1);

		Map<Cluster, Float> expectedClusters2 = new HashMap<Cluster, Float>();
		expectedClusters2.put(expectedCluster1, 0.2f);
		expectedClusters2.put(expectedCluster2, 0.7f);
		expectedClusters2.put(expectedCluster3, 0.1f);
		expectedClusters.put(expectedItem2, expectedClusters2);

		Map<Cluster, Float> expectedClusters3 = new HashMap<Cluster, Float>();
		expectedClusters3.put(expectedCluster1, 0.9f);
		expectedClusters3.put(expectedCluster2, 0.05f);
		expectedClusters3.put(expectedCluster3, 0.05f);
		expectedClusters.put(expectedItem3, expectedClusters3);

		for (ClusterItem i : p.getSecond().getClusterItems()) {
			Assert.assertEquals(expectedClusters.get(i), i.getFuzzyClusters());
		}
	}

	@Test
	public void testFuzzyToHardClustering() throws IOException,
			ClusteringParseException {
		Pair<ParameterSet, Clustering> p = Clustering
				.parseFromFile(
						null,
						new File(
								"testCaseRepository/results/01_30_2013-21_31_25_tc_vs_DS1/clusters/fuzzyClustering.txt")
								.getAbsoluteFile(), false);
		
		p.getSecond().loadIntoMemory();
		Clustering hardClustering = p.getSecond().toHardClustering();
		System.out.println(hardClustering);

		Set<ClusterItem> clusterItems = new HashSet<ClusterItem>();
		ClusterItem expectedItem1 = new ClusterItem("id1");
		ClusterItem expectedItem2 = new ClusterItem("id2");
		ClusterItem expectedItem3 = new ClusterItem("id3");
		clusterItems.add(expectedItem1);
		clusterItems.add(expectedItem2);
		clusterItems.add(expectedItem3);
		Assert.assertEquals(clusterItems, hardClustering.getClusterItems());

		Cluster expectedCluster1 = new Cluster("1");
		expectedCluster1.add(expectedItem1, 1.0f);
		expectedCluster1.add(expectedItem3, 1.0f);
		Cluster expectedCluster2 = new Cluster("2");
		expectedCluster2.add(expectedItem2, 1.0f);

		Map<ClusterItem, Map<Cluster, Float>> expectedClusters = new HashMap<ClusterItem, Map<Cluster, Float>>();

		Map<Cluster, Float> expectedClusters1 = new HashMap<Cluster, Float>();
		expectedClusters1.put(expectedCluster1, 1.0f);
		expectedClusters.put(expectedItem1, expectedClusters1);

		Map<Cluster, Float> expectedClusters2 = new HashMap<Cluster, Float>();
		expectedClusters2.put(expectedCluster2, 1.0f);
		expectedClusters.put(expectedItem2, expectedClusters2);

		Map<Cluster, Float> expectedClusters3 = new HashMap<Cluster, Float>();
		expectedClusters3.put(expectedCluster1, 1.0f);
		expectedClusters.put(expectedItem3, expectedClusters3);

		for (ClusterItem i : hardClustering.getClusterItems()) {
			Assert.assertEquals(expectedClusters.get(i), i.getFuzzyClusters());
		}
	}
}
