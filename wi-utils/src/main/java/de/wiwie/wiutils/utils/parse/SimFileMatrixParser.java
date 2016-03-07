package de.wiwie.wiutils.utils.parse;

import java.io.IOException;

import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;

/**
 * The Class SimFile2DArrayParser.
 */
public class SimFileMatrixParser extends SimFileParser {

	/** The similarities. */
	// protected double[][] similarities;
	protected SimilarityMatrix similarities;

	/**
	 * If this one is true, sparse values are not written into the result format
	 * (when supported), e.g. for a row-wise format. If this one is false, the
	 * sparse values are replaced by a default value or, if specified, by the
	 * value of {@link #sparseReplaceValue}.
	 */
	protected boolean skipSparseValues = true;

	protected double sparseReplaceValue = 0.0;

	/**
	 * Instantiates a new sim file2 d array parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileMatrixParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat) throws IOException {
		this(absFilePath, simFileFormat, null, null);
	}

	/**
	 * Instantiates a new sim file2 d array parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param absIdFilePath
	 *            the abs id file path
	 * @param idFileFormat
	 *            the id file format
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileMatrixParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String absIdFilePath,
			final ID_FILE_FORMAT idFileFormat) throws IOException {
		this(absFilePath, simFileFormat, absIdFilePath, idFileFormat, null,
				null, null);
	}

	/**
	 * Instantiates a new sim file2 d array parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param absIdFilePath
	 *            the abs id file path
	 * @param idFileFormat
	 *            the id file format
	 * @param outputFile
	 *            the output file
	 * @param outputMode
	 *            the output mode
	 * @param outputFormat
	 *            the output format
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileMatrixParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String absIdFilePath,
			final ID_FILE_FORMAT idFileFormat, final String outputFile,
			final OUTPUT_MODE outputMode, final SIM_FILE_FORMAT outputFormat)
			throws IOException {
		this(absFilePath, simFileFormat, absIdFilePath, idFileFormat,
				outputFile, outputMode, outputFormat, (SimilarityMatrix) null);
	}

	/**
	 * Instantiates a new sim file2 d array parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param outputFile
	 *            the output file
	 * @param outputMode
	 *            the output mode
	 * @param outputFormat
	 *            the output format
	 * @param initSimilarities
	 *            the init similarities
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileMatrixParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String outputFile,
			final OUTPUT_MODE outputMode, final SIM_FILE_FORMAT outputFormat,
			final SimilarityMatrix initSimilarities) throws IOException {
		super(absFilePath, simFileFormat, null, null, outputFile, outputMode,
				outputFormat);
		this.initSimilarities(initSimilarities);
	}

	/**
	 * Instantiates a new sim file2 d array parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param outputFile
	 *            the output file
	 * @param outputMode
	 *            the output mode
	 * @param outputFormat
	 *            the output format
	 * @param precision
	 *            The numeric precision in which to store the similarities
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileMatrixParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String outputFile,
			final OUTPUT_MODE outputMode, final SIM_FILE_FORMAT outputFormat,
			final NUMBER_PRECISION precision) throws IOException {
		super(absFilePath, simFileFormat, null, null, outputFile, outputMode,
				outputFormat);
		this.initSimilarities(precision);
	}

	/**
	 * Instantiates a new sim file2 d array parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param absIdFilePath
	 *            the abs id file path
	 * @param idFileFormat
	 *            the id file format
	 * @param outputFile
	 *            the output file
	 * @param outputMode
	 *            the output mode
	 * @param outputFormat
	 *            the output format
	 * @param initSimilarities
	 *            the init similarities
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileMatrixParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String absIdFilePath,
			final ID_FILE_FORMAT idFileFormat, final String outputFile,
			final OUTPUT_MODE outputMode, final SIM_FILE_FORMAT outputFormat,
			final SimilarityMatrix initSimilarities) throws IOException {
		super(absFilePath, simFileFormat, absIdFilePath, idFileFormat,
				outputFile, outputMode, outputFormat);
		this.initSimilarities(initSimilarities);
	}

	/**
	 * Instantiates a new sim file2 d array parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param absIdFilePath
	 *            the abs id file path
	 * @param idFileFormat
	 *            the id file format
	 * @param outputFile
	 *            the output file
	 * @param outputMode
	 *            the output mode
	 * @param outputFormat
	 *            the output format
	 * @param precision
	 *            The numeric precision in which to store the similarities
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileMatrixParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String absIdFilePath,
			final ID_FILE_FORMAT idFileFormat, final String outputFile,
			final OUTPUT_MODE outputMode, final SIM_FILE_FORMAT outputFormat,
			final NUMBER_PRECISION precision) throws IOException {
		super(absFilePath, simFileFormat, absIdFilePath, idFileFormat,
				outputFile, outputMode, outputFormat);
		this.initSimilarities(precision);
	}

	/**
	 * @return skipSparseValues
	 */
	public boolean getSkipSparseValues() {
		return this.skipSparseValues;
	}

	/**
	 * @param skipSparseValues
	 */
	public void setSkipSparseValues(boolean skipSparseValues) {
		this.skipSparseValues = skipSparseValues;
	}

