/**
 *
 */
package de.clusteval.utils;

import de.clusteval.utils.TextFileParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class TextFileMapParser.
 *
 * @author Christian Wiwie
 */
public class TextFileMapParser extends TextFileParser {

    /** The second column. */
    protected int firstColumn, secondColumn;

    /** The result. */
    protected Map<String, String> result;

    /**
     * Instantiates a new text file map parser.
     *
     * @param absFilePath  the abs file path
     * @param firstColumn  the first column
     * @param secondColumn the second column
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TextFileMapParser(final String absFilePath, final int firstColumn,
            final int secondColumn) throws IOException {
        super(absFilePath, new int[0], new int[]{0, 1});
        this.result = new HashMap<>();
        this.firstColumn = firstColumn;
        this.secondColumn = secondColumn;
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.parse.TextFileParser#processLine(java.lang.String[],
     * java.lang.String[])
     */
    @Override
    protected void processLine(String[] key, String[] value) {
        this.result.put(value[this.firstColumn], value[this.secondColumn]);
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public Map<String, String> getResult() {
        return result;
    }
}
