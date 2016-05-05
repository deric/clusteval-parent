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

import de.clusteval.api.data.AbsoluteDataSet;
import de.clusteval.api.data.DataMatrix;
import de.clusteval.api.data.DataPreprocessor;
import de.clusteval.api.data.DataSet;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 * Normalize every feature of the dataset (columns of the data matrix) to values
 * between 0 and 1.
 *
 * @author Christian Wiwie
 *
 */
@ServiceProvider(service = DataPreprocessor.class)
public class VarianceNormalizationDataPreprocessor extends DataPreprocessor {

    public VarianceNormalizationDataPreprocessor() {
        super();
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public VarianceNormalizationDataPreprocessor(
            VarianceNormalizationDataPreprocessor other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
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
                    "The variance normalization is only applicable to absolute coordinates");
        }
        final AbsoluteDataSet dataSet = (AbsoluteDataSet) data;
        IRengine rEngine = repository.getRengineForCurrentThread();
        DataSet newDataSet = null;
        try {
            dataSet.loadIntoMemory();
            DataMatrix matrix = dataSet.getDataSetContent();
            rEngine.assign("x", matrix.getData());
            rEngine.eval("x.norm <- t(t(x)/sqrt(apply(x,MARGIN=2,var)))");
            double[][] result = rEngine.eval("x.norm").asDoubleMatrix();
            DataMatrix newMatrix = new DataMatrix(matrix.getIds(),
                    result);
            newDataSet = dataSet.clone();
            newDataSet.setAbsolutePath(new File(dataSet
                    .getAbsolutePath() + ".varNorm"));
            newDataSet.setDataSetContent(newMatrix);
            newDataSet.writeToFile(false);
            newDataSet.unloadFromMemory();
            rEngine.clear();
        } catch (InvalidDataSetFormatVersionException | IllegalArgumentException |
                 IOException | UnknownDataSetFormatException e1) {
            e1.printStackTrace();
        } finally {
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
        return new HashSet<>(Arrays.asList(new String[]{"MatrixDataSetFormat"}));
    }
}
