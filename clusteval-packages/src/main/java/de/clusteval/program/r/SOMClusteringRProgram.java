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

import de.clusteval.api.IContext;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.cluster.Clustering;
import de.clusteval.context.Context;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is an implementation of Self Organizing Maps using the R-framework
 * implementation of the package <b>kohonen</b> in method <b>som</b>.
 *
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"kohonen"})
public class SOMClusteringRProgram extends AbsoluteDataRProgram {

    /**
     * @param repository
     * @throws RegisterException
     */
    public SOMClusteringRProgram(IRepository repository)
            throws RegisterException {
        super(repository, new File(FileUtils.buildPath(
                repository.getBasePath(IProgram.class),
                "SOMClusteringRProgram.jar")).lastModified(), new File(
                FileUtils.buildPath(repository.getBasePath(IProgram.class),
                        "SOMClusteringRProgram.jar")));
    }

    /**
     * The copy constructor of SOM.
     *
     * @param other
     *              The object to clone.
     *
     * @throws RegisterException
     */
    public SOMClusteringRProgram(SOMClusteringRProgram other) throws RegisterException {
        this(other.repository);
    }

    /*
     * (non-Javadoc)
     *
     * @see program.Program#getAlias()
     */
    @Override
    public String getAlias() {
        return "Self Organizing Maps";
    }

    /*
     * (non-Javadoc)
     *
     * @see program.r.RProgram#getInvocationFormat()
     */
    @Override
    public String getInvocationFormat() {
        return "som(x,keep.data=T,grid=somgrid(%x%,%y%))";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getClusterIdsFromExecResult()
     */
    @Override
    public float[][] getFuzzyCoeffMatrixFromExecResult()
            throws InterruptedException, RException {
        RExpr result = rEngine.eval("result$unit.classif");
        int[] clusterIds = result.asIntegers();
        return Clustering.clusterIdsToFuzzyCoeff(clusterIds);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getCompatibleDataSetFormats()
     */
    @Override
    public Set<IDataSetFormat> getCompatibleDataSetFormats()
            throws UnknownDataSetFormatException, UnknownProviderException {
        return new HashSet<>(DataSetFormat.parseFromString(
                repository, new String[]{"MatrixDataSetFormat"}));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getRunResultFormat()
     */
    @Override
    public IRunResultFormat getRunResultFormat() throws UnknownRunResultFormatException {
        return RunResultFormat.parseFromString(repository,
                "TabSeparatedRunResultFormat");
    }

    @Override
    public IContext getContext() throws UnknownContextException {
        return Context.parseFromString(repository, "ClusteringContext");
    }
}
