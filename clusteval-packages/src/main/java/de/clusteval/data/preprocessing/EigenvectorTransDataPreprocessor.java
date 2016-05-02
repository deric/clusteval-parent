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
package de.clusteval.data.preprocessing;

import de.clusteval.api.data.IDataSet;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.data.dataset.AbsoluteDataSet;
import de.clusteval.data.dataset.DataMatrix;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.framework.repository.Repository;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
public class EigenvectorTransDataPreprocessor extends DataPreprocessor {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public EigenvectorTransDataPreprocessor(Repository repository,
            boolean register, long changeDate, File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public EigenvectorTransDataPreprocessor(DataPreprocessor other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.preprocessing.DataPreprocessor#preprocess(de.clusteval
     * .data.DataConfig)
     */
    @Override
    public IDataSet preprocess(IDataSet data) throws InterruptedException, RException {
        if (data instanceof RelativeDataSet) {
            throw new IllegalArgumentException(
                    "The eigenvector transformation preprocessor is only applicable to absolute coordinates");
        }
        final AbsoluteDataSet dataSet = (AbsoluteDataSet) data;
        try {
            dataSet.loadIntoMemory();
            DataMatrix matrix = dataSet.getDataSetContent();
            IRengine rEngine = repository.getRengineForCurrentThread();
            rEngine.assign("matrix", matrix.getData());
            rEngine.clear();
        } catch (InvalidDataSetFormatVersionException | IllegalArgumentException |
                IOException | UnknownDataSetFormatException e1) {
            e1.printStackTrace();
        } finally {
            dataSet.unloadFromMemory();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.preprocessing.DataPreprocessor#getCompatibleDataSetFormats
     * ()
     */
    @Override
    public Set<String> getCompatibleDataSetFormats() {
        return new HashSet<String>(
                Arrays.asList(new String[]{"MatrixDataSetFormat"}));
    }
}
