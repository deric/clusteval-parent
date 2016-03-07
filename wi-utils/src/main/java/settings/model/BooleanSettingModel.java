/**
 * 
 */
package settings.model;

import javax.swing.Action;

import settings.Setting;

/**
 * @author Christian Wiwie
 * 
 */
public class BooleanSettingModel extends GeneralSettingModel<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3808531179410497824L;

	/**
	 * @param setting
	 */
	public BooleanSettingModel(Setting<?> setting) {
		super(setting);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.GeneralSettingModel#getTypeSpecificAction()
	 */
	@Override
	public Action getTypeSpecificAction() {
		return new BooleanSettingAction(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getUIModel()
	 */
	@Override
	public Object getUIModel() {
		return this.getAction();
	}

}
