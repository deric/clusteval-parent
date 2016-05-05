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

import de.clusteval.api.FormatVersion;
import de.clusteval.api.Precision;
import de.clusteval.api.data.DataSetAttributeParser;
import de.clusteval.api.data.DataSetFormatParser;
import de.clusteval.api.data.IConversionConfiguration;
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.TextFileParser.OUTPUT_MODE;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.wiwie.wiutils.utils.parse.SimFileMatrixParser;
import de.wiwie.wiutils.utils.parse.SimFileParser;
import de.wiwie.wiutils.utils.parse.SimFileParser.SIM_FILE_FORMAT;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Christian Wiwie
 *
 */
@FormatVersion(version = 1)
public class APRowSimDataSetFormatParser extends DataSetFormatParser {

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.format.DataSetFormatParser#convertToStandardFormat(data.
     * dataset.DataSet)
     */
    @SuppressWarnings("unused")
    @Override
    public IDataSet convertToStandardFormat(IDataSet ds, IConversionInputToStandardConfiguration config) {
        return null;
    }

    @Override
    public IDataSet convertToThisFormat(IDataSet dataSet,
            IDataSetFormat dataSetFormat, IConversionConfiguration config)
            throws IOException, InvalidDataSetFormatException, RegisterException, UnknownProviderException {
        switch (dataSetFormat.getVersion()) {
            case 1:
                return convertToThisFormat_v1(dataSet, dataSetFormat, config);
            default:
                throw new InvalidDataSetFormatException("Version "
                        + dataSet.getDataSetFormat().getVersion()
                        + " is unknown for DataSetFormat "
                        + dataSet.getDataSetFormat());
        }
    }

    protected IDataSet convertToThisFormat_v1(IDataSet dataSet,
            IDataSetFormat dataSetFormat, IConversionConfiguration config)
            throws IOException, RegisterException, UnknownProviderException {

        // check if file already exists
        String absResultFilePath = dataSet.getAbsolutePath();
        absResultFilePath = removeResultFileNameSuffix(absResultFilePath);
        absResultFilePath += ".APRowSim";
        String resultFile = absResultFilePath;

        if (!(new File(resultFile).exists())) {
            this.log.debug("Converting input file...");
            // replace IDs by numeric values from [1:N]
            final SimFileParser p = new APSimFileConverter(
                    dataSet.getAbsolutePath(), SIM_FILE_FORMAT.MATRIX_HEADER,
                    null, null, resultFile, OUTPUT_MODE.STREAM,
                    SIM_FILE_FORMAT.ID_ID_SIM);
            p.process();
            this.log.debug("Finished converting");
        }
        APRowSimDataSetFormat format = new APRowSimDataSetFormat();
        format.init(dataSet.getRepository(), System.currentTimeMillis(), new File(absResultFilePath));
        format.setVersion(dataSet.getRepository().getCurrentDataSetFormatVersion(APRowSimDataSetFormat.class.getSimpleName()));

        return new RelativeDataSet(dataSet.getRepository(), false,
                System.currentTimeMillis(), new File(absResultFilePath),
                dataSet.getAlias(), format,
                dataSet.getDataSetType(), WEBSITE_VISIBILITY.HIDE);
    }

    class APSimFileConverter extends SimFileMatrixParser {

        protected BufferedWriter mappingWriter;

        /**
         * @param absFilePath
         * @param simFileFormat
         * @param absIdFilePath
         * @param idFileFormat
         * @param outputFile
         * @param outputMode
         * @param outputFormat
         * @throws IOException
         */
        public APSimFileConverter(String absFilePath,
                SIM_FILE_FORMAT simFileFormat, String absIdFilePath,
                ID_FILE_FORMAT idFileFormat, String outputFile,
                OUTPUT_MODE outputMode, SIM_FILE_FORMAT outputFormat)
                throws IOException {
            super(absFilePath, simFileFormat, absIdFilePath, idFileFormat,
                    outputFile, outputMode, outputFormat);
        }

        @Override
        protected void resetReader() throws IOException {
            super.resetReader();

            if (this.mappingWriter != null) {
                this.mappingWriter.close();
            }
            this.mappingWriter = new BufferedWriter(new FileWriter(new File(
                    this.outputFile + ".map")));
        }

        @Override
        protected void closeStreams() throws IOException {
            super.closeStreams();

            if (this.mappingWriter != null) {
                this.mappingWriter.close();
                this.mappingWriter = null;
            }
        }

        @Override
        public void finishProcess() {
            try {
                for (String key : this.idToKey.values()) {
                    this.mappingWriter.write(key);
                    this.mappingWriter.write(this.outSplit);
                    this.mappingWriter.write((this.getIdForKey(key)) + "");
                    this.mappingWriter.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getIdForKey(String key) {
            return super.getIdForKey(key) + 1;
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

        @Override
        protected String getLineOutput(String[] key, String[] value) {
            if (this.outputFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
                StringBuilder sb = new StringBuilder();
                if (this.currentLine == 0) {
                    return "";
                }
                for (int i = 0; i < value.length; i++) {
                    if (this.getIdForKey(key[0]) == i + 1) {
                        continue;
                    }
                    sb.append(this.getIdForKey(key[0]) + this.outSplit
                            + (i + 1) + this.outSplit
                            + this.similarities.getSimilarity(0, i) + "\n");
                }
                return sb.toString();
            }
            return super.getLineOutput(key, value);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.format.DataSetFormatParser#parse(data.dataset.DataSet)
     */
    @SuppressWarnings("unused")
    @Override
    public SimilarityMatrix parse(IDataSet dataSet, Precision precision) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.dataset.format.DataSetFormatParser#writeToFile(de.clusteval
     * .data.dataset.DataSet)
     */
    @SuppressWarnings("unused")
    @Override
    public void writeToFileHelper(IDataSet dataSet, BufferedWriter writer) {
    }
}
