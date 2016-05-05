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
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
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
public class RangeNormalizationDataPreprocessor extends DataPreprocessor {

    public RangeNormalizationDataPreprocessor() {
        super();
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public RangeNormalizationDataPreprocessor(
            RangeNormalizationDataPreprocessor other) throws RegisterException {
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
                    "The range normalization is only applicable to absolute coordinates");
        }
        final AbsoluteDataSet dataSet = (AbsoluteDataSet) data;
        IRengine rEngine = repository.getRengineForCurrentThread();
        DataSet newDataSet = null;
        try {
            dataSet.loadIntoMemory();
            DataMatrix matrix = dataSet.getDataSetContent();

            rEngine.assign("x", matrix.getData());
            rEngine.eval("x.norm <- t(x) - apply(x,MARGIN=2,min)");
            rEngine.eval("x.norm <- t(x.norm / apply(x.norm,MARGIN=1,max))");
            double[][] result = rEngine.eval("x.norm").asDoubleMatrix();
            DataMatrix newMatrix = new DataMatrix(matrix.getIds(),
                    result);
            newDataSet = dataSet.clone();
            newDataSet.setAbsolutePath(new File(dataSet
                    .getAbsolutePath() + ".rangeNorm"));
            newDataSet.setDataSetContent(newMatrix);
            newDataSet.writeToFile(false);
            newDataSet.unloadFromMemory();
        } catch (InvalidDataSetFormatException | IllegalArgumentException |
                 IOException e1) {
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
        return new HashSet<>(Arrays.asList(new String[]{"MatrixDataSetFormat"}));
    }
}
