/**
 * 
 */
package de.wiwie.wiutils.utils.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Christian Wiwie
 * 
 */
public class StringGroupParser extends Parser<StringGroupNode> {

	protected char leftDelimiter;
	protected char rightDelimiter;

	Pattern p;

	/**
	 * @param leftDelimiter
	 * @param rightDelimiter
	 */
	public StringGroupParser(final String input, final char leftDelimiter,
			final char rightDelimiter) {
		super(input);
		this.leftDelimiter = leftDelimiter;
		this.rightDelimiter = rightDelimiter;
		p = Pattern.compile("[^\\" + leftDelimiter + "\\" + rightDelimiter
				+ "]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.parse.Parser#parse(java.lang.String)
	 */
	@Override
	public StringGroupNode parse() {
		List<StringGroupNode> childs = new ArrayList<StringGroupNode>();
		StringGroupNode root = new StringGroupNode(0, input.length(), input,
				childs);

		s1(root);

		return root;
	}

	protected void s1(final StringGroupNode node) {
		if (!endReached && input.charAt(pos) == leftDelimiter) {
			StringGroupNode child = new StringGroupNode();
			node.addChild(child);

			child.setStart(pos);
			match(leftDelimiter);

			s1(child);

			match(rightDelimiter);
			child.setEnd(pos);

			child.setText(input.substring(child.startPos, child.endPos));

			s1(node);
		} else {
			s1_strich(node);
		}
	}

	protected void s1_strich(final StringGroupNode node) {
		if (!endReached && p.matcher(input.charAt(pos) + "").matches()) {
			match(input.charAt(pos));
			s1(node);
		} else {
			// epsilon
		}
	}

}
