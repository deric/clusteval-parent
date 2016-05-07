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
package de.clusteval.program.r;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.data.AbsoluteDataSet;
import de.clusteval.api.data.DataMatrix;
import java.io.File;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public abstract class AbsoluteDataRProgram extends RProgram {

    /**
     * @param repository the repository this program should be registered at.
     * @param changeDate The change date of this program is used for equality
     *                   checks.
     * @param absPath    The absolute path of this program.
     * @throws RegisterException
     */
    public AbsoluteDataRProgram(IRepository repository, long changeDate,
            File absPath) throws RegisterException {
        super(repository, changeDate, absPath);
    }

    /**
     * The copy constructor for rprograms.
     *
     * @param rProgram The object to clone.
     * @throws RegisterException
     */
    public AbsoluteDataRProgram(final AbsoluteDataRProgram rProgram)
            throws RegisterException {
        super(rProgram);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.program.r.RProgram#beforeExec(de.clusteval.data.DataConfig,
     * de.clusteval.program.ProgramConfig, java.lang.String[], java.util.Map,
     * java.util.Map)
     */
    @Override
    public void beforeExec(IDataConfig dataConfig,
            IProgramConfig programConfig, String[] invocationLine,
            Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws RException,
                                                       RLibraryNotLoadedException, RNotAvailableException,
                                                       InterruptedException {
        super.beforeExec(dataConfig, programConfig, invocationLine,
                effectiveParams, internalParams);

        rEngine.assign("x", x);
        rEngine.eval("rownames(x) <- ids");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.program.r.RProgram#extractDataSetContent(de.clusteval.data
     * .DataConfig)
     */
    @Override
    public DataMatrix extractDataSetContent(IDataConfig dataConfig) {
        AbsoluteDataSet dataSet = (AbsoluteDataSet) (dataConfig
                .getDatasetConfig().getDataSet().getOriginalDataSet());
        DataMatrix dataMatrix = dataSet.getDataSetContent();
        this.ids = dataSet.getIds().toArray(new String[0]);
        this.x = dataMatrix.getData();
        return dataMatrix;
    }
}