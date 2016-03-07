/**
 * 
 */
package de.wiwie.wiutils.settings.model;

import de.wiwie.wiutils.settings.Setting;

/**
 * @author Christian Wiwie
 * 
 */
public class LongSettingModel extends NumberSettingModel<Long> {

	/**
	 * @param setting
	 */
	public LongSettingModel(Setting<?> setting) {
		super(setting);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7151861492031187619L;
}