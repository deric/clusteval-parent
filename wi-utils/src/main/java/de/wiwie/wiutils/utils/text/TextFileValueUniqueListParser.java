package de.wiwie.wiutils.utils.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.clusteval.utils.TextFileParser;

// TODO: Auto-generated Javadoc
/**
 * The Class TextFileValueUniqueListParser.
 */
public class TextFileValueUniqueListParser extends TextFileParser {

	/** The values. */
	protected List<String> values;

	/**
	 * Instantiates a new text file value unique list parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param keyColumnIds
	 *            the key column ids
	 * @param valueColumnIds
	 *            the value column ids
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public TextFileValueUniqueListParser(final String absFilePath,
			final int[] keyColumnIds, final int[] valueColumnIds)
			throws IOException {
		super(absFilePath, keyColumnIds, valueColumnIds, true, null, null, null);
		this.values = new ArrayList<String>();
	}

	/**
	 * Instantiates a new text file value unique list parser.
	 * 
	 * @param absFilePath
	 *            the abs file path
	 * @param keyColumnIds
	 *            the key column ids
	 * @param valueColumnIds
	 *            the value column ids
	 * @param splitLines
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public TextFileValueUniqueListParser(final String absFilePath,
			final int[] keyColumnIds, final int[] valueColumnIds,
			final boolean splitLines) throws IOException {
		super(absFilePath, keyColumnIds, valueColumnIds, splitLines, null,
				null, null);
		this.values = new ArrayList<String>();
	}

	/**
	 * Gets the list.
	 * 
	 * @return the list
	 */
	public List<String> getList() {
		return this.values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.TextFileParser#process()
	 */
	@Override
	public TextFileValueUniqueListParser process() throws IOException {
		super.process();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.TextFileParser#processLine(java.lang.String[],
	 * java.lang.String[])
	 */
	@Override
	protected void processLine(final String[] key, final String[] value) {
		for (final String val : value) {
			if (!this.values.contains(val)) {
				this.values.add(val);
			}
		}
	}
}