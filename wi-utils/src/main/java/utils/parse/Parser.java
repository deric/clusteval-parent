/**
 * 
 */
package utils.parse;

/**
 * @author Christian Wiwie
 * @param <T>
 * 
 */
public abstract class Parser<T> {

	protected String input;
	protected int pos;
	protected boolean endReached;

	public Parser(final String input) {
		super();
		this.input = input;
	}

	public abstract T parse();

	protected void match(final char token) {
		if (endReached) {
			throw new IllegalArgumentException("Unexpected token '" + token
					+ "' in input at position " + this.pos);
		}
		nextToken();
	}

	/**
	 * Sets the lookahead to the next character in the input string.
	 */
	protected void nextToken() {
		if (pos + 1 < input.length()) {
			pos++;
		} else {
			pos++;
			endReached = true;
		}
	}
}
