/**
 * 
 */
package de.wiwie.wiutils.utils.parse;

import java.util.List;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class AbstractNode<T extends AbstractNode> {

	protected List<T> childs;

	public AbstractNode(final List<T> childs) {
		super();
		this.childs = childs;
	}

	public List<T> getChilds() {
		return this.childs;
	}

}
