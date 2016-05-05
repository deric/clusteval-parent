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
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.IGoldStandard;
import de.clusteval.api.data.IGoldStandardConfig;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.stats.DataStatisticCalculator;
import de.clusteval.utils.ArraysExt;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public class IntraInterDistributionDataStatisticCalculator extends
        DataStatisticCalculator<IntraInterDistributionDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public IntraInterDistributionDataStatisticCalculator(IRepository repository,
            long changeDate, File absPath, final IDataConfig dataConfig)
            throws RegisterException {
        super(repository, changeDate, absPath, dataConfig);
        if (!(RelativeDataSetFormat.class.isAssignableFrom(dataConfig
                .getDatasetConfig().getDataSet().getDataSetFormat().getClass()))) {
            throw new IllegalArgumentException(
                    "Intra inter similarity distribution can only be calculated for relative dataset formats");
        }
    }

    /**
     * The copy constructor for this statistic calculator.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public IntraInterDistributionDataStatisticCalculator(
            final IntraInterDistributionDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#calculate(data.DataConfig)
     */
    @Override
    protected IntraInterDistributionDataStatistic calculateResult()
            throws DataStatisticCalculateException {
        try {
            if (!dataConfig.hasGoldStandardConfig()) {
                throw new IncompatibleDataConfigDataStatisticException(
                        "IntraInterDistribution requires a goldstandard, which the DataConfig "
                        + dataConfig + " does not provide.");
            }

            IGoldStandardConfig goldStandardConfig = dataConfig
                    .getGoldstandardConfig();
            IGoldStandard goldStandard = goldStandardConfig.getGoldstandard();
            IClustering gsClustering;

            gsClustering = goldStandard.getClustering();

            Map<String, Integer> idToClass = new HashMap<>();
            int clId = 0;
            for (Cluster cl : gsClustering) {
                for (ClusterItem item : cl) {
                    idToClass.put(item.getId(), clId);
                }
                clId++;
            }

            goldStandard.unloadFromMemory();

            IDataSetConfig dataSetConfig = dataConfig.getDatasetConfig();
            RelativeDataSet dataSet = (RelativeDataSet) (dataSetConfig
                    .getDataSet().getInStandardFormat());

            if (!dataSet.isInMemory()) {
                dataSet.loadIntoMemory();
            }
            Matrix simMatrix = dataSet.getDataSetContent();
            if (dataSet.isInMemory()) {
                dataSet.unloadFromMemory();
            }

            Pair<double[], int[][]> intraVsInterDistribution = simMatrix
                    .toIntraInterDistributionArray(100, idToClass);

            double[] intraDistr = ArraysExt
                    .toDoubleArray(intraVsInterDistribution.getSecond()[0]);
            double[] interDistr = ArraysExt
                    .toDoubleArray(intraVsInterDistribution.getSecond()[1]);

            double totalSum = ArraysExt.sum(intraDistr)
                    + ArraysExt.sum(interDistr);
            intraDistr = ArraysExt.scaleBy(intraDistr, totalSum);
            interDistr = ArraysExt.scaleBy(interDistr, totalSum);

            lastResult = new IntraInterDistributionDataStatistic(repository,
                    false, changeDate, absPath,
                    intraVsInterDistribution.getFirst(), intraDistr, interDistr);
            return lastResult;
        } catch (IncompatibleDataConfigDataStatisticException |
                 UnknownGoldStandardFormatException | IllegalArgumentException |
                 IOException | InvalidDataSetFormatException | RegisterException e) {
            throw new DataStatisticCalculateException(e);
        }
    }

    @Override
    public void writeOutputTo(final File absFolderPath) throws InterruptedException, RException {
        IRengine rEngine = repository.getRengineForCurrentThread();
        rEngine.eval("plotIntraVsInterDistribution <- function(title, path, xlabels, intraDistr, interDistr) {"
                + "names(intraDistr) <- xlabels;"
                + "names(interDistr) <- xlabels;"
                + "svg(filename=paste(path,'.svg',sep=''));"
                + "barplot(main=title, rbind(intraDistr, interDistr), beside = TRUE,legend = c('intra similarities', 'inter similarities'));"
                + "dev.off()" + "}; return 0;");
        double[] xlabels = this.getStatistic().xlabels;
        double[] intraDistr = this.getStatistic().intraDistribution;
        double[] interDistr = this.getStatistic().interDistribution;
        rEngine.assign("xlabels", xlabels);
        rEngine.assign("intraDistr", intraDistr);
        rEngine.assign("interDistr", interDistr);
        rEngine.eval("plotIntraVsInterDistribution("
                + "'intra vs. inter similiarities "
                + dataConfig.getName()
                + "',path="
                + "'"
                + FileUtils.buildPath(absFolderPath.getAbsolutePath(),
                        dataConfig.getName()) + "_intraVsInter"
                + "',xlabels=xlabels, " + "intraDistr=intraDistr, "
                + "interDistr=interDistr)");
        intraDistr = ArraysExt.scaleBy(intraDistr, 100.0, false);
        intraDistr = ArraysExt.add(intraDistr, 1.0);
        intraDistr = ArraysExt.log(intraDistr, true, 0.0);
        interDistr = ArraysExt.scaleBy(interDistr, 100.0, false);
        interDistr = ArraysExt.add(interDistr, 1.0);
        interDistr = ArraysExt.log(interDistr, true, 0.0);
        rEngine.assign("intraDistr", intraDistr);
        rEngine.assign("interDistr", interDistr);
        rEngine.eval("plotIntraVsInterDistribution("
                + "'intra vs. inter similiarities "
                + dataConfig.getName()
                + "',path="
                + "'"
                + FileUtils.buildPath(absFolderPath.getAbsolutePath(),
                        dataConfig.getName()) + "_intraVsInter_log"
                + "',xlabels=xlabels, " + "intraDistr=intraDistr, "
                + "interDistr=interDistr)");
        rEngine.clear();
    }
}
