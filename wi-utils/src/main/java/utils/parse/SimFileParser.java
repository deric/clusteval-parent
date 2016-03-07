package utils.parse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.ArraysExt;
import utils.StringExt;
import utils.text.TextFileValueUniqueListParser;

// TODO: Auto-generated Javadoc
/**
 * The Class SimFileParser.
 */
public class SimFileParser extends TextFileParser {

	/**
	 * The Enum ID_FILE_FORMAT.
	 */
	public enum ID_FILE_FORMAT {

		/** The ID. */
		ID,
		/** The I d_ tag. */
		ID_TAG
	}

	/**
	 * The Enum SIM_FILE_FORMAT.
	 */
	public enum SIM_FILE_FORMAT {

		/** The SIM. */
		SIM,
		/** The I d_ i d_ sim. */
		ID_ID_SIM,
		/** The BLAST. */
		BLAST,
		/** The MATRIX. */
		MATRIX,
		/** The MATRI x_ header. */
		MATRIX_HEADER
	}

	/** The sequence count. */
	protected int sequenceCount;

	/** The sim file format. */
	protected SIM_FILE_FORMAT simFileFormat;

	/** The output format. */
	protected SIM_FILE_FORMAT outputFormat;

	/** The abs id file path. */
	protected String absIdFilePath;

	/** The id file format. */
	protected ID_FILE_FORMAT idFileFormat;

	/** The ids. */
	protected List<String> ids;

	/** The key to id. */
	protected Map<String, Integer> keyToId;

	/** The id to key. */
	protected Map<Integer, String> idToKey;

	/**
	 * Instantiates a new sim file parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param simFileFormat
	 *            the sim file format
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public SimFileParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat) throws IOException {
		this(absFilePath, simFileFormat, null, null);
	}

	/**
	 * Instantiates a new sim file parser.
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
	public SimFileParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String absIdFilePath,
			final ID_FILE_FORMAT idFileFormat) throws IOException {
		this(absFilePath, simFileFormat, absIdFilePath, idFileFormat, null,
				null, null);
	}

	/**
	 * Instantiates a new sim file parser.
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
	public SimFileParser(final String absFilePath,
			final SIM_FILE_FORMAT simFileFormat, final String absIdFilePath,
			final ID_FILE_FORMAT idFileFormat, final String outputFile,
			final OUTPUT_MODE outputMode, final SIM_FILE_FORMAT outputFormat)
			throws IOException {
		super(absFilePath, null, null, outputFile, outputMode);
		this.simFileFormat = simFileFormat;
		this.outputFormat = outputFormat;
		this.keyToId = new HashMap<String, Integer>();
		this.idToKey = new HashMap<Integer, String>();
		if (absIdFilePath != null) {
			this.absIdFilePath = absIdFilePath;
			this.idFileFormat = idFileFormat;
			this.parseIdFile();
		}
		if (this.simFileFormat.equals(SIM_FILE_FORMAT.ID_ID_SIM)
				|| this.simFileFormat.equals(SIM_FILE_FORMAT.BLAST)) {
			this.keyColumns = new int[]{0, 1};
			this.valueColumns = new int[]{2};
			log.trace("Counting ids of similarity file...");
			this.sequenceCount = this.countKeysInColumns(this.keyColumns);
			log.trace("Similarity file contains " + this.sequenceCount
					+ " lines");
			if (this.getTotalLineCount() != (long) Math.pow(this.sequenceCount,
					2)) {
				log.warn("WARNING: The number of lines in the similarity file ("
						+ (this.getTotalLineCount())
						+ ") does not match the expected number ("
						+ (this.sequenceCount * this.sequenceCount) + ")");
			}
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.SIM)) {
			this.sequenceCount = (int) Math.sqrt(this.getTotalLineCount());
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX)) {
			// setting both arrays empty will cause default behaviour, all
			// columns are handled as values
			this.keyColumns = new int[0];
			this.valueColumns = new int[0];
			this.sequenceCount = this.getColumnsInFile();
		} else if (this.simFileFormat.equals(SIM_FILE_FORMAT.MATRIX_HEADER)) {
			this.keyColumns = new int[]{0};
			this.valueColumns = ArraysExt.range(1, this.getColumnsInFile());
			// TODO: verify, why was it like that before?
			// this.sequenceCount = this.countKeysInColumns(this.keyColumns);
			this.sequenceCount = this.getColumnsInFile() - 1;
		}
	}

	/**
	 * Count keys in columns.
	 * 
	 * @param keyColumns
	 *            the key columns
	 * @return the int
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected int countKeysInColumns(final int[] keyColumns) throws IOException {
		this.resetReader();
		long lineCount = 0;
		int number = this.keyToId.keySet().size();
		try {
			String line = null;
			this.parsingComments = true;
			while ((line = this.readLine()) != null) {
				if ((line.isEmpty() && this.skipEmptyLines) || !checkLine(line))
					continue;
				lineCount++;
				final String[] lineSplit = StringExt.split(line, this.inSplit);
				final String[] keyEntries = this.extractKeyEntries(lineSplit);
				for (final String key : keyEntries) {
					if (key.isEmpty())
						continue;
					if (this.absIdFilePath != null) {
						if (!this.keyToId.containsKey(key)) {
							throw new IOException(
									"The similarity file contains wrong keys");
						}
						// TODO: removed at 17.04.2012
						// else if (this.keyToId.get(key) != null) {
						// this.keyToId.put(key, number);
						// this.idToKey.put(number++, key);
						// }
					} else {
						if (!this.keyToId.containsKey(key)) {
							this.keyToId.put(key, number);
							this.idToKey.put(number++, key);
						}
					}
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.setTotalLineCount(lineCount);
		return this.keyToId.keySet().size();
	}

	/**
	 * Gets the absolute id file path.
	 * 
	 * @return the absolute id file path
	 */
	public String getAbsoluteIdFilePath() {
		return this.absIdFilePath;
	}

