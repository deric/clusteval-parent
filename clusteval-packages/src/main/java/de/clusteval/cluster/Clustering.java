/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.cluster;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.ClusteringQualitySet;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.ClusteringParseException;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.utils.TextFileParser;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 * A clustering contains several clusters. Every cluster contains cluster items.
 */
@ServiceProvider(service = IClustering.class)
public class Clustering extends RepositoryObject implements Iterable<Cluster>, IClustering {

    /**
     * The fuzzy size of this clustering is the sum of all fuzzy coefficients of
     * any item contained in any cluster.
     */
    protected float fuzzySize;

    /**
     * The clusters contained in this clustering.
     */
    protected Set<Cluster> clusters;

    /**
     * Used to get clusters in O(1) with their id.
     */
    protected Map<String, Cluster> clusterIdToCluster;

    /**
     * A map from cluster item to cluster and fuzzy coefficient. This serves for
     * fast access and performance purposes only.
     */
    protected Map<ClusterItem, Map<Cluster, Float>> itemToCluster;

    /**
     * Used to get cluster items in O(1) with their id.
     */
    protected Map<String, ClusterItem> itemIdToItem;

    /**
     * If the qualities of this clustering were set using the method
     * {@link #setQualities(ClusteringQualitySet)}, they are stored in this
     * attribute.
     */
    protected ClusteringQualitySet qualities;

    public Clustering() {
        super();
    }

    /**
     * Instantiates a new clustering.
     *
     * @param repository
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public Clustering(IRepository repository, long changeDate, File absPath)
            throws RegisterException {
        super(repository, false, changeDate, absPath);
        this.clusters = new HashSet<>();
        this.clusterIdToCluster = new HashMap<>();
        this.itemToCluster = new HashMap<>();
        this.itemIdToItem = new HashMap<>();

        this.register();
    }

    /**
     * The copy constructor of clusterings.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public Clustering(final Clustering other) throws RegisterException {
        super(other);
        this.clusters = cloneClusters(other.clusters);
        this.clusterIdToCluster = cloneClusterIdToCluster(other.clusterIdToCluster);
        this.itemToCluster = cloneItemToClusters(other.itemToCluster);
        this.itemIdToItem = cloneItemIdToItem(other.itemIdToItem);
    }

    @Override
    public Clustering clone() {
        Clustering result;
        try {
            result = new Clustering(this.repository, this.changeDate,
                    this.absPath);
            final Map<Cluster, Cluster> clusters = new HashMap<>();
            final Map<ClusterItem, ClusterItem> items = new HashMap<>();

            for (Cluster cl : this.clusters) {
                Cluster newCluster = new Cluster(cl.getId());
                clusters.put(cl, newCluster);

                for (Map.Entry<ClusterItem, Float> e : cl.getFuzzyItems().entrySet()) {
                    if (!items.containsKey(e.getKey())) {
                        ClusterItem newItem = new ClusterItem(e.getKey().getId());
                        items.put(e.getKey(), newItem);
                    }
                    ClusterItem item = items.get(e.getKey());
                    newCluster.add(item, e.getValue());
                }

                result.addCluster(newCluster);
            }
            return result;
        } catch (RegisterException e1) {
            // should not occur
            e1.printStackTrace();
        }
        return null;
    }

    protected static Map<String, Cluster> cloneClusterIdToCluster(
            Map<String, Cluster> clusterIdToCluster) {
        final Map<String, Cluster> result = new HashMap<>();

        for (Map.Entry<String, Cluster> entry : clusterIdToCluster.entrySet()) {
            result.put(entry.getKey(), entry.getValue().clone());
        }

        return result;
    }

    protected static Map<String, ClusterItem> cloneItemIdToItem(
            Map<String, ClusterItem> itemIdToItem) {
        final Map<String, ClusterItem> result = new HashMap<>();

        for (Map.Entry<String, ClusterItem> entry : itemIdToItem.entrySet()) {
            result.put(entry.getKey(), entry.getValue().clone());
        }

        return result;
    }

    protected static Map<ClusterItem, Map<Cluster, Float>> cloneItemToClusters(
            Map<ClusterItem, Map<Cluster, Float>> itemToCluster2) {
        final Map<ClusterItem, Map<Cluster, Float>> result = new HashMap<>();

        for (Map.Entry<ClusterItem, Map<Cluster, Float>> entry : itemToCluster2
                .entrySet()) {
            final Map<Cluster, Float> newMap = new HashMap<>();

            for (Map.Entry<Cluster, Float> entry2 : entry.getValue().entrySet()) {
                newMap.put(entry2.getKey().clone(), entry2.getValue());
            }

            result.put(entry.getKey().clone(), newMap);
        }

        return result;
    }

    protected static Set<Cluster> cloneClusters(Set<Cluster> clusters) {
        final Set<Cluster> result = new HashSet<>();
        final Set<ClusterItem> items = new HashSet<>();

        for (Cluster cl : clusters) {
            Cluster newCluster = new Cluster(cl.getId());

            for (Map.Entry<ClusterItem, Float> e : cl.getFuzzyItems().entrySet()) {
                if (!items.contains(e.getKey())) {
                    items.add(new ClusterItem(e.getKey().getId()));
                }
            }

            result.add(newCluster);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Clustering)) {
            return false;
        }
        Clustering other = (Clustering) obj;
        return (!this.absPath.equals("") && !other.absPath.equals("") && this.absPath
                .equals(other.absPath)) || this.clusters.equals(other.clusters);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        // return this.clusters.hashCode();
        return this.absPath.hashCode();
    }

    /**
     * @param item The item to look for.
     * @return A map containing all clusters together with fuzzy coefficients,
     *         in which the given item is contained.
     */
    @Override
    public Map<Cluster, Float> getClusterForItem(ClusterItem item) {
        return this.itemToCluster.get(item);
    }

