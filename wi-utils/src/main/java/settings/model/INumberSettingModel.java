/**
 * 
 */
package settings.model;

import javax.swing.JComponent;

/**
 * @author Christian Wiwie
 * 
 * @param <DATA>
 */
public interface INumberSettingModel<DATA> extends ISettingModel<DATA> {

	Comparable getMinimum();

	Comparable getMaximum();

	void setMaximum(Comparable newMax);

	void setMinimum(Comparable newMin);
}