	/**
	 * Gets the id for key.
	 * 
	 * @param key
	 *            the key
	 * @return the id for key
	 */
	public int getIdForKey(final String key) {
		return (this.keyToId.containsKey(key) && this.keyToId.get(key) != null)
				? this.keyToId.get(key)
				: -1;
	}

	/**
	 * Gets the key for id.
	 * 
	 * @param id
	 *            the id
	 * @return the key for id
	 */
	public String getKeyForId(final int id) {
		return (this.idToKey.containsKey(id)) ? this.idToKey.get(id) : null;
	}

	/**
	 * Gets the sequence count.
	 * 
	 * @return the sequence count
	 */
	public int getSequenceCount() {
		return this.sequenceCount;
	}

	/**
	 * Parses the id file.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void parseIdFile() throws IOException {
		log.debug("Parsing id-file");

		if (this.idFileFormat.equals(ID_FILE_FORMAT.ID)) {
			final TextFileValueUniqueListParser p = new TextFileValueUniqueListParser(
					this.absIdFilePath, new int[]{}, new int[]{0}, false);
			p.process();
			final List<String> values = p.getList();
			for (final String value : values) {
				this.keyToId.put(value, this.keyToId.keySet().size());
				this.idToKey.put(this.keyToId.keySet().size() - 1, value);
			}
			this.ids = values;
		} else if (this.idFileFormat.equals(ID_FILE_FORMAT.ID_TAG)) {
			final TextFileValueUniqueListParser p = new TextFileValueUniqueListParser(
					this.absIdFilePath, new int[]{}, new int[]{0});
			p.process();
			final List<String> values = p.getList();
			for (final String value : values) {
				this.keyToId.put(value, this.keyToId.keySet().size());
				this.idToKey.put(this.keyToId.keySet().size() - 1, value);
			}
			this.ids = values;
		}

		log.debug("Finished parsing id-file");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.TextFileParser#processLine(java.lang.String[],
	 * java.lang.String[])
	 */
	@Override
	protected void processLine(String[] key, String[] value) {
		// Do nothing by default
	}
}