    /**
     * @param id The id of the cluster.
     * @return The cluster with the given id.
     */
    @Override
    public Cluster getClusterWithId(final String id) {
        return this.clusterIdToCluster.get(id);
    }

    /**
     * @return A set with all clusters of this clustering.
     */
    @Override
    public Set<Cluster> getClusters() {
        return this.clusters;
    }

    /**
     * @param qualitySet Set the qualities of this clustering.
     */
    @Override
    public void setQualities(final ClusteringQualitySet qualitySet) {
        this.qualities = qualitySet;
    }

    /**
     * @return Returns the qualities of this clustering.
     * @see Clustering#qualities
     */
    @Override
    public ClusteringQualitySet getQualities() {
        return this.qualities;
    }

    /**
     * @return A set with all cluster items contained in this clustering.
     */
    @Override
    public Set<ClusterItem> getClusterItems() {
        return this.itemToCluster.keySet();
    }

    /**
     *
     * @param id The id of the cluster item.
     * @return The cluster item with the given id.
     */
    @Override
    public ClusterItem getClusterItemWithId(final String id) {
        return this.itemIdToItem.get(id);
    }

    /**
     * Add a cluster to this clustering.
     *
     * @param cluster The cluster to add.
     * @return true, if the cluster is added and hasn't been in the clustering
     *         before.
     */
    public boolean addCluster(final Cluster cluster) {
        boolean b = this.clusters.add(cluster);
        if (b) {
            this.clusterIdToCluster.put(cluster.getId(), cluster);
            this.fuzzySize += cluster.fuzzySize();
            Map<ClusterItem, Float> items = cluster.getFuzzyItems();
            for (ClusterItem item : items.keySet()) {
                if (!this.itemToCluster.containsKey(item)) {
                    this.itemToCluster.put(item, new HashMap<>());
                }
                this.itemToCluster.get(item).putAll(item.getFuzzyClusters());

                if (!itemIdToItem.containsKey(item.getId())) {
                    itemIdToItem.put(item.getId(), item);
                }
            }
        }
        return b;
    }

