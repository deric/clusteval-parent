/**
 * 
 */
package settings.model;

import settings.Setting;

/**
 * @author Christian Wiwie
 * 
 */
public class IntegerSettingModel extends NumberSettingModel<Integer> {

	/**
	 * @param setting
	 */
	public IntegerSettingModel(Setting<?> setting) {
		super(setting);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2666151557164296375L;
}