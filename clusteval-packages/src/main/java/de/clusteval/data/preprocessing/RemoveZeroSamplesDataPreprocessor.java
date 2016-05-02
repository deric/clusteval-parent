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
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.framework.repository.Repository;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
public class RemoveZeroSamplesDataPreprocessor extends DataPreprocessor {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public RemoveZeroSamplesDataPreprocessor(Repository repository,
            boolean register, long changeDate, File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public RemoveZeroSamplesDataPreprocessor(
            RemoveZeroSamplesDataPreprocessor other) throws RegisterException {
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
        if (data instanceof AbsoluteDataSet) {
            throw new IllegalArgumentException(
                    "The RemoveZeroSamplesPreprocessor is only applicable to relative similarities");
        }
        final RelativeDataSet dataSet = (RelativeDataSet) data;
        IRengine rEngine = repository.getRengineForCurrentThread();
        DataSet newDataSet = null;
        try {
            dataSet.loadIntoMemory();
            SimilarityMatrix matrix = dataSet.getDataSetContent();
            rEngine.assign("matrix", matrix.toArray());
            rEngine.assign("ids", matrix.getIdsArray());
            rEngine.eval("row.names(matrix) <- ids");
            rEngine.eval("naRows <- which(apply(matrix,1,function(x) {all(is.na(x))}))");
            rEngine.eval("zeroRows <- which(apply(matrix,1,function(x) {all(x==0)}))");
            rEngine.eval("matrix <- matrix[-c(naRows,zeroRows),-c(naRows,zeroRows)]");
            double[][] result = rEngine.eval("matrix").asDoubleMatrix();
            String[] ids = rEngine.eval("row.names(matrix)")
                    .asStrings();
            SimilarityMatrix newMatrix = new SimilarityMatrix(ids,
                    result);
            newDataSet = dataSet.clone();
            newDataSet.setAbsolutePath(new File(dataSet
                    .getAbsolutePath() + ".remZeroNA"));
            newDataSet.setDataSetContent(newMatrix);
            newDataSet.writeToFile(false);
            newDataSet.unloadFromMemory();
        } catch (InvalidDataSetFormatVersionException | IllegalArgumentException |
                IOException | UnknownDataSetFormatException e1) {
            e1.printStackTrace();
        } finally {
            rEngine.clear();
            dataSet.unloadFromMemory();
        }
        return newDataSet;
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
                Arrays.asList(new String[]{"SimMatrixDataSetFormat"}));
    }
}
