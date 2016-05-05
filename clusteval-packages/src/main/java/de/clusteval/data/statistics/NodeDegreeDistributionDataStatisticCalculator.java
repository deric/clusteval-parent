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

import de.clusteval.api.Matrix;
import de.clusteval.api.Pair;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.utils.ArraysExt;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.IOException;

/**
 * @author Christian Wiwie
 *
 */
public class NodeDegreeDistributionDataStatisticCalculator
        extends
        DataStatisticRCalculator<NodeDegreeDistributionDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public NodeDegreeDistributionDataStatisticCalculator(IRepository repository,
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
    public NodeDegreeDistributionDataStatisticCalculator(
            final NodeDegreeDistributionDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#calculate(data.DataConfig)
     */
    @Override
    protected NodeDegreeDistributionDataStatistic calculateResultHelper(
            final IRengine rEngine)
            throws IllegalArgumentException, IOException, InvalidDataSetFormatException,
                   RegisterException {
        IDataSetConfig dataSetConfig = dataConfig.getDatasetConfig();
        RelativeDataSet dataSet = (RelativeDataSet) (dataSetConfig.getDataSet()
                .getInStandardFormat());

        if (!dataSet.isInMemory()) {
            dataSet.loadIntoMemory();
        }
        Matrix simMatrix = dataSet.getDataSetContent();
        if (dataSet.isInMemory()) {
            dataSet.unloadFromMemory();
        }

        double[] degrees = new double[simMatrix.getIds().size()];
        for (int i = 0; i < degrees.length; i++) {
            for (int j = 0; j < simMatrix.getIds().size(); j++) {
                degrees[i] += simMatrix.getSimilarity(i, j);
            }
        }

        Pair<double[], int[]> histogram = ArraysExt.toHistogram(degrees, 100);

        double[] distr = ArraysExt.scaleBy(histogram.getSecond(),
                ArraysExt.sum(histogram.getSecond()));

        lastResult = new NodeDegreeDistributionDataStatistic(repository, false,
                changeDate, absPath, histogram.getFirst(), distr);
        return lastResult;
    }

    @Override
    protected void writeOutputToHelper(final File absFolderPath, final IRengine rEngine)
            throws RException, InterruptedException {

        rEngine.eval("plotNodeDegreeDistribution <- function(title, path, xlabels, distr) {"
                + "names(distr) <- xlabels;"
                + "svg(filename=paste(path,'.svg',sep=''));"
                + "barplot(main=title, distr,legend = c('node degrees'));"
                + "dev.off()" + "}");

        double[] xlabels = this.getStatistic().xlabels;
        double[] distr = this.getStatistic().distribution;

        rEngine.assign("xlabels", xlabels);
        rEngine.assign("distr", distr);

        rEngine.eval("plotNodeDegreeDistribution("
                + "'node degree distribution "
                + dataConfig.getName()
                + "',path="
                + "'"
                + FileUtils.buildPath(absFolderPath.getAbsolutePath(),
                        dataConfig.getName()) + "_nodeDegreeDistr"
                + "',xlabels=xlabels, " + "distr=distr)");

        /*
         * Create a log-plot
         */
        distr = ArraysExt.scaleBy(distr, 100.0, false);
        distr = ArraysExt.add(distr, 1.0);
        distr = ArraysExt.log(distr, true, 0.0);

        rEngine.assign("distr", distr);

        rEngine.eval("plotNodeDegreeDistribution("
                + "'node degree distribution"
                + dataConfig.getName()
                + "',path="
                + "'"
                + FileUtils.buildPath(absFolderPath.getAbsolutePath(),
                        dataConfig.getName()) + "_nodeDegreeDistr_log"
                + "',xlabels=xlabels, " + "distr=distr)");
    }
}