	/**
	 * @param replaceValue
	 */
	public void setSparseReplaceValue(final double replaceValue) {
		this.sparseReplaceValue = replaceValue;
	}

	/**
	 * @return
	 */
	public double getSparseReplaceValue() {
		return this.sparseReplaceValue;
	}

	/**
	 * Gets the similarities.
	 * 
	 * @return the similarities
	 */
	public SimilarityMatrix getSimilarities() {
		return this.similarities;
	}

	/**
	 * Inits the similarities.
	 * 
	 * @param initSims
	 *            the init sims
	 */
	public void initSimilarities(final SimilarityMatrix initSims) {
		if (initSims == null) {
			if (this.outputMode != null
					&& this.outputMode.equals(OUTPUT_MODE.STREAM))
				this.similarities = new SimilarityMatrix(1, this.sequenceCount);
			else
				this.similarities = new SimilarityMatrix(this.sequenceCount,
						this.sequenceCount);
		} else {
			this.similarities = initSims;
		}
	}

	/**
	 * Inits the similarities.
	 * 
	 * @param precision
	 *            The numeric precision in which to store the similarities
	 */
	public void initSimilarities(final NUMBER_PRECISION precision) {
		if (this.outputMode != null
				&& this.outputMode.equals(OUTPUT_MODE.STREAM))
			this.similarities = new SimilarityMatrix(1, this.sequenceCount,
					precision);
		else
			this.similarities = new SimilarityMatrix(this.sequenceCount,
					this.sequenceCount, precision);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.TextFileParser#checkLine(java.lang.String)
	 */
	@Override
	protected boolean checkLine(String line) {
		if (parsingComments) {
			boolean isComment = attributeLinePrefixPattern.matcher(line)
					.matches();
			if (isComment)
				this.currentLine = -1;
			parsingComments = parsingComments && isComment;
			return !isComment;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.SimFileParser#processLine(java.lang.String[],
	 * java.lang.String[])
	 */
	@Override
	protected void processLine(final String[] key, final String[] value) {
		int i = -1, j = -1;
		if (this.simFileFormat.equals(SIM_FILE_FORMAT.SIM)) {
			// here we assume, that the sim file contains n*n entries in the
			// correct order
			i = (int) (this.currentLine / this.getTotalLineCount());
			j = (int) (this.currentLine % this.getTotalLineCount());
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
			i = this.getIdForKey(key[0]);
			j = this.getIdForKey(key[1]);
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.BLAST)) {
			i = this.getIdForKey(key[0]);
			j = this.getIdForKey(key[1]);
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX)) {
			i = (int) (this.currentLine);
			for (j = 0; j < sequenceCount; j++) {
				if (value[j].equals("NA"))
					continue;

				if (this.outputMode != null
						&& this.outputMode.equals(OUTPUT_MODE.STREAM))
					this.similarities.setSimilarity(0, j,
							Double.valueOf(value[j]));
				else
					this.similarities.setSimilarity(i, j,
							Double.valueOf(value[j]));
			}
			return;
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
			i = (int) (this.currentLine);
			// parse ids from first row
			if (i == 0) {
				String[] ids = value;
				for (int x = 0; x < ids.length; x++) {
					this.keyToId.put(ids[x], x);
					this.idToKey.put(x, ids[x]);
				}
				this.similarities.setIds(ids);

				return;
			}
			for (j = 0; j < sequenceCount; j++) {
				if (value[j].equals("NA"))
					continue;

				if (this.outputMode != null
						&& this.outputMode.equals(OUTPUT_MODE.STREAM))
					this.similarities.setSimilarity(0, j,
							Double.valueOf(value[j]));
				else
					this.similarities.setSimilarity(i - 1, j,
							Double.valueOf(value[j]));
			}
			return;
		}
		// TODO ?
		try {
			if (this.outputMode == null
					|| this.outputMode.equals(OUTPUT_MODE.BURST))
				this.similarities.setSimilarity(i, j,
						Double.valueOf(this.combineColumns(value)));
			else
				this.similarities.setSimilarity(0, j,
						Double.valueOf(this.combineColumns(value)));
		} catch (NumberFormatException e) {
			this.log.warn(String.format(
					"Skipping invalid similarity value '%s' between %s and %s",
					this.combineColumns(value), this.getKeyForId(i),
					this.getKeyForId(j)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.TextFileParser#finishProcess()
	 */
	@Override
	public void finishProcess() {
		super.finishProcess();
		if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
			String[] ids = new String[this.idToKey.size()];
			for (int i = 0; i < this.idToKey.size(); i++) {
				ids[i] = this.idToKey.get(i);
			}
			this.similarities.setIds(ids);

			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.TextFileParser#processBurstOutput()
	 */
	@Override
	protected String getBurstOutput() {
		StringBuilder sb = new StringBuilder();
		if (this.outputFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM))
			for (int i = 0; i < this.sequenceCount; i++) {
				for (int j = 0; j < this.sequenceCount; j++) {
					if (!this.similarities.isSparse(i, j))
						sb.append(this.getKeyForId(i) + this.outSplit
								+ this.getKeyForId(j) + this.outSplit
								+ this.similarities.getSimilarity(i, j) + "\n");
					else if (!skipSparseValues)
						sb.append(this.getKeyForId(i) + this.outSplit
								+ this.getKeyForId(j) + this.outSplit
								+ sparseReplaceValue + "\n");
				}
			}
		else if (this.outputFormat.equals(SIM_FILE_FORMAT.MATRIX)) {
			for (int i = 0; i < this.sequenceCount; i++) {
				for (int j = 0; j < this.sequenceCount; j++) {
					if (!this.similarities.isSparse(i, j))
						sb.append(this.similarities.getSimilarity(i, j)
								+ this.outSplit);
					else
						sb.append("NA" + this.outSplit);
				}
				sb.append("\n");
			}
		} else if (this.outputFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
			// print ids into first line
			for (int j = 0; j < this.sequenceCount; j++) {
				sb.append(this.outSplit + this.getKeyForId(j));
			}
			sb.append("\n");

			for (int i = 0; i < this.sequenceCount; i++) {
				sb.append(this.getKeyForId(i) + this.outSplit);
				for (int j = 0; j < this.sequenceCount; j++) {
					if (!this.similarities.isSparse(i, j))
						sb.append(this.similarities.getSimilarity(i, j)
								+ this.outSplit);
					else
						sb.append("NA" + this.outSplit);
				}
				sb.deleteCharAt(sb.length() - 1);

				sb.append("\n");
			}
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.TextFileParser#getLineOutput(java.lang.String[],
	 * java.lang.String[])
	 */
	@Override
	protected String getLineOutput(final String[] key, final String[] value) {
		StringBuilder sb = new StringBuilder();
		if (this.outputFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
			if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
				if (this.currentLine == 0)
					return "";
				for (int i = 0; i < value.length; i++) {
					if (!this.similarities.isSparse(0, i))
						sb.append(key[0] + this.outSplit + this.getKeyForId(i)
								+ this.outSplit
								+ this.similarities.getSimilarity(0, i) + "\n");
					else if (!skipSparseValues)
						sb.append(key[0] + this.outSplit + this.getKeyForId(i)
								+ this.outSplit + sparseReplaceValue + "\n");
				}
			} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
				if (!this.similarities.isSparse(0, this.getIdForKey(key[1])))
					sb.append(key[0]
							+ this.outSplit
							+ key[1]
							+ this.outSplit
							+ this.similarities.getSimilarity(0,
									this.getIdForKey(key[1])) + "\n");
				else if (!skipSparseValues)
					sb.append(key[0] + this.outSplit + key[1] + this.outSplit
							+ sparseReplaceValue + "\n");
			}
		} else if (this.outputFormat.equals(SIM_FILE_FORMAT.MATRIX)) {
			if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
				for (int i = 0; i < value.length; i++) {
					if (!this.similarities.isSparse(0, i))
						sb.append(this.similarities.getSimilarity(0, i)
								+ this.outSplit);
					else
						sb.append("NA" + this.outSplit);
				}
			} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
				if (!this.similarities.isSparse(0, this.getIdForKey(key[1])))
					sb.append(this.similarities.getSimilarity(0,
							this.getIdForKey(key[1]))
							+ this.outSplit);
				else
					sb.append("NA" + this.outSplit);
			}
		} else if (this.outputFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
			if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
				// print ids into first line
				if (this.currentLine == 0) {
					for (int j = 0; j < this.sequenceCount; j++) {
						sb.append(this.outSplit + this.getKeyForId(j));
					}
					sb.append("\n");
				} else if ((this.currentLine + 1) % this.sequenceCount == 0) {
					sb.append(this.getKeyForId((int) (this.currentLine - 1)
							/ this.sequenceCount));
					sb.append(this.outSplit);
					for (int j = 0; j < this.sequenceCount; j++) {
						if (!this.similarities.isSparse(0, j))
							sb.append(this.similarities.getSimilarity(0, j));
						else
							sb.append("NA");
						sb.append(this.outSplit);
					}
					int delPos = sb.lastIndexOf(this.outSplit);
					sb.delete(delPos, delPos + this.outSplit.length());
					sb.append("\n");
				}
			} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
				// print ids into first line
				if (this.currentLine == 0) {
					for (int j = 0; j < this.sequenceCount; j++) {
						sb.append(this.outSplit + this.getKeyForId(j));
					}
					sb.append("\n");
				}
				if ((this.currentLine + 1) % this.sequenceCount == 0) {
					sb.append(this.getKeyForId((int) (this.currentLine - 1)
							/ this.sequenceCount));
					sb.append(this.outSplit);
					for (int j = 0; j < this.sequenceCount; j++) {
						if (!this.similarities.isSparse(0, j))
							sb.append(this.similarities.getSimilarity(0, j));
						else
							sb.append("NA");
						sb.append(this.outSplit);
					}
					int delPos = sb.lastIndexOf(this.outSplit);
					sb.delete(delPos, delPos + this.outSplit.length());
					sb.append("\n");
				}
			}
		}
		return sb.toString();
	}
}
