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

import de.clusteval.api.Pair;
import de.clusteval.api.factory.ServiceFactory;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.ParameterSet;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author deric
 */
public class ClusteringFactory extends ServiceFactory<IClustering> {

    private static ClusteringFactory instance;

    public static ClusteringFactory getInstance() {
        if (instance == null) {
            instance = new ClusteringFactory();
        }
        return instance;
    }

    private ClusteringFactory() {
        providers = new LinkedHashMap<>();
        Collection<? extends IClustering> list = Lookup.getDefault().lookupAll(IClustering.class);
        for (IClustering c : list) {
            providers.put(c.getClass().getSimpleName(), c);
        }
        sort();
    }

    public static IClustering parseFromString(String name) throws UnknownProviderException {
        return getInstance().getProvider(name);
    }

    public static IClustering parseFromString(IRepository repo, String name) throws UnknownProviderException {
        IClustering inst = getInstance().getProvider(name);
        inst.init(repo, System.currentTimeMillis(), new File(name));
        return inst;
    }

    /**
     * Convert an integer array holding cluster ids for every object to a fuzzy
     * coefficient matrix.
     *
     * @param clusterIds The cluster ids of the objects.
     * @return Fuzzy coefficient matrix. [i][j] holds the fuzzy coefficient for
     *         object i and cluster j.
     */
    public static float[][] clusterIdsToFuzzyCoeff(final int[] clusterIds) {
        Map<Integer, Integer> clusterPos = new HashMap<>();
        for (int id : clusterIds) {
            if (!(clusterPos.containsKey(id))) {
                clusterPos.put(id, clusterPos.size());
            }
        }

        int numberClusters = clusterPos.keySet().size();

        float[][] fuzzy = new float[clusterIds.length][numberClusters];
        for (int i = 0; i < clusterIds.length; i++) {
            fuzzy[i][clusterPos.get(clusterIds[i])] = 1.0f;
        }
        return fuzzy;
    }

    /**
     * The passed clustering is assumed to be a hard (non-fuzzy) clustering.
     *
     * @param repository
     * @param absPath
     * @param objectIds  The ids of the cluster items.
     * @param clusterIds Position i holds the cluster id of cluster item i.
     * @return A clustering wrapper object.
     */
    public static IClustering parseFromIntArray(final IRepository repository,
            final File absPath, final String[] objectIds, final int[] clusterIds) throws UnknownProviderException {
        return parseFromFuzzyCoeffMatrix(repository, absPath, objectIds,
                clusterIdsToFuzzyCoeff(clusterIds));
    }

    /**
     * @param repository
     * @param absPath
     * @param objectIds   The ids of the cluster items.
     * @param fuzzyCoeffs Position [i,j] is the fuzzy coefficient of object i
     *                    and cluster j.
     * @return A clustering wrapper object.
     */
    public static IClustering parseFromFuzzyCoeffMatrix(
            final IRepository repository, final File absPath,
            final String[] objectIds, final float[][] fuzzyCoeffs) throws UnknownProviderException {
        if (objectIds.length != fuzzyCoeffs.length) {
            throw new IllegalArgumentException(
                    "The number of object ids and cluster ids needs to be the same.");
        }
        Map<String, Cluster> clusters = new HashMap<>();

        for (int i = 0; i < fuzzyCoeffs.length; i++) {
            ClusterItem item = new ClusterItem(objectIds[i]);
            for (int j = 0; j < fuzzyCoeffs[i].length; j++) {
                final String clusterId = j + "";
                Cluster cluster = clusters.get(clusterId);
                if (cluster == null) {
                    cluster = new Cluster(clusterId);
                    clusters.put(clusterId, cluster);
                }

                cluster.add(item, fuzzyCoeffs[i][j]);
            }
        }
        IClustering clustering;
        try {
            IClustering c = ClusteringFactory.getInstance().getProvider("Clustering");
            clustering = c.getClass().newInstance();
            clustering.init(repository, System.currentTimeMillis(), absPath);
            for (Cluster cl : clusters.values()) {
                clustering.addCluster(cl);
            }
            return clustering;
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * This method parses clusterings together with the corresponding parameter
     * sets from a file.
     *
     * @param repository
     *
     * @param absFilePath    The absolute path to the input file.
     * @param parseQualities True, if the qualities of the clusterings should
     *                       also be parsed. Those will be taken from .qual-files.
     * @return A map containing parameter sets and corresponding clusterings.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static Pair<ParameterSet, IClustering> parseFromFile(final IRepository repository, final File absFilePath,
            final boolean parseQualities) throws IOException {
        try {
            IClusteringParser parser = Lookup.getDefault().lookup(IClusteringParser.class);
            IClusteringParser inst = parser.getClass().newInstance();

            inst.init(repository,
                    absFilePath.getAbsolutePath(), parseQualities);
            parser.process();

            return parser.getClusterings();
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

}