    /**
     * Remove a cluster item from this clustering by removing the item from
     * every cluster contained.
     *
     * @param item The item to remove
     * @return True if this item was contained in this clustering.
     */
    public boolean removeClusterItem(final ClusterItem item) {
        Map<Cluster, Float> fuzzyClusters = this.itemToCluster.remove(item);
        boolean result = false;
        for (Cluster cl : fuzzyClusters.keySet()) {
            result = cl.remove(item);
            this.fuzzySize -= fuzzyClusters.get(cl);
        }
        return result;
    }

    /**
     * Remove a cluster item from the specified cluster.
     *
     * @param item    The item to remove
     * @param cluster The cluster to remove the item from.
     * @return True if this item was contained in this clustering.
     */
    public boolean removeClusterItem(final ClusterItem item, final Cluster cluster) {
        if (!this.itemToCluster.containsKey(item)) {
            return false;
        }

        float fuzzy = this.itemToCluster.get(item).remove(cluster);
        boolean result = cluster.remove(item);
        this.fuzzySize -= fuzzy;

        if (this.itemToCluster.get(item).isEmpty()) {
            this.itemIdToItem.remove(item.getId());
            this.itemToCluster.remove(item);
        }

        return result;
    }

    /**
     * @return The fuzzy size of this clustering.
     * @see #fuzzySize
     */
    @Override
    public double fuzzySize() {
        return this.fuzzySize;
    }

