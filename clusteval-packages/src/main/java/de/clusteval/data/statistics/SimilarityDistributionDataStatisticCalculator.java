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

import de.clusteval.api.Pair;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.utils.ArraysExt;
import de.clusteval.utils.FileUtils;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public class SimilarityDistributionDataStatisticCalculator
        extends
        DataStatisticCalculator<SimilarityDistributionDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public SimilarityDistributionDataStatisticCalculator(IRepository repository,
            long changeDate, File absPath, final IDataConfig dataConfig)
            throws RegisterException {
        super(repository, changeDate, absPath, dataConfig);
        if (!(RelativeDataSetFormat.class.isAssignableFrom(dataConfig
                .getDatasetConfig().getDataSet().getDataSetFormat().getClass()))) {
            throw new IllegalArgumentException(
                    "Similarity distribution can only be calculated for relative dataset formats");
        }
    }

    /**
     * The copy constructor for this statistic calculator.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public SimilarityDistributionDataStatisticCalculator(
            final SimilarityDistributionDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#calculate(data.DataConfig)
     */
    @Override
    protected SimilarityDistributionDataStatistic calculateResult()
            throws DataStatisticCalculateException {
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

            Pair<double[], int[]> distribution = simMatrix
                    .toDistributionArray(100);

            double[] distr = ArraysExt.toDoubleArray(distribution.getSecond());

            distr = ArraysExt.scaleBy(distr, ArraysExt.sum(distr));

            SimilarityDistributionDataStatistic result = new SimilarityDistributionDataStatistic(
                    repository, false, changeDate, absPath,
                    distribution.getFirst(), distr);
            lastResult = result;
            return result;
        } catch (Exception e) {
            throw new DataStatisticCalculateException(e);
        }
    }

    @Override
    public void writeOutputTo(final File absFolderPath)
            throws InterruptedException, RException {
        IRengine rEngine = repository.getRengineForCurrentThread();
        rEngine.eval("plotSimilarityDistribution <- function(title, path, xlabels, distr) {"
                + "names(distr) <- xlabels;"
                + "svg(filename=paste(path,'.svg',sep=''));"
                + "barplot(main=title, distr,legend = c('similarities'));"
                + "dev.off()" + "}; return 0;");
        double[] xlabels = this.getStatistic().xlabels;
        double[] distr = this.getStatistic().distribution;
        rEngine.assign("xlabels", xlabels);
        rEngine.assign("distr", distr);
        rEngine.eval("plotSimilarityDistribution("
                + "'similiarity distribution "
                + dataConfig.getName()
                + "',path="
                + "'"
                + FileUtils.buildPath(absFolderPath.getAbsolutePath(),
                        dataConfig.getName()) + "_similarityDistr"
                + "',xlabels=xlabels, " + "distr=distr)");
        distr = ArraysExt.scaleBy(distr, 100.0, false);
        distr = ArraysExt.add(distr, 1.0);
        distr = ArraysExt.log(distr, true, 0.0);
        rEngine.assign("distr", distr);
        rEngine.eval("plotSimilarityDistribution("
                + "'similiarity distribution"
                + dataConfig.getName()
                + "',path="
                + "'"
                + FileUtils.buildPath(absFolderPath.getAbsolutePath(),
                        dataConfig.getName()) + "_similarityDistr_log"
                + "',xlabels=xlabels, " + "distr=distr)");
        rEngine.clear();
    }
}
