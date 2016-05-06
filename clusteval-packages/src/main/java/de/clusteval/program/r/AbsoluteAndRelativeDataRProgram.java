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

import de.clusteval.api.Matrix;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.data.AbsoluteDataSet;
import de.clusteval.api.data.DataMatrix;
import de.clusteval.api.data.RelativeDataSet;
import java.io.File;
import java.util.Map;

/**
 * This class represents R programs, which are compatible to relative and
 * absolute datasets.
 *
 * @author Christian Wiwie
 *
 */
public abstract class AbsoluteAndRelativeDataRProgram extends RProgram {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public AbsoluteAndRelativeDataRProgram(IRepository repository,
            long changeDate, File absPath) throws RegisterException {
        super(repository, changeDate, absPath);
    }

    /**
     *
     * @param other The object to clone.
     *
     * @throws RegisterException
     */
    public AbsoluteAndRelativeDataRProgram(AbsoluteAndRelativeDataRProgram other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.program.r.RProgram#extractDataSetContent(de.clusteval.data
     * .DataConfig)
     */
    @Override
    public Object extractDataSetContent(IDataConfig dataConfig) {
        boolean absoluteData = dataConfig.getDatasetConfig().getDataSet()
                .getOriginalDataSet() instanceof AbsoluteDataSet;
        Object content;
        if (absoluteData) {
            AbsoluteDataSet dataSet = (AbsoluteDataSet) (dataConfig
                    .getDatasetConfig().getDataSet().getOriginalDataSet());

            DataMatrix dataMatrix = dataSet.getDataSetContent();
            this.ids = dataMatrix.getIds();
            this.x = dataMatrix.getData();
            content = dataMatrix;
        } else {
            RelativeDataSet dataSet = (RelativeDataSet) (dataConfig
                    .getDatasetConfig().getDataSet().getInStandardFormat());
            Matrix simMatrix = dataSet.getDataSetContent();
            this.ids = dataSet.getIds().toArray(new String[0]);
            this.x = simMatrix.toArray();
            content = simMatrix;
        }
        return content;
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
            Map<String, String> internalParams)
            throws RLibraryNotLoadedException, RException,
                   RNotAvailableException, InterruptedException {
        super.beforeExec(dataConfig, programConfig, invocationLine,
                effectiveParams, internalParams);

        boolean absoluteData = dataConfig.getDatasetConfig().getDataSet()
                .getOriginalDataSet() instanceof AbsoluteDataSet;
        if (absoluteData) {
            rEngine.assign("x", x);
            rEngine.eval("rownames(x) <- ids");
        } else {
            rEngine.assign("x", x);
            rEngine.eval("x <- max(x)-x");
            this.convertDistancesToAppropriateDatastructure();
        }
    }

    protected void convertDistancesToAppropriateDatastructure() throws RException, InterruptedException {
        rEngine.eval("x <- as.dist(x)");
    }
}
