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

import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.cluster.Clustering;
import de.clusteval.api.ContextFactory;
import de.clusteval.api.IContext;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.program.Program;
import de.clusteval.api.run.RunResultFormatFactory;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.Set;

/**
 * This class is an implementation of DBSCAN using the R-framework
 * implementation of the package <b>fpc</b> in method <b>dbscan</b>.
 *
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"fpc"})
public class DBSCANClusteringRProgram extends RelativeDataRProgram {

    /**
     * @param repository
     * @throws RegisterException
     */
    public DBSCANClusteringRProgram(IRepository repository)
            throws RegisterException {
        super(repository, new File(FileUtils.buildPath(
                repository.getBasePath(Program.class),
                "DBSCANClusteringRProgram.jar")).lastModified(), new File(
                FileUtils.buildPath(repository.getBasePath(IProgram.class),
                        "DBSCANClusteringRProgram.jar")));
    }

    /**
     * The copy constructor of Spectral clustering.
     *
     * @param other
     *              The object to clone.
     *
     * @throws RegisterException
     */
    public DBSCANClusteringRProgram(DBSCANClusteringRProgram other)
            throws RegisterException {
        this(other.repository);
    }

    /*
     * (non-Javadoc)
     *
     * @see program.Program#getAlias()
     */
    @Override
    public String getAlias() {
        return "DBSCAN";
    }

    /*
     * (non-Javadoc)
     *
     * @see program.r.RProgram#getInvocationFormat()
     */
    @Override
    public String getInvocationFormat() {
        return "dbscan(x,MinPts=%MinPts%,eps=%eps%)";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getClusterIdsFromExecResult()
     */
    @Override
    public float[][] getFuzzyCoeffMatrixFromExecResult() throws InterruptedException, RException {
        RExpr result = rEngine.eval("result$cluster");
        int[] clusterIds = result.asIntegers();
        return Clustering.clusterIdsToFuzzyCoeff(clusterIds);
    }

    @Override
    public Set<IDataSetFormat> getCompatibleDataSetFormats() throws UnknownProviderException {
        return DataSetFormatFactory.parseFromString(repository, new String[]{"SimMatrixDataSetFormat"});
    }

    @Override
    public IRunResultFormat getRunResultFormat() throws UnknownProviderException {
        return RunResultFormatFactory.parseFromString(repository, "TabSeparatedRunResultFormat");
    }

    @Override
    public IContext getContext() throws UnknownProviderException {
        return ContextFactory.parseFromString(repository, "ClusteringContext");
    }

}
