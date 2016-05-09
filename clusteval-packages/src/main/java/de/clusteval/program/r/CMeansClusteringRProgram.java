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
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.RunResultFormatFactory;
import de.clusteval.utils.ArraysExt;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 * This class is an implementation of C-Means Clustering based on the
 * R-framework implementation in the cmeans() method which is contained in the
 * e1071 library.
 *
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"e1071"})
@ServiceProvider(service = IProgram.class)
public class CMeansClusteringRProgram extends AbsoluteAndRelativeDataRProgram {

    public CMeansClusteringRProgram() {
        super();
    }

    /**
     * @param repository
     * @throws RegisterException
     */
    public CMeansClusteringRProgram(IRepository repository)
            throws RegisterException {
        super(repository, new File(FileUtils.buildPath(
                repository.getBasePath(IProgram.class),
                "CMeansClusteringRProgram.jar")).lastModified(), new File(
                FileUtils.buildPath(repository.getBasePath(IProgram.class),
                        "CMeansClusteringRProgram.jar")));
    }

    /**
     * The copy constructor of C-Means clustering.
     *
     * @param other
     *              The object to clone.
     *
     * @throws RegisterException
     */
    public CMeansClusteringRProgram(CMeansClusteringRProgram other)
            throws RegisterException {
        this(other.repository);
    }

    @Override
    public String getName() {
        return "c-Means";
    }

    /*
     * (non-Javadoc)
     *
     * @see program.r.RProgram#getInvocationFormat()
     */
    @Override
    public String getInvocationFormat() {
        return "cmeans(x,centers=(%k%),m=%m%)";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getClusterIdsFromExecResult()
     */
    @Override
    public float[][] getFuzzyCoeffMatrixFromExecResult() throws InterruptedException, RException {
        RExpr result = rEngine.eval("result$membership");
        double[][] fuzzyCoeffs = result.asDoubleMatrix();
        return ArraysExt.toFloatArray(fuzzyCoeffs);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getCompatibleDataSetFormats()
     */
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

}