    /**
     * @return The number of items in this clustering. In case of fuzzy
     *         clusterings this may differ from the fuzzy size.
     */
    @Override
    public int size() {
        return this.itemToCluster.keySet().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Cluster> iterator() {
        return this.clusters.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[Clustering: " + clusters.toString() + "]";
    }

    /**
     *
     * @return A string representing this clustering, where clusters are
     *         separated by semi-colons and elements of clusters are separated by
     *         commas.
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        for (Cluster cluster : this.clusters) {
            for (Map.Entry<ClusterItem, Float> entry : cluster.getFuzzyItems()
                    .entrySet()) {
                if (entry.getValue() > 0f) {
                    sb.append(entry.getKey());
                    sb.append(":");
                    sb.append(entry.getValue());
                    sb.append(",");
                }
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(";");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * This method converts a fuzzy to a hard clustering by assigning each item
     * to the cluster, with the highest according fuzzy coefficient. If there
     * are ties, the assigned cluster is randomly selected.
     *
     * @return A hard clustering resulting from converting this fuzzy
     *         clustering.
     */
    public IClustering toHardClustering() {

        Clustering result;
        try {
            result = new Clustering(this.repository, this.changeDate,
                    this.absPath);

            // assign each item to the cluster with the maximal fuzzy
            // coefficient.
            Map<String, Cluster> newClusters = new HashMap<>();
            Set<ClusterItem> items = this.getClusterItems();
            for (ClusterItem item : items) {
                Cluster cl = null;
                double maxFuzzyCoeff = -0.1;

                for (Map.Entry<Cluster, Float> p : item.getFuzzyClusters()
                        .entrySet()) {
                    if (p.getValue() > maxFuzzyCoeff) {
                        cl = p.getKey();
                        maxFuzzyCoeff = p.getValue();
                    }
                }

                if (cl == null) {
                    continue;
                }

                if (!newClusters.containsKey(cl.getId())) {
                    newClusters.put(cl.getId(), new Cluster(cl.getId()));
                }
                newClusters.get(cl.getId()).add(new ClusterItem(item.getId()), 1.0f);
            }
            for (Cluster cl : newClusters.values()) {
                result.addCluster(cl);
            }

            return result;
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads this clustering into memory (clusters + cluster items);
     *
     * @throws ClusteringParseException
     */
    public void loadIntoMemory() throws ClusteringParseException {
        final Clustering result = this;

        TextFileParser p;
        try {
            p = new TextFileParser(this.getAbsolutePath(), new int[]{0},
                    new int[]{1}) {

                @Override
                protected void processLine(String[] key, String[] value) {
                    if (currentLine == 0) {
                        return;
                    }
                    try {
                        String clusteringString = value[0];
                        String[] clusters = clusteringString.split(";");
                        int no = 1;
                        for (String cluster : clusters) {
                            Cluster c = new Cluster((no++ + "").intern());
                            String[] items = cluster.split(",");
                            for (String item : items) {
                                String[] itemSplit = item.split(":");
                                String id = itemSplit[0].intern();
                                ClusterItem cItem = result
                                        .getClusterItemWithId(id);
                                if (cItem == null) {
                                    cItem = new ClusterItem(id);
                                }
                                c.add(cItem, Float.parseFloat(itemSplit[1]));
                            }
                            result.addCluster(c);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            p.process();
        } catch (IOException e) {
            throw new ClusteringParseException("The clustering "
                    + this.getAbsolutePath() + " could not be load into memory");
        }
    }

    /**
     * Unloads this clustering from memory.
     */
    public void unloadFromMemory() {
        if (this.clusterIdToCluster != null) {
            this.clusterIdToCluster.clear();
            this.clusterIdToCluster = null;
        }
        if (this.clusters != null) {
            this.clusters.clear();
            this.clusters = null;
        }
        if (this.itemIdToItem != null) {
            this.itemIdToItem.clear();
            this.itemIdToItem = null;
        }
        if (this.itemToCluster != null) {
            this.itemToCluster.clear();
            this.itemToCluster = null;
        }
    }

    /**
     * Assess quality.
     *
     * @param dataConfig
     *
     * @param qualityMeasures the quality measures
     * @return A set of qualities for every quality measure that was passed in
     *         the list.
     * @throws UnknownGoldStandardFormatException the unknown gold standard
     * format exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws UnknownDataSetFormatException
     * @throws InvalidDataSetFormatException
     */
    public ClusteringQualitySet assessQuality(final IDataConfig dataConfig,
            final List<ClusteringEvaluation> qualityMeasures)
            throws UnknownGoldStandardFormatException, IOException,
                   InvalidDataSetFormatException {
        // added: 30.07.2014: assume all ids of the dataset missing in the
        // clustering to be singletons
        for (String id : dataConfig.getDatasetConfig().getDataSet()
                .getInStandardFormat().getIds()) {
            // if the object with this id is not in the clustering, add a
            // singleton cluster with this object
            if (!this.itemIdToItem.containsKey(id)) {
                Cluster cluster = new Cluster(this.clusterIdToCluster.size()
                        + "");
                cluster.add(new ClusterItem(id), 1.0f);
                this.addCluster(cluster);
            }
        }

        // TODO: 20.08.2012 ensure, that this runresult is in standard format
        final ClusteringQualitySet resultSet = new ClusteringQualitySet();
        for (ClusteringEvaluation qualityMeasure : qualityMeasures) {
            // do not calculate, when there is no goldstandard
            if (qualityMeasure.requiresGoldstandard()
                    && !dataConfig.hasGoldStandardConfig()) {
                continue;
            }
            ClustEvalValue quality;
            try {
                IClustering goldStandard = null;
                if (dataConfig.hasGoldStandardConfig()) {
                    goldStandard = dataConfig.getGoldstandardConfig()
                            .getGoldstandard().getClustering();
                }
                // convert the clustering to a hard clustering if the measure
                // does not support fuzzy clusterings
                IClustering cl = this;
                if (!qualityMeasure.supportsFuzzyClusterings()) {
                    cl = this.toHardClustering();
                }

                quality = qualityMeasure.getQualityOfClustering(cl,
                        goldStandard, dataConfig);
                // if (dataConfig.hasGoldStandardConfig())
                // dataConfig.getGoldstandardConfig().getGoldstandard()
                // .unloadFromMemory();
                // we rethrow some exceptions, since they mean, that we
                // cannot calculate ANY quality measures for this data
            } catch (InvalidDataSetFormatException e) {
                throw e;
            } catch (Exception e) {
                this.log.error(e.getMessage(), e);
                // all the remaining exceptions are catched, because they
                // mean, that the quality measure calculation is flawed
                quality = ClustEvalValue.getForDouble(Double.NaN);
            }
            resultSet.put(qualityMeasure, quality);
        }
        return resultSet;
    }
}
