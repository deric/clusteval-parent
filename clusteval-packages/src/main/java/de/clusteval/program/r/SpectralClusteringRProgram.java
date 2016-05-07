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
package de.clusteval.program.r;

import de.clusteval.api.ContextFactory;
import de.clusteval.api.IContext;
import de.clusteval.api.cluster.ClusteringFactory;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.RunResultFormatFactory;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * This class is an implementation of Spectral Clustering using the R-framework
 * implementation of the package <b>kernlab</b> in method <b>specc</b>.
 *
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"kernlab"})
public class SpectralClusteringRProgram extends AbsoluteAndRelativeDataRProgram {

    /**
     * @param repository
     * @throws RegisterException
     */
    public SpectralClusteringRProgram(IRepository repository)
            throws RegisterException {
        super(repository, new File(FileUtils.buildPath(
                repository.getBasePath(IProgram.class),
                "SpectralClusteringRProgram.jar")).lastModified(), new File(
                FileUtils.buildPath(repository.getBasePath(IProgram.class),
                        "SpectralClusteringRProgram.jar")));
    }

    /**
     * The copy constructor of Spectral clustering.
     *
     * @param other
     *              The object to clone.
     *
     * @throws RegisterException
     */
    public SpectralClusteringRProgram(SpectralClusteringRProgram other)
            throws RegisterException {
        this(other.repository);
    }

    /*
     * (non-Javadoc)
     *
     * @see program.Program#getName()
     */
    @Override
    public String getName() {
        return "Spectral Clustering";
    }

    /*
     * (non-Javadoc)
     *
     * @see program.r.RProgram#getInvocationFormat()
     */
    @Override
    public String getInvocationFormat() {
        return "{ for (i in 1:10) {tryCatch({"
                + "resultNew <- specc(x,centers=%k%);"
                + "if (sum(resultNew@withinss) < minWithinss) {"
                + "resultTmp <<- resultNew;"
                + "minWithinss <<- sum(resultNew@withinss);" + "};});" + "};"
                + "resultTmp}";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getClusterIdsFromExecResult()
     */
    @Override
    public float[][] getFuzzyCoeffMatrixFromExecResult() throws RException, InterruptedException {
        RExpr result = rEngine.eval("result@.Data");
        int[] clusterIds = result.asIntegers();
        return ClusteringFactory.clusterIdsToFuzzyCoeff(clusterIds);
    }

    @Override
    public Set<IDataSetFormat> getCompatibleDataSetFormats() throws UnknownProviderException {
        return DataSetFormatFactory.parseFromString(repository,
                new String[]{"MatrixDataSetFormat", "SimMatrixDataSetFormat"});
    }

    @Override
    public IRunResultFormat getRunResultFormat() throws UnknownProviderException {
        return RunResultFormatFactory.parseFromString(repository, "TabSeparatedRunResultFormat");
    }

    @Override
    public IContext getContext() throws UnknownProviderException {
        return ContextFactory.parseFromString(repository, "ClusteringContext");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.AbsoluteAndRelativeDataRProgram#
     * convertDistancesToAppropriateDatastructure()
     */
    @Override
    protected void convertDistancesToAppropriateDatastructure() throws RException, InterruptedException {
        rEngine.eval("x <- as.kernelMatrix(x)");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.program.r.AbsoluteAndRelativeDataRProgram#beforeExec(de.
     * clusteval.data.DataConfig, de.clusteval.program.ProgramConfig,
     * java.lang.String[], java.util.Map, java.util.Map)
     */
    @Override
    public void beforeExec(IDataConfig dataConfig,
            IProgramConfig programConfig, String[] invocationLine,
            Map<String, String> effectiveParams,
            Map<String, String> internalParams)
            throws RLibraryNotLoadedException, RException,
                   RNotAvailableException, InterruptedException {
        super.beforeExec(dataConfig, programConfig, invocationLine,
                effectiveParams, internalParams);
        rEngine.eval("minWithinss <- .Machine$double.xmax");
    }
}
