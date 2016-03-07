/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
package de.clusteval.utils.plot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import de.wiwie.wiutils.utils.ArraysExt;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.AbsoluteDataSet;
import de.clusteval.data.dataset.DataMatrix;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.framework.repository.MyRengine;
import de.clusteval.run.result.ParameterOptimizationResult;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class Plotter {

	/**
	 * @param result
	 * @throws InterruptedException
	 */
	public static void plotParameterOptimizationResult(
			final ParameterOptimizationResult result)
			throws InterruptedException {

		MyRengine rEngine;
		try {
			rEngine = result.getRepository().getRengineForCurrentThread();
			try {
				rEngine.eval("Sys.setlocale(category='LC_NUMERIC',locale='C')");
				/*
				 * Define functions
				 */
				rEngine.eval("getDensity <- function(x) {"
						+ "	return (c(strsplit(x=as.character(x[1]),split=',')[[1]][1],x[-1]))}");

				rEngine.eval("plotDensityVSQuality <- function(title, path, densityParam) {"
						+ "data <- t(apply(read.table(path, sep='\t',header=TRUE), 1, getDensity));"
						+ "svg(filename=paste(path,'.svg',sep=''));"
						// + "par(cex=.5);"
						+ "matplot(x=data[,1],data[,-1],xlab=densityParam,ylab='Clustering quality',main=title,type='p',pch=20,col=1:6);"
						+ "legend('topleft',legend=colnames(data)[-1],col=1:6,pch=20);"
						+ "dev.off();"
						+ "png(filename=paste(path,'.png',sep=''));"
						// + "par(cex=.5);"
						+ "matplot(x=data[,1],data[,-1],xlab=densityParam,ylab='Clustering quality',main=title,type='p',pch=20,col=1:6);"
						+ "legend('topleft',legend=colnames(data)[-1],col=1:6,pch=20);"
						+ "dev.off()" + "}");
				rEngine.eval("plotDensityVSQuality("
						+ "'"
						+ result.getMethod().getProgramConfig().getProgram()
								.getMajorName()
						+ " vs. "
						+ result.getMethod().getDataConfig().getDatasetConfig()
								.getDataSet().getFullName() + "'," + "'"
						+ result.getAbsolutePath() + "', " + "'"
						+ result.getMethod().getPlotDensityParameter() + "')");
			} finally {
				rEngine.clear();
			}
		} catch (RserveException e) {
		}

	}

	/**
	 * This method creates a file containing isoMDS coordinates using the
	 * similarity matrix file of the given data configuration.
	 * 
	 * @param dataConfig
	 * @throws UnknownDataSetFormatException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws REngineException
	 * @throws InterruptedException
	 */
	public static void assessAndWriteIsoMDSCoordinates(
			final DataConfig dataConfig) throws UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IllegalArgumentException,
			IOException, REngineException, InterruptedException {

		try {
			MyRengine rEngine = dataConfig.getRepository()
					.getRengineForCurrentThread();
			try {
				DataSet absStandard = dataConfig.getDatasetConfig()
						.getDataSet().getInStandardFormat();

				String newPath = dataConfig.getDatasetConfig().getDataSet()
						.getOriginalDataSet().getAbsolutePath()
						+ ".isoMDS";
				if (new File(newPath).exists())
					return;

				boolean wasLoaded = absStandard.isInMemory();
				if (!wasLoaded)
					absStandard.loadIntoMemory();
				SimilarityMatrix simMatrix;
				double[][] sims;
				String[] ids;
				try {
					simMatrix = (SimilarityMatrix) dataConfig
							.getDatasetConfig().getDataSet()
							.getInStandardFormat().getDataSetContent();
					sims = simMatrix.toArray();
					ids = new String[simMatrix.getIds().size()];
					for (Map.Entry<String, Integer> entry : simMatrix.getIds()
							.entrySet())
						ids[entry.getValue()] = entry.getKey();
				} finally {
					if (!wasLoaded)
						absStandard.unloadFromMemory();
				}
				rEngine.assign("x",
						ArraysExt.subtract(ArraysExt.max(sims), sims, true));
				rEngine.assign("labels", ids);
				rEngine.eval("rownames(x) <- labels;");
				rEngine.eval("colnames(x) <- labels;");
				rEngine.eval("dists <- as.dist(x+0.00000000000001);");
				rEngine.eval("library(MASS);");
				rEngine.eval("iso <- isoMDS(dists)$points;");
				double[][] isoMDS = rEngine.eval("iso").asDoubleMatrix();

				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < ids.length; i++) {
					sb.append(ids[i]);
					sb.append("\t");
					double[] row = isoMDS[i];
					for (int c = 0; c < row.length; c++) {
						sb.append(row[c] + "");
						sb.append("\t");
					}
					sb.deleteCharAt(sb.length() - 1);
					sb.append(System.getProperty("line.separator"));
				}
				sb.deleteCharAt(sb.length() - 1);

				BufferedWriter bw = new BufferedWriter(new FileWriter(newPath));
				bw.append(sb.toString());
				bw.close();
			} catch (REXPMismatchException e) {
				e.printStackTrace();
			} finally {
				rEngine.clear();
			}
		} catch (RserveException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dataConfig
	 * @throws InvalidDataSetFormatVersionException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws REngineException
	 * @throws UnknownDataSetFormatException
	 * @throws InterruptedException
	 */
	public static void assessAndWritePCACoordinates(final DataConfig dataConfig)
			throws InvalidDataSetFormatVersionException,
			IllegalArgumentException, IOException, REngineException,
			UnknownDataSetFormatException, InterruptedException {

		try {
			MyRengine rEngine = dataConfig.getRepository()
					.getRengineForCurrentThread();
			try {

				double[][] x;
				String[] ids;

				DataSet standard = dataConfig.getDatasetConfig().getDataSet()
						.getOriginalDataSet();
				String newPath = standard.getAbsolutePath() + ".PCA";
				if (new File(newPath).exists())
					return;

				if (standard instanceof AbsoluteDataSet) {
					AbsoluteDataSet absStandard = (AbsoluteDataSet) standard;

					boolean wasLoaded = absStandard.isInMemory();
					if (!wasLoaded)
						absStandard.loadIntoMemory();
					DataMatrix dataMatrix;

					try {
						dataMatrix = absStandard.getDataSetContent();
						x = dataMatrix.getData();
						ids = dataMatrix.getIds();
					} finally {
						if (!wasLoaded)
							absStandard.unloadFromMemory();
					}
				} else {
					RelativeDataSet absStandard = (RelativeDataSet) standard;

					boolean wasLoaded = absStandard.isInMemory();
					if (!wasLoaded)
						absStandard.loadIntoMemory();
					SimilarityMatrix dataMatrix;

					try {
						dataMatrix = absStandard.getDataSetContent();
						x = dataMatrix.toArray();
						ids = dataMatrix.getIdsArray();
					} finally {
						if (!wasLoaded)
							absStandard.unloadFromMemory();
					}

				}

				rEngine.assign("x", x);
				rEngine.assign("labels", ids);
				rEngine.eval("rownames(x) <- labels;");
				rEngine.eval("pca <- prcomp(x)$x;");
				double[][] pca = rEngine.eval("pca").asDoubleMatrix();

				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < ids.length; i++) {
					sb.append(ids[i]);
					sb.append("\t");
					double[] row = pca[i];
					for (int c = 0; c < row.length; c++) {
						sb.append(row[c] + "");
						sb.append("\t");
					}
					sb.deleteCharAt(sb.length() - 1);
					sb.append(System.getProperty("line.separator"));
				}
				sb.deleteCharAt(sb.length() - 1);

				BufferedWriter bw = new BufferedWriter(new FileWriter(newPath));
				bw.append(sb.toString());
				bw.close();
			} catch (REXPMismatchException e) {
				e.printStackTrace();
			} finally {
				rEngine.clear();
			}
		} catch (RserveException e) {
			e.printStackTrace();
		}
	}
}
