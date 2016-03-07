/**
 * 
 */
package de.wiwie.wiutils.settings.model;

import java.io.Serializable;

import de.wiwie.wiutils.settings.Setting;

/**
 * @author Christian Wiwie
 * 
 * @param <DATA>
 */
public abstract class GeneralSettingModel<DATA>
		extends
			ActionSettingModel<DATA> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1384105089629535425L;

	/**
	 * @param setting
	 * 
	 */
	public GeneralSettingModel(Setting<?> setting) {
		super(setting);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.ISettingModel#getValue()
	 */
	@Override
	public Object getValue() {
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.ISettingModel#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object newValue) {
		this.value = (DATA) newValue;
	}
}