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

import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.api.FormatVersion;
import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.Pair;
import de.clusteval.api.Precision;
import de.clusteval.api.data.IConversionConfiguration;
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.data.dataset.AbsoluteDataSet;
import de.clusteval.api.data.DataMatrix;
import de.clusteval.data.dataset.DataSetAttributeParser;
import de.clusteval.data.dataset.RelativeDataSet;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.wiwie.wiutils.utils.parse.TextFileParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
@FormatVersion(version = 1)
public class MatrixDataSetFormatParser extends DataSetFormatParser {

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.format.DataSetFormatParser#convertToStandardFormat(data.
     * dataset.DataSet)
     */
    @Override
    public IDataSet convertToStandardFormat(IDataSet dataSet,
            IConversionInputToStandardConfiguration config)
            throws IOException,
                   RegisterException, UnknownDataSetFormatException,
                   InvalidParameterException, RNotAvailableException,
                   InterruptedException, UnknownProviderException {

        File targetFile = new File(dataSet.getAbsolutePath() + ".SimMatrix");

        RelativeDataSet newDataSet = new RelativeDataSet(
                dataSet.getRepository(), false, System.currentTimeMillis(),
                targetFile, dataSet.getAlias(),
                (RelativeDataSetFormat) DataSetFormat.parseFromString(
                        dataSet.getRepository(), "SimMatrixDataSetFormat"),
                dataSet.getDataSetType(), WEBSITE_VISIBILITY.HIDE);

        if (!targetFile.exists()) {
            this.log.info("Parsing input file coordinates into RAM");
            MatrixParser parser = new MatrixParser(dataSet.getAbsolutePath());
            parser.process();
            List<Pair<String, double[]>> coords = parser.getCoordinates();
            double[][] coordsMatrix = new double[coords.size()][];
            String[] ids = new String[coords.size()];
            for (int i = 0; i < coordsMatrix.length; i++) {
                coordsMatrix[i] = coords.get(i).getSecond();
                ids[i] = coords.get(i).getFirst();
            }

            IDistanceMeasure dist = config.getDistanceMeasureAbsoluteToRelative();

            SimilarityMatrix matrix = null;

            this.log.info("Calculating pairwise distances");
            if (dist.supportsMatrix()) {
                matrix = (SimilarityMatrix) dist.getDistances(config, coordsMatrix);
                matrix.setIds(ids);
                this.log.info("Converting distances to similarities");
                matrix.invert();
            }
            // 31.01.2013: Some measures require R for the
            // getDistances(double[][]) operation. In these cases, the return
            // type is null.
            if (matrix == null) {
                matrix = new SimilarityMatrix(ids, coordsMatrix.length,
                        coordsMatrix.length, config.getSimilarityPrecision(),
                        dist.isSymmetric());
                for (int i = 0; i < matrix.getRows(); i++) {
                    for (int j = i; j < matrix.getColumns(); j++) {
                        matrix.setSimilarity(i, j, dist.getDistance(
                                coords.get(i).getSecond(), coords.get(j)
                                .getSecond()));
                    }
                }
                this.log.info("Converting distances to similarities");
                matrix.invert();
            }

            /*
             * changed 23.09.2012 removed scaling and put max in subtract as
             * first parameter
             */
            if (this.normalize) {
                this.log.info("Normalizing similarities");
                matrix.normalize();
            }

            this.log.info("Writing similarity matrix into file");
            newDataSet.setDataSetContent(matrix);
            newDataSet.writeToFile(false);
            newDataSet.unloadFromMemory();
        }
        return newDataSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.format.DataSetFormatParser#convertToThisFormat(data.dataset
     * .DataSet, data.dataset.format.DataSetFormat)
     */
    @Override
    public IDataSet convertToThisFormat(IDataSet dataSet,
            IDataSetFormat dataSetFormat, IConversionConfiguration config)
            throws InvalidDataSetFormatVersionException {
        throw new InvalidDataSetFormatVersionException(
                "Cannot convert to this format");
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.format.DataSetFormatParser#parse(data.dataset.DataSet)
     */
    @Override
    public DataMatrix parse(IDataSet dataSet, Precision precision)
            throws IOException {
        MatrixParser parser = new MatrixParser(dataSet.getAbsolutePath());
        parser.process();
        List<Pair<String, double[]>> coords = parser.getCoordinates();
        String[] ids = new String[coords.size()];
        double[][] data = new double[coords.size()][];
        for (int i = 0; i < coords.size(); i++) {
            data[i] = coords.get(i).getSecond();
            ids[i] = coords.get(i).getFirst();
        }
        return new DataMatrix(ids, data);
    }

    class MatrixParser extends TextFileParser {

        protected List<Pair<String, double[]>> idToCoordinates;

        /**
         * @param absFilePath
         * @throws IOException
         */
        public MatrixParser(String absFilePath) throws IOException {
            super(absFilePath, new int[0], new int[0]);
            this.setLockTargetFile(true);
            this.idToCoordinates = new ArrayList<>();
        }

        /*
         * (non-Javadoc)
         *
         * @see utils.parse.TextFileParser#checkLine(java.lang.String)
         */
        @Override
        protected boolean checkLine(String line) {
            return !DataSetAttributeParser.attributeLinePrefixPattern.matcher(
                    line).matches();
        }

        /*
         * (non-Javadoc)
         *
         * @see utils.parse.TextFileParser#processLine(java.lang.String[],
         * java.lang.String[])
         */
        @SuppressWarnings("unused")
        @Override
        protected void processLine(String[] key, String[] value) {
            double[] coords = new double[value.length - 1];
            for (int i = 1; i < value.length; i++) {
                coords[i - 1] = Double.valueOf(value[i]);
            }
            this.idToCoordinates.add(Pair.getPair(value[0], coords));
        }

        public List<Pair<String, double[]>> getCoordinates() {
            return this.idToCoordinates;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.dataset.format.DataSetFormatParser#writeToFile(de.clusteval
     * .data.dataset.DataSet)
     */
    @Override
    public void writeToFileHelper(IDataSet dataSet, BufferedWriter writer)
            throws IOException {
        AbsoluteDataSet absDataSet = (AbsoluteDataSet) dataSet;
        DataMatrix dataMatrix = absDataSet.getDataSetContent();
        String[] ids = dataMatrix.getIds();
        double[][] coords = dataMatrix.getData();

        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < coords.length; row++) {
            sb.append(ids[row]);
            sb.append("\t");
            for (int col = 0; col < coords[row].length; col++) {
                sb.append(coords[row][col]);
                sb.append("\t");
            }
            sb.deleteCharAt(sb.length() - 1);

            sb.append(System.getProperty("line.separator"));
        }
        writer.append(sb.toString());
    }
}
