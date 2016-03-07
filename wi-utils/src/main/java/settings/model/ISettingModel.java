/**
 * 
 */
package settings.model;

import javax.swing.Action;

/**
 * @author Christian Wiwie
 * @param <DATA>
 * 
 */
public interface ISettingModel<DATA> {

	/**
	 * @return
	 */
	Object getValue();

	/**
	 * @param newValue
	 */
	void setValue(Object newValue);

	/**
	 * @return
	 * 
	 */
	Action getAction();

	/**
	 * @return
	 */
	Action getTypeSpecificAction();

	/**
	 * @return
	 */
	Object getUIModel();
}