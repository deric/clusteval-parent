/**
 * 
 */
package utils.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Wiwie
 * 
 */
public class StringGroupNode extends AbstractNode<StringGroupNode> {

	protected int startPos;
	protected int endPos;
	protected String completeString;

	/**
	 */
	public StringGroupNode() {
		super(new ArrayList<StringGroupNode>());
	}

	/**
	 * @param start
	 * @param end
	 * @param text
	 * @param childs
	 */
	public StringGroupNode(final int start, final int end, final String text,
			List<StringGroupNode> childs) {
		super(childs);
		this.startPos = start;
		this.endPos = end;
		this.completeString = text;
	}

	public void setStart(final int start) {
		this.startPos = start;
	}

	public void setEnd(final int end) {
		this.endPos = end;
	}

	public void setText(final String text) {
		this.completeString = text;
	}

	public void addChild(final StringGroupNode child) {
		this.childs.add(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.completeString);
		if (this.childs.size() > 0) {
			sb.append(" -> {");
			for (StringGroupNode child : this.childs) {
				sb.append(child.toString());
				sb.append(", ");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.append("}");
		}
		return sb.toString();
	}
}
