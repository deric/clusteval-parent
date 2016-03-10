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
/**
 *
 */
package de.clusteval.run.result;

import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.ClusteringParseException;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.api.cluster.quality.ClusteringQualityMeasureValue;
import de.clusteval.api.cluster.quality.ClusteringQualitySet;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.wiwie.wiutils.utils.StringExt;
import de.wiwie.wiutils.utils.parse.TextFileParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
public class ParameterOptimizationResultParser extends TextFileParser {

    protected List<ProgramParameter<?>> parameters = new ArrayList<ProgramParameter<?>>();
    protected List<ClusteringQualityMeasure> qualityMeasures = new ArrayList<ClusteringQualityMeasure>();
    protected ParameterOptimizationMethod method;
    protected ParameterOptimizationRun run;
    protected ParameterOptimizationResult tmpResult;
    protected boolean parseClusterings, storeClusterings;

    /**
     * @param method
     * @param run
     * @param tmpResult
     * @param absFilePath
     * @param keyColumnIds
     * @param valueColumnIds
     * @param parseClusterings
     * @param storeClusterings
     * @throws IOException
     */
    public ParameterOptimizationResultParser(final ParameterOptimizationMethod method,
            final ParameterOptimizationRun run, final ParameterOptimizationResult tmpResult, final String absFilePath,
            int[] keyColumnIds, int[] valueColumnIds, final boolean parseClusterings, final boolean storeClusterings)
            throws IOException {
        super(absFilePath, keyColumnIds, valueColumnIds);
        this.setLockTargetFile(true);
        this.method = method;
        this.run = run;
        this.tmpResult = tmpResult;
        this.parseClusterings = parseClusterings;
        this.storeClusterings = storeClusterings;
    }

    @SuppressWarnings("unused")
    @Override
    protected void processLine(String[] key, String[] value) {
        if (this.currentLine == 0) {
            /*
			 * Parse header line
             */
            // 04.04.2013: added iteration number into first column
            String[] paramSplit = StringExt.split(value[1], ",");
            for (String p : paramSplit) {
                parameters.add(method.getProgramConfig().getParameterForName(p));
            }
            for (int i = 2; i < value.length; i++) {
                String q = value[i];
                for (ClusteringQualityMeasure other : run.getQualityMeasures()) {
                    if (other.getClass().getSimpleName().equals(q)) {
                        qualityMeasures.add(other);
                        break;
                    }
                }
            }
        } else {
            try {
                // duplicated parameter set -> skipped iteration
                if (value[0].contains("*")) {
                    // 13.07.2014: we don't parse duplicated iterations anymore
                    return;
                    // long iterationNumber = Long.valueOf(value[0].replace("*",
                    // ""));
                    // long previousIteration = Long.valueOf(value[1]);
                    // int indexOfIteration = tmpResult.iterationNumbers
                    // .indexOf(previousIteration);
                    //
                    // ParameterSet paramSet = tmpResult.parameterSets
                    // .get(indexOfIteration);
                    // ClusteringQualitySet qualitySet =
                    // tmpResult.parameterSetToQualities
                    // .get(paramSet);
                    //
                    // tmpResult.parameterSets.add(paramSet);
                    // tmpResult.iterationNumbers.add(iterationNumber);
                    //
                    // if (parseClusterings) {
                    // tmpResult.put(iterationNumber, paramSet, qualitySet,
                    // tmpResult.parameterSetToClustering
                    // .get(paramSet));
                    // return;
                    // }
                    //
                    // tmpResult.put(iterationNumber, paramSet, qualitySet);
                }
                long iterationNumber = Long.valueOf(value[0]);
                ParameterSet paramSet = new ParameterSet();
                String[] paramSplit = StringExt.split(value[1], ",");
                for (int pos = 0; pos < paramSplit.length; pos++) {
                    ProgramParameter<?> p = this.parameters.get(pos);
                    paramSet.put(p.getName(), paramSplit[pos]);
                }

                ClusteringQualitySet qualitySet = new ClusteringQualitySet();

                // changed 03.04.2013 this does not necessarily work,
                // because line number not always corresponds to iteration
                // number.
                // added 14.03.2013
                // ensure, that the iteration result file containing the
                // clustering exists
                String iterationId = iterationNumber + "";
                // find the file with the longest name with this prefix
                String clusteringFilePath = new File(
                        this.getAbsoluteFilePath().replace("results.qual.complete", iterationId + ".results.conv"))
                        .getAbsolutePath();
                for (RunResultPostprocessor postprocessor : run.getPostProcessors()) {
                    String newPath = String.format("%s.%s", clusteringFilePath,
                            postprocessor.getClass().getSimpleName());
                    if (new File(newPath).exists()) {
                        clusteringFilePath = newPath;
                    } else {
                        break;
                    }
                }
                // FilenameFilter ff = new FilenameFilter() {
                //
                // /*
                // * (non-Javadoc)
                // *
                // * @see java.io.FilenameFilter#accept(java.io.File,
                // * java.lang.String)
                // */
                // @Override
                // public boolean accept(File dir, String name) {
                // return name.startsWith(clusteringFilePath);
                // }
                // };
                // String[] clusteringFiles = new
                // File(this.getAbsoluteFilePath())
                // .getParentFile().list(ff);
                //
                // String longestFilename;
                // if (clusteringFiles.length > 0) {
                // longestFilename = clusteringFiles[0];
                // for (String name : clusteringFiles)
                // if (name.length() > longestFilename.length())
                // longestFilename = name;
                // } else {
                // longestFilename = new File(this.getAbsoluteFilePath()
                // .replace("results.qual.complete",
                // iterationId + ".results.conv")).getName();
                // }
                //
                // File absFile = new File(FileUtils.buildPath(
                // new File(this.getAbsoluteFilePath()).getParent(),
                // longestFilename)).getAbsoluteFile();

                File absFile = new File(clusteringFilePath);
                // if the corresponding file exists take the qualities for
                // granted
                // if (absFile.exists()) {
                for (int pos = 2; pos < value.length; pos++) {
                    ClusteringQualityMeasure other = this.qualityMeasures.get(pos - 2);
                    qualitySet.put(other, ClusteringQualityMeasureValue.parseFromString(value[pos]));
                }
                // }
                // if the file does not exist, put NT quality values
                // else {
                // for (int pos = 1; pos < value.length; pos++) {
                // ClusteringQualityMeasure other = this.qualityMeasures
                // .get(pos - 1);
                // qualitySet.put(other, ClusteringQualityMeasureValue
                // .getForNotTerminated());
                // }
                // }
                tmpResult.parameterSets.add(paramSet);
                tmpResult.iterationNumbers.add(iterationNumber);

                Clustering clustering = null;
                if (absFile.exists()) {
                    clustering = Clustering.parseFromFile(method.getRepository(), absFile, false).getSecond();
                }

                tmpResult.put(iterationNumber, paramSet, qualitySet, clustering);

                // added 20.08.2012
                if (parseClusterings) {
                    // if (absFile.exists()) {
                    try {
                        if (storeClusterings && clustering != null) {
                            clustering.loadIntoMemory();
                        }
                        return;
                    } catch (ClusteringParseException e) {
                        e.printStackTrace();
                    }
                }
                // }
                // tmpResult.put(iterationNumber, paramSet, qualitySet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.parse.TextFileParser#split(java.lang.String)
     */
    public String[] split(String line) {
        return this.splitLines ? StringExt.split(line, this.inSplit) : new String[]{line};
    }
}
