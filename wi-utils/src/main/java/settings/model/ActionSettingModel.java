/**
 * 
 */
package settings.model;

import java.io.Serializable;

import javax.swing.Action;

import settings.Setting;

/**
 * @author Christian Wiwie
 * @param <DATA>
 * 
 */
public abstract class ActionSettingModel<DATA>
		implements
			ISettingModel<DATA>,
			Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6370167238768629116L;
	protected Setting<?> setting;
	protected Action action;
	protected DATA value;

	/**
	 * @param setting
	 * 
	 */
	public ActionSettingModel(Setting<?> setting) {
		super();
		this.setting = setting;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getAction()
	 */
	@Override
	public Action getAction() {
		if (action == null) {
			if (setting.isRestricted()) {
				action = new RestrictedSettingAction(this);
			} else
				action = getTypeSpecificAction();
		}
		return action;
	}
}
