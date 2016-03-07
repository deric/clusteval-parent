package utils.parse;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class SimilarityFileNormalizer.
 */
public class SimilarityFileNormalizer extends SimFileParser {

	/**
	 * The Enum PROCESSING_PHASE.
	 */
	enum PROCESSING_PHASE {

		/** The FIN d_ maximu m_ sim. */
		FIND_MAXIMUM_SIM,
		/** The NORMALIZ e_ si m_ file. */
		NORMALIZE_SIM_FILE
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		try {
			final SimilarityFileNormalizer no = new SimilarityFileNormalizer(
					"/home/chris/Dropbox/workspace/ClusEvalFramework/example/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_costmatrix_for_beh_with_threshold_100.cm.SimMatrix.norm",
					SIM_FILE_FORMAT.MATRIX_HEADER,
					"/home/chris/Dropbox/workspace/ClusEvalFramework/example/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_costmatrix_for_beh_with_threshold_100.cm.SimMatrix.norm2",
					1.0);
			no.process();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/** The processing phase. */
	protected PROCESSING_PHASE processingPhase;

	/** The invert. */
	protected boolean invert;

	/** The min file similarity. */
	protected double minFileSimilarity;

	/** The max file similarity. */
	protected double maxFileSimilarity;

	/** The max target similarity. */
	protected double maxTargetSimilarity;

	/**
	 * Instantiates a new similarity file normalizer.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param absOutputFile
	 *            the abs output file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimilarityFileNormalizer(final String absFilePath,
			SIM_FILE_FORMAT simFileFormat, final String absOutputFile)
			throws IOException {
		this(absFilePath, simFileFormat, absOutputFile, Double.MIN_VALUE, 0.0,
				1.0);
	}

	/**
	 * Instantiates a new similarity file normalizer.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param absOutputFile
	 *            the abs output file
	 * @param maxTargetSimilarity
	 *            the max target similarity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimilarityFileNormalizer(final String absFilePath,
			SIM_FILE_FORMAT simFileFormat, final String absOutputFile,
			final double maxTargetSimilarity) throws IOException {
		this(absFilePath, simFileFormat, absOutputFile, Double.MAX_VALUE,
				Double.MIN_VALUE, maxTargetSimilarity);
	}

	/**
	 * Instantiates a new similarity file normalizer.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @param absOutputFile
	 *            the abs output file
	 * @param minFileSimilarity
	 *            the min file similarity
	 * @param maxFileSimilarity
	 *            the max file similarity
	 * @param maxTargetSimilarity
	 *            the max target similarity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimilarityFileNormalizer(final String absFilePath,
			SIM_FILE_FORMAT simFileFormat, final String absOutputFile,
			final double minFileSimilarity, final double maxFileSimilarity,
			final double maxTargetSimilarity) throws IOException {
		super(absFilePath, simFileFormat, null, null, absOutputFile,
				OUTPUT_MODE.STREAM, simFileFormat);
		this.processingPhase = PROCESSING_PHASE.FIND_MAXIMUM_SIM;
		if (maxFileSimilarity == Double.MIN_VALUE
				|| minFileSimilarity == Double.MAX_VALUE) {
			this.findMinAndMaxSimilarity();
		} else {
			this.maxFileSimilarity = maxFileSimilarity;
			this.minFileSimilarity = minFileSimilarity;
		}
		this.maxTargetSimilarity = maxTargetSimilarity;
		this.processingPhase = PROCESSING_PHASE.NORMALIZE_SIM_FILE;
	}

	/**
	 * Find min and max similarity.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void findMinAndMaxSimilarity() throws IOException {
		log.debug("Scanning for minimum/maximum similarity in file");
		this.process();
		log.debug("Found minimum similarity to be " + this.minFileSimilarity);
		log.debug("Found maximal similarity to be " + this.maxFileSimilarity);
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
		if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
			switch (this.processingPhase) {
				case NORMALIZE_SIM_FILE :
					sb.append(this.combineColumns(key));
					sb.append(this.outSplit);
					if (value[0].equals("NA")) {
						sb.append("NA");
						sb.append(System.getProperty("line.separator"));
						break;
					}
					final double sim = Double.valueOf(value[0]);
					if (this.invert) {
						sb.append(String
								.valueOf(1 - (sim / this.maxFileSimilarity * this.maxTargetSimilarity)));
					} else {
						sb.append(String
								.valueOf((sim - this.minFileSimilarity)
										/ (this.maxFileSimilarity - this.minFileSimilarity)
										* this.maxTargetSimilarity));
						sb.append(System.getProperty("line.separator"));
					}
					break;
				case FIND_MAXIMUM_SIM :
				default :
			}
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
			switch (this.processingPhase) {
				case NORMALIZE_SIM_FILE :
					sb.append(key[0]);
					sb.append(this.outSplit);
					if (this.currentLine == 0) {
						sb.append(this.combineColumns(value));
					} else {
						for (int pos = 0; pos < value.length; pos++) {
							if (value[pos].equals("NA")) {
								sb.append("NA");
							} else {
								final double sim = Double.valueOf(value[pos]);
								if (this.invert) {
									sb.append(String
											.valueOf(1 - (sim
													/ this.maxFileSimilarity * this.maxTargetSimilarity)));
								} else {
									sb.append(String
											.valueOf((sim - this.minFileSimilarity)
													/ (this.maxFileSimilarity - this.minFileSimilarity)
													* this.maxTargetSimilarity));
								}
							}
							sb.append(this.outSplit);
						}
						sb.deleteCharAt(sb.lastIndexOf(this.outSplit));
					}
					sb.append(System.getProperty("line.separator"));
					break;
				case FIND_MAXIMUM_SIM :
					if (this.currentLine == 0)
						return "";
				default :
			}
		}
		return sb.toString();
	}

	/**
	 * Inverte similarities.
	 */
	public void inverteSimilarities() {
		this.invert = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.SimFileParser#processLine(java.lang.String[],
	 * java.lang.String[])
	 */
	@Override
	protected void processLine(final String[] key, final String[] value) {
		if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)) {
			switch (this.processingPhase) {
				case NORMALIZE_SIM_FILE :
					break;
				case FIND_MAXIMUM_SIM :
					if (value[0].equals("NA"))
						break;
					final double val = Double.valueOf(value[0]);
					if (val > this.maxFileSimilarity) {
						this.maxFileSimilarity = val;
					}
					if (val < this.minFileSimilarity)
						this.minFileSimilarity = val;
					break;
				default :
			}
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
			if (this.currentLine == 0)
				return;
			switch (this.processingPhase) {
				case NORMALIZE_SIM_FILE :
					break;
				case FIND_MAXIMUM_SIM :
					for (int pos = 0; pos < value.length; pos++) {
						if (value[pos].equals("NA"))
							continue;
						final double val = Double.valueOf(value[pos]);
						if (val > this.maxFileSimilarity) {
							this.maxFileSimilarity = val;
						}
						if (val < this.minFileSimilarity)
							this.minFileSimilarity = val;
					}
					break;
				default :
			}
		}
	}
}
