/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.cluster.paramOptimization;

import de.clusteval.api.opt.ParameterOptimizationMethod;
import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.data.AbsoluteDataSet;
import de.clusteval.api.data.AbsoluteDataSetFormat;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.opt.ParameterSet;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.opt.ParameterOptimizationRun;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * TODO: This parameter optimization method does not support resumption of old
 * values, because the forced parameter set is ignored.
 *
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"cluster"})
public class GapStatisticParameterOptimizationMethod extends ParameterOptimizationMethod {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @param run                   The run this method belongs to.
     * @param programConfig         The program configuration this method was created
     *                              for.
     * @param dataConfig            The data configuration this method was created for.
     * @param params                This list holds the program parameters that are to be
     *                              optimized by the parameter optimization run.
     * @param optimizationCriterion The quality measure used as the optimization
     *                              criterion (see {@link #optimizationCriterion}).
     * @param iterationPerParameter This array holds the number of iterations
     *                              that are to be performed for each optimization parameter.
     * @param isResume              This boolean indiciates, whether the run is a resumption
     *                              of a previous run execution or a completely new execution.
     * @throws RegisterException
     */
    public GapStatisticParameterOptimizationMethod(final IRepository repo,
            final boolean register, final long changeDate, final File absPath,
            ParameterOptimizationRun run, IProgramConfig programConfig,
            IDataConfig dataConfig, List<IProgramParameter<?>> params,
            ClusteringEvaluation optimizationCriterion,
            int iterationPerParameter, boolean isResume)
            throws RegisterException {
        super(repo, false, changeDate, absPath, run, programConfig, dataConfig,
                params, optimizationCriterion, iterationPerParameter, isResume);

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor for this method.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public GapStatisticParameterOptimizationMethod(
            final GapStatisticParameterOptimizationMethod other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.ParameterOptimizationMethod#getNextParameterSet
     * (program.ParameterSet)
     */
    @Override
    public ParameterSet getNextParameterSet(ParameterSet forcedParameterSet)
            throws InterruptedException {

        AbsoluteDataSet dataSet = (AbsoluteDataSet) (dataConfig
                .getDatasetConfig().getDataSet().getOriginalDataSet());

        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            dataSet.loadIntoMemory();
            double[][] coords = dataSet.getDataSetContent().getData();
            List<String> ids = dataSet.getIds();
            rEngine.assign("ids", ids.toArray(new String[0]));
            dataSet.unloadFromMemory();
            rEngine.eval("x <- c()");
            for (int i = 0; i < coords.length; i++) {
                rEngine.assign("x_" + i, coords[i]);
                rEngine.eval("x <- rbind(x, x_" + i + ")");
                rEngine.eval("remove(x_" + i + ")");
            }
            rEngine.eval("rownames(x) <- ids");
            rEngine.eval("library(cluster)");
            rEngine.eval("result <- clusGap(x, FUNcluster=kmeans, K.max=10, B=100)");
            rEngine.eval("result_tab <- cbind(result$Tab,result$Tab[,3]-result$Tab[,4])");
            RExpr result = rEngine
                    .eval("maxSE(f=result_tab[,3],SE.f=result_tab[,4])");
            int noOfClusters = result.asInteger();

            ParameterSet res = new ParameterSet();
            res.put("k", (double) noOfClusters + "");

            return res;

        } catch (IllegalArgumentException | InvalidDataSetFormatException | IOException e) {
            e.printStackTrace();
        } catch (RException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.paramOptimization.ParameterOptimizationMethod#hasNext()
     */
    @Override
    public boolean hasNext() {
        return currentCount < getTotalIterationCount();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.ParameterOptimizationMethod#getTotalIterationCount
     * ()
     */
    @Override
    public int getTotalIterationCount() {
        return 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.paramOptimization.ParameterOptimizationMethod#
     * getCompatibleDataSetFormatBaseClasses()
     */
    @Override
    public List<Class<? extends DataSetFormat>> getCompatibleDataSetFormatBaseClasses() {
        List<Class<? extends DataSetFormat>> result = new ArrayList<>();
        result.add(AbsoluteDataSetFormat.class);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.paramOptimization.ParameterOptimizationMethod#
     * getCompatibleProgramClasses()
     */
    @Override
    public List<String> getCompatibleProgramNames() {
        List<String> result = new ArrayList<>();
        result.add("FannyClusteringRProgram");
        result.add("KMeansClusteringRProgram");
        result.add("HierarchicalClusteringRProgram");
        result.add("SpectralClusteringRProgram");
        return result;
    }
}
