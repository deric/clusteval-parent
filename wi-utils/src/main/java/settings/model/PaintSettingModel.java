/**
 * 
 */
package settings.model;

import java.awt.Paint;

import javax.swing.Action;

import settings.Setting;

/**
 * @author Christian Wiwie
 * 
 */
public class PaintSettingModel extends GeneralSettingModel<Paint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6240174057694538796L;

	/**
	 * @param setting
	 */
	public PaintSettingModel(Setting<?> setting) {
		super(setting);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.GeneralSettingModel#getTypeSpecificAction()
	 */
	@Override
	public Action getTypeSpecificAction() {
		// TODO
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getUIModel()
	 */
	@Override
	public Object getUIModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
