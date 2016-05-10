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
package de.clusteval.data.dataset.format;

import com.google.common.util.concurrent.Striped;
import de.clusteval.api.FormatVersion;
import de.clusteval.api.Matrix;
import de.clusteval.api.Precision;
import de.clusteval.api.data.DataSetFormatParser;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.TextFileParser.OUTPUT_MODE;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.wiwie.wiutils.utils.parse.SimFileMatrixParser;
import de.wiwie.wiutils.utils.parse.SimFileParser.SIM_FILE_FORMAT;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import de.clusteval.api.data.ConvConf;

/**
 * @author Christian Wiwie
 */
@FormatVersion(version = 1)
public class SimMatrixDataSetFormatParser extends DataSetFormatParser {

    private final Striped<Lock> locks;

    public SimMatrixDataSetFormatParser() {
        locks = Striped.lock(5);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.format.DataSetFormat#parseDataSet(data.dataset.DataSet)
     */
    @Override
    public SimilarityMatrix parse(IDataSet dataSet, Precision precision)
            throws IllegalArgumentException, IOException,
                   InvalidDataSetFormatException {

        Lock l = locks.get(dataSet.getAbsolutePath());
        l.lock();
        try {
            // TODO: symmetry
            final SimFileMatrixParser p;

            try {
                p = new SimFileMatrixParser(dataSet.getAbsolutePath(),
                        SIM_FILE_FORMAT.MATRIX_HEADER, null, OUTPUT_MODE.BURST,
                        SIM_FILE_FORMAT.MATRIX_HEADER, precision);
                p.process();
                return p.getSimilarities();
            } catch (IOException e) {
                throw new InvalidDataSetFormatException(e.getMessage());
            }
        } finally {
            l.unlock();
        }
    }

    @Override
    public IDataSet convertToStandardFormat(IDataSet dataSet, InputToStd config)
            throws IOException, InvalidDataSetFormatException, RegisterException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.dataset.format.DataSetFormatParser#convertToThisFormat
     * (de.clusteval.data.dataset.DataSet,
     * de.clusteval.data.dataset.format.DataSetFormat,
     * de.clusteval.data.dataset.format.ConversionConfiguration)
     */
    @Override
    public IDataSet convertToThisFormat(IDataSet dataSet,
            IDataSetFormat dataSetFormat, ConvConf config)
            throws IOException, InvalidDataSetFormatException,
                   RegisterException {
        return null;
    }

    @Override
    public void writeToFileHelper(IDataSet dataSet, BufferedWriter writer) throws IOException {
        RelativeDataSet absDataSet = (RelativeDataSet) dataSet;
        Matrix matrix = absDataSet.getDataSetContent();

        // create sorted id array
        Map<String, Integer> idMap = matrix.getIds();
        String[] ids = new String[idMap.keySet().size()];
        for (Map.Entry<String, Integer> entry : idMap.entrySet()) {
            ids[entry.getValue()] = entry.getKey();
        }

        // add header line with ids
        for (String id : ids) {
            writer.append("\t");
            writer.append(id);
        }
        // we write it line-was, such that we only keep the string for one line
        // in the memory. otherwise we might encounter problems with huge
        // datasets.
        writer.append(System.getProperty("line.separator"));
        for (int i = 0; i < matrix.getRows(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(ids[i]);
            sb.append("\t");
            for (int j = 0; j < matrix.getColumns(); j++) {
                sb.append(matrix.getSimilarity(i, j) + "\t");
            }
            sb.deleteCharAt(sb.length() - 1);
            if (i < matrix.getRows() - 1) {
                sb.append(System.getProperty("line.separator"));
            }
            writer.append(sb.toString());
        }
    }
}
