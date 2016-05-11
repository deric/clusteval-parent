/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api.cluster;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.ClusteringParseException;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.repository.IRepositoryObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author deric
 */
public interface IClustering extends IRepositoryObject, Iterable<Cluster> {

    Map<Cluster, Float> getClusterForItem(ClusterItem item);

    IClustering clone();

    /**
     * @param id The id of the cluster.
     * @return The cluster with the given id.
     */
    Cluster getClusterWithId(final String id);

    /**
     * @return A set with all clusters of this clustering.
     */
    Set<Cluster> getClusters();

    /**
     * @param qualitySet Set the qualities of this clustering.
     */
    void setQualities(final ClusteringQualitySet qualitySet);

    /**
     * @return Returns the qualities of this clustering.
     * @see Clustering#qualities
     */
    ClusteringQualitySet getQualities();

    /**
     * @return A set with all cluster items contained in this clustering.
     */
    Set<ClusterItem> getClusterItems();

    /**
     * @return The number of items in this clustering. In case of fuzzy
     *         clusterings this may differ from the fuzzy size.
     */
    int size();

    /**
     * @return The fuzzy size of this clustering.
     * @see #fuzzySize
     */
    double fuzzySize();

    /**
     *
     * @return A string representing this clustering, where clusters are
     *         separated by semi-colons and elements of clusters are separated by
     *         commas.
     */
    String toFormattedString();

    /**
     * Remove a cluster item from this clustering by removing the item from
     * every cluster contained.
     *
     * @param item The item to remove
     * @return True if this item was contained in this clustering.
     */
    boolean removeClusterItem(final ClusterItem item);

    /**
     *
     * @param id The id of the cluster item.
     * @return The cluster item with the given id.
     */
    ClusterItem getClusterItemWithId(final String id);

    /**
     * Remove a cluster item from the specified cluster.
     *
     * @param item    The item to remove
     * @param cluster The cluster to remove the item from.
     * @return True if this item was contained in this clustering.
     */
    boolean removeClusterItem(final ClusterItem item, final Cluster cluster);

    /**
     * Add a cluster to this clustering.
     *
     * @param cluster The cluster to add.
     * @return true, if the cluster is added and hasn't been in the clustering
     *         before.
     */
    boolean addCluster(final Cluster cluster);

    ClusteringQualitySet assessQuality(final IDataConfig dataConfig, final List<ClusteringEvaluation> qualityMeasures)
            throws UnknownGoldStandardFormatException, IOException,
                   InvalidDataSetFormatException;

    void loadIntoMemory() throws ClusteringParseException;

    void unloadFromMemory();

    /**
     * This method converts a fuzzy to a hard clustering by assigning each item
     * to the cluster, with the highest according fuzzy coefficient. If there
     * are ties, the assigned cluster is randomly selected.
     *
     * @return A hard clustering resulting from converting this fuzzy
     *         clustering.
     */
    IClustering toHardClustering();
}
