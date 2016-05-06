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
package de.clusteval.cluster.paramOptimization;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.opt.ParameterOptimizationException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.opt.ParameterOptimizationRun;
import de.clusteval.utils.RangeCreationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
public class TransClustQuantileParameterOptimizationMethod
        extends
        DivisiveParameterOptimizationMethod {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @param run
     * @param programConfig
     * @param dataConfig
     * @param params
     * @param optimizationCriterion
     * @param terminateCount
     * @param isResume
     * @throws ParameterOptimizationException
     * @throws RegisterException
     */
    public TransClustQuantileParameterOptimizationMethod(final IRepository repo,
            final boolean register, final long changeDate, final File absPath,
            ParameterOptimizationRun run, IProgramConfig programConfig,
            IDataConfig dataConfig, List<IProgramParameter<?>> params,
            ClusteringEvaluation optimizationCriterion, int terminateCount,
            boolean isResume) throws ParameterOptimizationException,
                                     RegisterException {
        super(repo, false, changeDate, absPath, run, programConfig, dataConfig,
                params, optimizationCriterion, terminateCount, isResume);

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor for this method.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public TransClustQuantileParameterOptimizationMethod(
            final TransClustQuantileParameterOptimizationMethod other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.paramOptimization.DivisiveParameterOptimizationMethod#
     * initParameterValues()
     */
    @Override
    public void initParameterValues() throws ParameterOptimizationException {
        this.parameterValues = new HashMap<>();
        currentPos = new HashMap<>();
        try {
            RelativeDataSet dataSet = (RelativeDataSet) (this.dataConfig
                    .getDatasetConfig().getDataSet().getInStandardFormat());
            dataSet.loadIntoMemory();
            double[] quantiles = dataSet.getDataSetContent().getQuantiles(
                    iterationPerParameter[0]);
            dataSet.unloadFromMemory();

            String[] quantileStr = new String[quantiles.length];
            for (int i = 0; i < quantileStr.length; i++) {
                quantileStr[i] = quantiles[i] + "";
            }

            IProgramParameter<?> tParam = this.params.get(0);
            parameterValues.put(tParam, quantileStr);
            currentPos.put(tParam, -1);
        } catch (RangeCreationException e1) {
            throw new ParameterOptimizationException(
                    "Could not create parameter range for the next iteration: "
                    + e1.getMessage());
        } catch (IllegalArgumentException | IOException | InvalidDataSetFormatException e) {
            e.printStackTrace();
        }
    }
}
