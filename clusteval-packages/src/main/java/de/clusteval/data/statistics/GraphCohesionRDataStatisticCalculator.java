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
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.data.RelativeDataSet;
import java.io.File;
import java.io.IOException;

/**
 * @author Christian Wiwie
 *
 */
public class GraphCohesionRDataStatisticCalculator extends
        DataStatisticRCalculator<GraphCohesionRDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public GraphCohesionRDataStatisticCalculator(IRepository repository,
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
    public GraphCohesionRDataStatisticCalculator(
            final GraphCohesionRDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#calculate()
     */
    @Override
    protected GraphCohesionRDataStatistic calculateResultHelper(
            final IRengine rEngine)
            throws IllegalArgumentException, IOException, InvalidDataSetFormatVersionException,
                   RegisterException,
                   UnknownDataSetFormatException, InterruptedException, RException {

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

        simMatrix.scaleBy(simMatrix.getMaxValue());

        rEngine.assign("simMatrix", simMatrix.toArray());
        rEngine.eval("library('igraph')");
        rEngine.eval("gr <- graph.adjacency(simMatrix,weighted=TRUE)");
        rEngine.eval("gr <- simplify(gr, remove.loops=TRUE, remove.multiple=TRUE)");
        RExpr result = rEngine.eval("graph.cohesion(gr)");
        return new GraphCohesionRDataStatistic(repository, false, changeDate,
                absPath, result.asDouble());
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#writeOutputTo(java.io.File)
     */
    @SuppressWarnings("unused")
    @Override
    protected void writeOutputToHelper(File absFolderPath,
            final IRengine rEngine) {
    }

}
