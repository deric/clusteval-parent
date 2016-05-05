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
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.stats.DataStatisticCalculator;
import de.clusteval.api.stats.StatisticCalculateException;
import java.io.File;
import java.io.IOException;

/**
 * @author Christian Wiwie
 *
 */
public class NumberOfSamplesDataStatisticCalculator
        extends
        DataStatisticCalculator<NumberOfSamplesDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public NumberOfSamplesDataStatisticCalculator(IRepository repository,
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
    public NumberOfSamplesDataStatisticCalculator(
            final NumberOfSamplesDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#calculate()
     */
    @Override
    protected NumberOfSamplesDataStatistic calculateResult()
            throws StatisticCalculateException {
        try {
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

            return new NumberOfSamplesDataStatistic(repository, false,
                    changeDate, absPath, simMatrix.getIds().size());
        } catch (IllegalArgumentException | IOException | InvalidDataSetFormatException | RegisterException e) {
            throw new StatisticCalculateException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatisticCalculator#writeOutputTo(java.io.File)
     */
    @Override
    public void writeOutputTo(File absFolderPath) {
    }

}
