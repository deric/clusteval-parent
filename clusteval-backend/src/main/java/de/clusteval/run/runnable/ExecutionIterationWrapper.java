/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.run.runnable;

import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.opt.ParameterSet;
import de.clusteval.api.run.IterationWrapper;
import de.clusteval.run.result.ClusteringRunResult;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ExecutionIterationWrapper extends IterationWrapper {

    /**
     * A temporary variable holding a file object pointing to the absolute path
     * of the current clustering output file during execution of the runnable
     */
    private File clusteringResultFile;

    /**
     * A temporary variable holding a file object pointing to the absolute path
     * of the current clustering quality output file during execution of the
     * runnable
     */
    private File resultQualityFile;

    /**
     * This number indicates the current iteration performed by the runnable
     * object.
     *
     * <p>
     * This is only larger than 1, if we are in PARAMETER_OPTIMIZATION mode.
     * Then the optimization method will determine, how often we iterate in
     * total and this attribute will be increased by the runnable after every
     * iteration.
     */
    private int optId;

    /**
     * A map containing the parameters of {@link #runParams} and additionally
     * internal parameters like file paths that are used throughout execution of
     * this runnable.
     */
    final private Map<String, String> effectiveParams;

    /**
     * The internal parameters are parameters, that cannot be directly
     * influenced by the user, e.g. the absolute input or output path.
     */
    final protected Map<String, String> internalParams;

    protected ClusteringRunResult clusteringRunResult;

    protected ClusteringRunResult convertedClusteringRunResult;

    protected ParameterSet parameterSet;

    protected IProgramConfig programConfig;

    protected String[] invocation;

    public String[] getInvocation() {
        return invocation;
    }

    public void setInvocation(String[] invocation) {
        this.invocation = invocation;
    }

    /**
     *
     */
    public ExecutionIterationWrapper() {
        super();
        this.internalParams = new HashMap<>();
        this.effectiveParams = new HashMap<>();
    }

    protected File getClusteringResultFile() {
        return clusteringResultFile;
    }

    protected void setClusteringResultFile(File clusteringResultFile) {
        this.clusteringResultFile = clusteringResultFile;
    }

    protected File getResultQualityFile() {
        return resultQualityFile;
    }

    protected void setResultQualityFile(File resultQualityFile) {
        this.resultQualityFile = resultQualityFile;
    }

    public int getOptId() {
        return optId;
    }

    public void setOptId(int optId) {
        this.optId = optId;
    }

    protected Map<String, String> getEffectiveParams() {
        return effectiveParams;
    }

    protected Map<String, String> getInternalParams() {
        return internalParams;
    }

    protected ClusteringRunResult getClusteringRunResult() {
        return clusteringRunResult;
    }

    protected ClusteringRunResult getConvertedClusteringRunResult() {
        return convertedClusteringRunResult;
    }

    protected void setClusteringRunResult(
            ClusteringRunResult clusteringRunResult) {
        this.clusteringRunResult = clusteringRunResult;
    }

    public IProgramConfig getProgramConfig() {
        return programConfig;
    }

    public void setProgramConfig(IProgramConfig programConfig) {
        this.programConfig = programConfig;
    }

    protected void setConvertedClusteringRunResult(
            ClusteringRunResult clusteringRunResult) {
        this.convertedClusteringRunResult = clusteringRunResult;
    }

    protected ParameterSet getParameterSet() {
        return parameterSet;
    }

    protected void setParameterSet(ParameterSet parameterSet) {
        this.parameterSet = parameterSet;
    }
}
