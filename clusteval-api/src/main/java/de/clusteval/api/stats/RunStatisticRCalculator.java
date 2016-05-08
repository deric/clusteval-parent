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
package de.clusteval.api.stats;

import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.exceptions.InvalidConfigurationFileException;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;

/**
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class RunStatisticRCalculator<T extends RunStatistic> extends RunStatisticCalculator<T> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param uniqueRunIdentifiers
     * @throws RegisterException
     */
    public RunStatisticRCalculator(IRepository repository, long changeDate,
            File absPath, final String uniqueRunIdentifiers)
            throws RegisterException {
        super(repository, changeDate, absPath, uniqueRunIdentifiers);
    }

    /**
     * The copy constructor of run statistic calculators.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RunStatisticRCalculator(final RunStatisticRCalculator<T> other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.statistics.RunDataStatisticCalculator#calculateResult()
     */
    @Override
    protected final T calculateResult() throws RunStatisticCalculateException {
        try {

            IRengine rEngine = repository.getRengineForCurrentThread();
            try {

                return calculateResultHelper(rEngine);
            } catch (RException e) {
                this.log.warn("R-framework ("
                        + this.getClass().getSimpleName() + "): "
                        + rEngine.getLastError());
                throw e;
            } finally {
                rEngine.clear();
            }
        } catch (Exception e) {
            throw new RunStatisticCalculateException(e);
        }
    }

    protected abstract T calculateResultHelper(final IRengine rEngine)
            throws IncompatibleDataConfigDataStatisticException,
                   UnknownGoldStandardFormatException,
                   IllegalArgumentException, IOException,
                   InvalidDataSetFormatException, ConfigurationException,
                   GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   GoldStandardConfigNotFoundException,
                   UnknownRunResultFormatException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownProgramParameterException, InternalAttributeException,
                   InvalidConfigurationFileException,
                   RepositoryAlreadyExistsException, InvalidRepositoryException,
                   NoRepositoryFoundException, GoldStandardNotFoundException,
                   InvalidOptimizationParameterException, RunException,
                   UnknownProgramTypeException, UnknownRProgramException, IncompatibleParameterOptimizationMethodException,
                   RException, RepositoryConfigurationException, RegisterException,
                   NoDataSetException, RunResultParseException;
}