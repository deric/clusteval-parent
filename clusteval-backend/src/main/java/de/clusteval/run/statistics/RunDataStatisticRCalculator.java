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
package de.clusteval.run.statistics;

import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.InternalAttributeException;
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
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.stats.RunDataStatistic;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.statistics.IncompatibleDataConfigDataStatisticException;
import de.clusteval.data.statistics.RunDataStatisticCalculateException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.run.RunException;
import de.clusteval.run.result.AnalysisRunResultException;
import de.clusteval.utils.InvalidConfigurationFileException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class RunDataStatisticRCalculator<T extends RunDataStatistic> extends RunDataStatisticCalculator<T> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param uniqueRunIdentifiers
     * @param uniqueDataIdentifiers
     * @throws RegisterException
     */
    public RunDataStatisticRCalculator(IRepository repository, long changeDate,
            File absPath, final List<String> uniqueRunIdentifiers,
            final List<String> uniqueDataIdentifiers) throws RegisterException {
        super(repository, changeDate, absPath, uniqueRunIdentifiers,
                uniqueDataIdentifiers);
    }

    /**
     * The copy constructor of R run data statistic calculators.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RunDataStatisticRCalculator(
            final RunDataStatisticRCalculator<T> other)
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
    protected final T calculateResult()
            throws RunDataStatisticCalculateException {
        try {
            try {
                IRengine rEngine = repository.getRengineForCurrentThread();
                try {
                    try {
                        return calculateResultHelper(rEngine);
                    } catch (REXPMismatchException e) {
                        // handle this type of exception as an REngineException
                        throw new RException(rEngine, e.getMessage());
                    }
                } catch (REngineException e) {
                    this.log.warn("R-framework ("
                            + this.getClass().getSimpleName() + "): "
                            + rEngine.getLastError());
                    throw e;
                } finally {
                    rEngine.clear();
                }
            } catch (RserveException e) {
                throw new RNotAvailableException(e.getMessage());
            }
        } catch (Exception e) {
            throw new RunDataStatisticCalculateException(e);
        }
    }

    protected abstract T calculateResultHelper(final IRengine rEngine)
            throws IncompatibleDataConfigDataStatisticException,
                   UnknownGoldStandardFormatException,
                   IllegalArgumentException, IOException,
                   InvalidDataSetFormatException, ConfigurationException,
                   GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, UnknownRunResultFormatException,
                   InvalidRunModeException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownProgramParameterException, InternalAttributeException,
                   InvalidConfigurationFileException,
                   RepositoryAlreadyExistsException, InvalidRepositoryException,
                   NoRepositoryFoundException, GoldStandardNotFoundException,
                   InvalidOptimizationParameterException, RunException,
                   UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   AnalysisRunResultException, RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, RegisterException,
                   NoDataSetException,
                   RunResultParseException,
                   REngineException, REXPMismatchException;
}
