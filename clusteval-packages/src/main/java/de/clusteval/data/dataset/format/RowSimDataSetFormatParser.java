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

import de.clusteval.api.data.DataSetFormatParser;
import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.api.FormatVersion;
import de.clusteval.api.Precision;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.api.data.IConversionConfiguration;
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.data.DataSetAttributeFilterer;
import de.clusteval.api.data.RelativeDataSet;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.wiwie.wiutils.utils.parse.SimFileMatrixParser;
import de.wiwie.wiutils.utils.parse.SimFileParser.SIM_FILE_FORMAT;
import de.wiwie.wiutils.utils.parse.SimilarityFileNormalizer;
import de.clusteval.utils.TextFileParser.OUTPUT_MODE;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Christian Wiwie
 */
@FormatVersion(version = 1)
public class RowSimDataSetFormatParser extends DataSetFormatParser {

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
                   InvalidDataSetFormatVersionException, RegisterException,
                   UnknownDataSetFormatException, UnknownProviderException {
        switch (dataSet.getDataSetFormat().getVersion()) {
            case 1:
                return convertToStandardFormat_v1(dataSet, config);
            default:
                throw new InvalidDataSetFormatVersionException("Version "
                        + dataSet.getDataSetFormat().getVersion()
                        + " is unknown for DataSetFormat "
                        + dataSet.getDataSetFormat());
        }
    }

