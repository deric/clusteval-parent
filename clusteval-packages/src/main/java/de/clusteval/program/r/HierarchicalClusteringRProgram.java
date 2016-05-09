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
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.RunResultFormatFactory;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 * This class is an implementation of Hierarchical Clustering using the
 * R-framework implementation of the package <b>stats</b> in method
 * <b>cutree</b>.
 *
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"stats"})
@ServiceProvider(service = IProgram.class)
public class HierarchicalClusteringRProgram extends RelativeDataRProgram {

    @Override
    public String getName() {
        return "Hierarchical Clustering";
    }

    @Override
    public String getInvocationFormat() {
        return "cutree(hclust(x,method=\"%method%\"),k=%k%)";
    }

    @Override
    public float[][] getFuzzyCoeffMatrixFromExecResult()
            throws InterruptedException, RException {
        RExpr result = rEngine.eval("result@.Data");
        int[] clusterIds = result.asIntegers();
        return ClusteringFactory.clusterIdsToFuzzyCoeff(clusterIds);
    }

    @Override
    public Set<IDataSetFormat> getCompatibleDataSetFormats() throws UnknownProviderException {
        return DataSetFormatFactory.parseFromString(repository,
                new String[]{"SimMatrixDataSetFormat"});
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
