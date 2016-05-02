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
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 * This class is parent class of all different kind of analyses on a DataConfig.
 * This analyses can be performed unrelated to clustering, since it only
 * requires the dataset (and optionally the goldstandard).
 *
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class DataStatisticRCalculator<T extends DataStatistic> extends DataStatisticCalculator<T> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public DataStatisticRCalculator(IRepository repository, long changeDate,
            File absPath, final IDataConfig dataConfig) throws RegisterException {
        super(repository, changeDate, absPath, dataConfig);
    }

    /**
     * The copy constructor of data statistic calculators.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public DataStatisticRCalculator(final DataStatisticRCalculator<T> other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.statistics.DataStatisticCalculator#calculateResult()
     */
    @Override
    protected final T calculateResult() throws DataStatisticCalculateException {
        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            try {

                return calculateResultHelper(rEngine);

            } catch (IncompatibleDataConfigDataStatisticException |
                    UnknownGoldStandardFormatException | UnknownDataSetFormatException |
                    IllegalArgumentException | IOException | InvalidDataSetFormatVersionException |
                    RegisterException | RException | InterruptedException e) {
                throw new DataStatisticCalculateException(e);
            } finally {
                rEngine.clear();
            }
        } catch (InterruptedException | RException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    protected abstract T calculateResultHelper(final IRengine rEngine)
            throws IncompatibleDataConfigDataStatisticException,
                   UnknownGoldStandardFormatException, UnknownDataSetFormatException,
                   IllegalArgumentException, IOException,
                   InvalidDataSetFormatVersionException, RegisterException,
                   RException, InterruptedException;

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.utils.StatisticCalculator#writeOutputTo(java.io.File)
     */
    @Override
    public final void writeOutputTo(File absFolderPath) throws RException, InterruptedException {
        IRengine rEngine = repository.getRengineForCurrentThread();
        writeOutputToHelper(absFolderPath, rEngine);
        rEngine.clear();
    }

    protected abstract void writeOutputToHelper(File absFolderPath,
            final IRengine rEngine) throws RException, InterruptedException;
}
