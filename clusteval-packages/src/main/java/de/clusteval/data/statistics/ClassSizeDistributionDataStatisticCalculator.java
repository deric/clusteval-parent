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

import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IGoldStandard;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.stats.DataStatisticCalculator;
import de.clusteval.api.stats.StatisticCalculateException;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public class ClassSizeDistributionDataStatisticCalculator
        extends
        DataStatisticCalculator<ClassSizeDistributionDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public ClassSizeDistributionDataStatisticCalculator(IRepository repository,
            long changeDate, File absPath, final IDataConfig dataConfig)
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
    public ClassSizeDistributionDataStatisticCalculator(
            final ClassSizeDistributionDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#calculate(data.DataConfig)
     */
    @Override
    protected ClassSizeDistributionDataStatistic calculateResult()
            throws StatisticCalculateException {
        try {
            IDataSet ds = dataConfig.getDatasetConfig().getDataSet();
            ds.loadIntoMemory();
            List<String> dataSetIds = ds.getIds();
            ds.unloadFromMemory();
            IGoldStandard gs = dataConfig.getGoldstandardConfig()
                    .getGoldstandard();
            gs.loadIntoMemory();
            IClustering clazzes = gs.getClustering();
            gs.unloadFromMemory();

            double[] fuzzySizes = new double[clazzes.getClusters().size()];
            String[] classLabels = new String[clazzes.getClusters().size()];
            Iterator<Cluster> it = clazzes.getClusters().iterator();
            for (int i = 0; i < classLabels.length; i++) {
                Cluster clazz = it.next();
                classLabels[i] = clazz.getId();

                Map<ClusterItem, Float> fuzzyItems = clazz.getFuzzyItems();
                for (Map.Entry<ClusterItem, Float> item : fuzzyItems.entrySet()) {
                    if (dataSetIds.contains(item.getKey().getId())) {
                        fuzzySizes[i] += item.getValue();
                    }
                }
            }

            ClassSizeDistributionDataStatistic result = new ClassSizeDistributionDataStatistic(
                    repository, false, changeDate, absPath, classLabels,
                    fuzzySizes);
            lastResult = result;
            return result;
        } catch (Exception e) {
            throw new StatisticCalculateException(e);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void writeOutputTo(final File absFolderPath) {
    }
}
