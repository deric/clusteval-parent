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

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.cluster.Clustering;
import de.clusteval.context.Context;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.program.Program;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
public class ClusterDPClusteringRProgram extends RelativeDataRProgram {

    /**
     * @param repository
     * @throws RegisterException
     */
    public ClusterDPClusteringRProgram(IRepository repository)
            throws RegisterException {
        super(repository, new File(FileUtils.buildPath(
                repository.getBasePath(Program.class),
                "ClusterDPClusteringRProgram.jar")).lastModified(), new File(
                FileUtils.buildPath(repository.getBasePath(IProgram.class),
                        "ClusterDPClusteringRProgram.jar")));
    }

    /**
     * @param rProgram
     * @throws RegisterException
     */
    public ClusterDPClusteringRProgram(ClusterDPClusteringRProgram rProgram)
            throws RegisterException {
        this(rProgram.repository);
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
            throws RLibraryNotLoadedException, RNotAvailableException, InterruptedException, RException {
        super.beforeExec(dataConfig, programConfig, invocationLine,
                effectiveParams, internalParams);

        // define function for this program
        this.rEngine.eval("clusterdp <- function(dc, k) {"
                + "  d <- as.matrix(x);"
                + "  ND <- nrow(d);"
                + "  rho <- c();"
                + "  for (  i in 1 : ND ) {"
                + "    rho[i]<-0.;"
                + "  };"
                + "  for (  i in 1:(ND-1) ) {"
                + "    for (  j in (i+1):ND ) {"
                + "       e <- exp(-(d[i,j]/dc)*(d[i,j]/dc));"
                + "       rho[i]<-rho[i]+e;"
                + "       rho[j]<-rho[j]+e;"
                + "    };"
                + "  };"
                + "  "
                + "  maxd<-max(max(d));"
                + "  "
                + "  tmp <- sort(rho,decreasing=T, index.return=T);"
                + "  rho.sorted <- tmp$x;"
                + "  ordrho <- tmp$ix;"
                + "  "
                + "  delta <- c();"
                + "  nneigh <- c();"
                + "  "
                + "  delta[ordrho[1]]<--1.;"
                + "  nneigh[ordrho[1]]<-1;"
                + "  "
                + "  for (  ii in 2 : ND ) {"
                + "     delta[ordrho[ii]]<-maxd;"
                + "     for (  jj in 1 : (ii-1) ) {"
                + "       if(d[ordrho[ii],ordrho[jj]]<delta[ordrho[ii]]) {"
                + "          delta[ordrho[ii]]<-d[ordrho[ii],ordrho[jj]];"
                + "          nneigh[ordrho[ii]]<-ordrho[jj];"
                + "       };"
                + "     };"
                + "  };"
                + "  delta[ordrho[1]]<-max(delta);"
                + "  rhodelta <- c();"
                + "  for (i in 1:ND) { rhodelta[i]=rho[i]*delta[i];};"
                + "  ordrhodelta <- sort(rhodelta,decreasing=T, index.return=T);"
                + "  cl <- c();" + "  icl <- c();"
                + "  for (i in 1:ND) {" + "    cl[i]=-1;" + "  };"
                + "  for (i in 1:k) {"
                + "    cl[ordrhodelta$ix[i]] <- i;" + "      icl[i]=i;"
                + "  };" + "  " + "  for (  i in 1 : ND ) {"
                + "    if (cl[ordrho[i]]==-1)"
                + "      cl[ordrho[i]]<-cl[nneigh[ordrho[i]]];"
                + "  };" + "  result <- cl;" + "};" + "return (0);");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getInvocationFormat()
     */
    @Override
    public String getInvocationFormat() {
        return "clusterdp(%dc%, %k%)";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.Program#getAlias()
     */
    @Override
    public String getAlias() {
        return "clusterdp";
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getClusterIdsFromExecResult()
     */
    @Override
    public float[][] getFuzzyCoeffMatrixFromExecResult()
            throws RException, InterruptedException {
        RExpr result = rEngine.eval("result");
        int[] clusterIds = result.asIntegers();
        return Clustering.clusterIdsToFuzzyCoeff(clusterIds);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getCompatibleDataSetFormats()
     */
    @Override
    public Set<IDataSetFormat> getCompatibleDataSetFormats() throws UnknownDataSetFormatException, UnknownProviderException {
        return new HashSet<>(DataSetFormat.parseFromString(
                repository, new String[]{"SimMatrixDataSetFormat"}));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.r.RProgram#getRunResultFormat()
     */
    @Override
    public IRunResultFormat getRunResultFormat()
            throws UnknownRunResultFormatException {
        return RunResultFormat.parseFromString(repository,
                "TabSeparatedRunResultFormat");
    }

    @Override
    public Context getContext() throws UnknownContextException {
        return Context.parseFromString(repository, "ClusteringContext");
    }
}
