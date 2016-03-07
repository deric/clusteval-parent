/**
 * 
 */
package de.wiwie.wiutils.settings.model;

import javax.swing.AbstractAction;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class AbstractSettingAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7285284138548248398L;

	protected ISettingModel<?> model;

	/**
	 * @param model
	 */
	public AbstractSettingAction(ISettingModel<?> model) {
		super();
		this.model = model;
	}
}
