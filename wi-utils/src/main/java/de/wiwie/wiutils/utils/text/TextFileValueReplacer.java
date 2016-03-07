package de.wiwie.wiutils.utils.text;

import java.io.IOException;

import de.wiwie.wiutils.utils.parse.TextFileParser;

// TODO: Auto-generated Javadoc
/**
 * The Class TextFileValueReplacer.
 */
public abstract class TextFileValueReplacer extends TextFileParser {

	/**
	 * Instantiates a new text file value replacer.
	 *
	 * @param absFilePath the abs file path
	 * @param keyColumnIds the key column ids
	 * @param valueColumnIds the value column ids
	 * @param absOutputFile the abs output file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public TextFileValueReplacer(final String absFilePath,
			final int[] keyColumnIds, final int[] valueColumnIds,
			final String absOutputFile) throws IOException {
		super(absFilePath, keyColumnIds, valueColumnIds, absOutputFile,
				OUTPUT_MODE.STREAM);
	}

	/**
	 * Instantiates a new text file value replacer.
	 *
	 * @param absFilePath the abs file path
	 * @param absOutputFile the abs output file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public TextFileValueReplacer(final String absFilePath,
			final String absOutputFile) throws IOException {
		this(absFilePath, null, null, absOutputFile);
	}

	/* (non-Javadoc)
	 * @see utils.parse.TextFileParser#getLineOutput(java.lang.String[], java.lang.String[])
	 */
	@Override
	protected String getLineOutput(final String[] key, final String[] value) {
		return key[0] + this.outSplit + key[1] + this.outSplit
				+ this.getReplacementForValue(this.combineColumns(value[0]))
				+ "\n";
	}

	/**
	 * Gets the replacement for value.
	 *
	 * @param value the value
	 * @return the replacement for value
	 */
	protected abstract String getReplacementForValue(String value);
}