    /**
     * @param dataSet
     * @param config
     * @return The converted dataset.
     * @throws IOException
     * @throws RegisterException
     * @throws UnknownDataSetFormatException
     */
    protected IDataSet convertToStandardFormat_v1(IDataSet dataSet,
            IConversionInputToStandardConfiguration config)
            throws IOException, RegisterException, UnknownDataSetFormatException, UnknownProviderException {
        // ID_ID_SIM -> SIM_MATRIX

        // check if file already exists
        String resultFileName = dataSet.getAbsolutePath();
        resultFileName = removeResultFileNameSuffix(resultFileName);
        resultFileName += ".SimMatrix";
        final String resultFile = resultFileName;

        if (!(new File(resultFile).exists())) {
            this.log.debug("Converting input file...");
            final SimFileMatrixParser p = new SimFileMatrixParser(
                    dataSet.getAbsolutePath(), SIM_FILE_FORMAT.ID_ID_SIM, null,
                    null, resultFile, OUTPUT_MODE.BURST,
                    SIM_FILE_FORMAT.MATRIX_HEADER);
            p.process();
            if (this.normalize) {
                new SimilarityFileNormalizer(resultFile,
                        SIM_FILE_FORMAT.MATRIX_HEADER, resultFile + ".tmp", 1.0)
                        .process();
                new File(resultFile).delete();
                new File(resultFile + ".tmp").renameTo(new File(resultFile));
            }
        }
        return new RelativeDataSet(dataSet.getRepository(), false,
                dataSet.getChangeDate(), new File(resultFileName),
                dataSet.getAlias(),
                (RelativeDataSetFormat) DataSetFormat.parseFromString(
                        dataSet.getRepository(), "SimMatrixDataSetFormat"),
                dataSet.getDataSetType(), WEBSITE_VISIBILITY.HIDE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.format.DataSetFormatParser#convertToThisFormat(data.dataset
     * .DataSet)
     */
    @Override
    public IDataSet convertToThisFormat(IDataSet dataSet,
            IDataSetFormat dataSetFormat, IConversionConfiguration config)
            throws IOException, InvalidDataSetFormatVersionException,
                   RegisterException, UnknownDataSetFormatException {
        switch (dataSetFormat.getVersion()) {
            case 1:
                return convertToThisFormat_v1(dataSet, dataSetFormat);
            default:
                throw new InvalidDataSetFormatVersionException("Version "
                        + dataSet.getDataSetFormat().getVersion()
                        + " is unknown for DataSetFormat "
                        + dataSet.getDataSetFormat());
        }
    }

    protected IDataSet convertToThisFormat_v1(IDataSet dataSet,
            IDataSetFormat dataSetFormat) throws IOException, RegisterException,
                                                 UnknownDataSetFormatException {
        // SIM_MATRIX -> ID_ID_SIM

        // check if file already exists
        String resultFileName = dataSet.getAbsolutePath();
        resultFileName = removeResultFileNameSuffix(resultFileName);
        resultFileName += ".RowSim";
        final String resultFile = resultFileName;

        if (!(new File(resultFile).exists())) {
            String matrixFile = dataSet.getAbsolutePath();
            if (dataSetFormat.getNormalized()) {
                this.log.info("Normalizing similarities");
                new SimilarityFileNormalizer(matrixFile,
                        SIM_FILE_FORMAT.MATRIX_HEADER, matrixFile + ".tmp", 1.0)
                        .process();
                new File(matrixFile).delete();
                new File(matrixFile + ".tmp").renameTo(new File(matrixFile));
            }

            this.log.debug("Converting input file...");
            final SimFileMatrixParser p = new SimFileMatrixParser(
                    dataSet.getAbsolutePath(), SIM_FILE_FORMAT.MATRIX_HEADER,
                    null, null, resultFile, OUTPUT_MODE.STREAM,
                    SIM_FILE_FORMAT.ID_ID_SIM);
            p.process();
        }

        RowSimDataSetFormat format = new RowSimDataSetFormat();
        format.init(dataSet.getRepository(), System.currentTimeMillis(), new File(resultFileName));
        format.setVersion(dataSet.getRepository().getCurrentDataSetFormatVersion(RowSimDataSetFormat.class.getSimpleName()));

        return new RelativeDataSet(dataSet.getRepository(), false,
                dataSet.getChangeDate(), new File(resultFileName),
                dataSet.getAlias(), format,
                dataSet.getDataSetType(), WEBSITE_VISIBILITY.HIDE);
    }

    /**
     * The Class APSimFileConverter.
     */
    class APSimFileConverter extends SimFileMatrixParser {

        /** The mapping writer. */
        protected BufferedWriter mappingWriter;

        /**
         * Instantiates a new aP sim file converter.
         *
         * @param absFilePath
         *                      the abs file path
         * @param simFileFormat
         *                      the sim file format
         * @param absIdFilePath
         *                      the abs id file path
         * @param idFileFormat
         *                      the id file format
         * @param outputFile
         *                      the output file
         * @param outputMode
         *                      the output mode
         * @param outputFormat
         *                      the output format
         * @throws IOException
         *                     Signals that an I/O exception has occurred.
         */
        public APSimFileConverter(String absFilePath,
                SIM_FILE_FORMAT simFileFormat, String absIdFilePath,
                ID_FILE_FORMAT idFileFormat, String outputFile,
                OUTPUT_MODE outputMode, SIM_FILE_FORMAT outputFormat)
                throws IOException {
            super(absFilePath, simFileFormat, absIdFilePath, idFileFormat,
                    outputFile, outputMode, outputFormat);
        }

        /*
         * (non-Javadoc)
         *
         * @see utils.parse.TextFileParser#resetReader()
         */
        @Override
        protected void resetReader() throws IOException {
            super.resetReader();

            if (this.mappingWriter != null) {
                this.mappingWriter.close();
            }
            this.mappingWriter = new BufferedWriter(new FileWriter(new File(
                    this.outputFile + ".map")));
        }

        /*
         * (non-Javadoc)
         *
         * @see utils.parse.TextFileParser#closeStreams()
         */
        @Override
        protected void closeStreams() throws IOException {
            super.closeStreams();

            if (this.mappingWriter != null) {
                this.mappingWriter.close();
                this.mappingWriter = null;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see utils.parse.TextFileParser#finishProcess()
         */
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


        /*
         * (non-Javadoc)
         *
         * @see utils.parse.SimFileParser#getIdForKey(java.lang.String)
         */
        @Override
        public int getIdForKey(String key) {
            return super.getIdForKey(key) + 1;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * utils.parse.SimFile2DArrayParser#getLineOutput(java.lang.String[],
         * java.lang.String[])
         */
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
    @Override
    public SimilarityMatrix parse(IDataSet dataSet, Precision precision)
            throws IOException, InvalidDataSetFormatVersionException {
        switch (dataSet.getDataSetFormat().getVersion()) {
            case 1:
                return parse_v1(dataSet, precision);
            default:
                throw new InvalidDataSetFormatVersionException("Version "
                        + dataSet.getDataSetFormat().getVersion()
                        + " is unknown for DataSetFormat "
                        + dataSet.getDataSetFormat());
        }
    }

    protected SimilarityMatrix parse_v1(IDataSet dataSet,
            Precision precision) throws IOException {
        /*
         * Remove dataset attributes from file and write the result to
         * dataSet.getAbsolutePath() + ".strip"
         */
        DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
                dataSet.getAbsolutePath());
        filterer.process();

        final SimFileMatrixParser p = new SimFileMatrixParser(
                dataSet.getAbsolutePath() + ".strip",
                SIM_FILE_FORMAT.ID_ID_SIM, null, null, null, precision);
        p.process();
        return p.getSimilarities();
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
        return;
    }
}
