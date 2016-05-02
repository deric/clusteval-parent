/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.data.statistics;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.repository.IRepository;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.utils.ArraysExt;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import java.io.File;
import org.openide.util.Exceptions;

/**
 * @author Christian Wiwie
 *
 */
public class ClusteringCoefficientDataStatisticCalculator
        extends
        DataStatisticCalculator<ClusteringCoefficientDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public ClusteringCoefficientDataStatisticCalculator(IRepository repository,
            long changeDate, File absPath, IDataConfig dataConfig)
            throws RegisterException {
        super(repository, changeDate, absPath, dataConfig);
    }

    /**
     * The copy constructor for this statistic calculator.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public ClusteringCoefficientDataStatisticCalculator(
            final ClusteringCoefficientDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    @Override
    protected ClusteringCoefficientDataStatistic calculateResult() throws DataStatisticCalculateException {
        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            try {
                IDataSetConfig dataSetConfig = dataConfig.getDatasetConfig();
                RelativeDataSet dataSet = (RelativeDataSet) (dataSetConfig
                        .getDataSet().getInStandardFormat());

                if (!dataSet.isInMemory()) {
                    dataSet.loadIntoMemory();
                }
                SimilarityMatrix simMatrix = dataSet.getDataSetContent();
                if (dataSet.isInMemory()) {
                    dataSet.unloadFromMemory();
                }

                double[][] similarities = simMatrix.toArray();
                similarities = ArraysExt.scaleBy(similarities,
                        ArraysExt.max(similarities), true);

                rEngine.assign("simMatrix", similarities);
                rEngine.eval("library('igraph')");
                rEngine.eval("gr <- graph.adjacency(simMatrix,weighted=TRUE)");
                rEngine.eval("gr <- simplify(as.undirected(gr,mode='collapse'), remove.loops=TRUE, remove.multiple=TRUE)");
                rEngine.eval("trans <- transitivity(gr,type='global',vids=V(gr))");
                RExpr result = rEngine.eval("trans");
                return new ClusteringCoefficientDataStatistic(repository,
                        false, changeDate, absPath, result.asDouble());

            } catch (Exception e) {
                throw new DataStatisticCalculateException(e);
            } finally {
                rEngine.clear();
            }
        } catch (InterruptedException | RException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#writeOutputTo(java.io.File)
     */
    @SuppressWarnings("unused")
    @Override
    public void writeOutputTo(File absFolderPath) {
    }

}
