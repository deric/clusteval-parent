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
import de.clusteval.context.Context;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.program.Program;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.utils.ArraysExt;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"cluster"})
public class FannyClusteringRProgram extends AbsoluteAndRelativeDataRProgram {

    /**
     * @param repository
     * @throws RegisterException
     */
    public FannyClusteringRProgram(IRepository repository)
            throws RegisterException {
        super(repository, new File(FileUtils.buildPath(
                repository.getBasePath(Program.class),
                "FannyClusteringRProgram.jar")).lastModified(), new File(
                FileUtils.buildPath(repository.getBasePath(IProgram.class),
                        "FannyClusteringRProgram.jar")));
    }

    /**
     * @param rProgram
     * @throws RegisterException
     */
    public FannyClusteringRProgram(FannyClusteringRProgram rProgram) throws RegisterException {
        this(rProgram.repository);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getInvocationFormat()
     */
    @Override
    public String getInvocationFormat() {
        return "fanny(x,k=%k%, memb.exp = %membexp%)";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.Program#getAlias()
     */
    @Override
    public String getAlias() {
        return "fanny";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getClusterIdsFromExecResult()
     */
    @Override
    public float[][] getFuzzyCoeffMatrixFromExecResult()
            throws RException, InterruptedException {
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
    public Set<IDataSetFormat> getCompatibleDataSetFormats()
            throws UnknownDataSetFormatException, UnknownProviderException {
        return new HashSet<>(DataSetFormat.parseFromString(
                repository, new String[]{"MatrixDataSetFormat",
                    "SimMatrixDataSetFormat"}));
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
    public Context getContext() throws UnknownContextException {
        return Context.parseFromString(repository, "ClusteringContext");
    }
}
