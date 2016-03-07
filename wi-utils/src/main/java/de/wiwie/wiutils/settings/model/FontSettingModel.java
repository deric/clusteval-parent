/**
 * 
 */
package de.wiwie.wiutils.settings.model;

import java.awt.Font;

import javax.swing.Action;

import de.wiwie.wiutils.settings.Setting;

/**
 * @author Christian Wiwie
 * 
 */
public class FontSettingModel extends GeneralSettingModel<Font> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1972553437502245081L;

	/**
	 * @param setting
	 */
	public FontSettingModel(Setting<?> setting) {
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
		// TODO
		return null;
	}

}
