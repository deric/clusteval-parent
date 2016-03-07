package de.wiwie.wiutils.settings;

import java.io.Serializable;

import de.wiwie.wiutils.settings.model.NumberSettingModel;

/**
 * @author Christian Wiwie
 * 
 * @param <DATA>
 */
public abstract class NumberSetting<DATA extends Number> extends Setting<DATA>
		implements
			Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2061473384709364529L;

	/**
	 * @param name_
	 * @param title
	 */
	public NumberSetting(final String name_, final String title) {
		this(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public NumberSetting(final String name_, final String title,
			final DATA initValue) {
		this(name_, title, initValue, null, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 * @param minValue
	 * @param maxValue
	 */
	public NumberSetting(final String name_, final String title,
			final DATA initValue, final DATA minValue, final DATA maxValue) {
		super(name_, title, initValue);

		if (minValue != null)
			setMinimum(minValue);
		else
			setMinimum(getDefaultMinValue());

		if (maxValue != null)
			setMaximum(maxValue);
		else
			setMaximum(getDefaultMaxValue());
	}

	/**
	 * @param otherSetting
	 */
	public NumberSetting(final NumberSetting<DATA> otherSetting) {
		this(otherSetting.id, otherSetting.title,
				(DATA) ((NumberSettingModel) otherSetting.model).getValue(),
				(DATA) ((NumberSettingModel) otherSetting.model).getMinimum(),
				(DATA) ((NumberSettingModel) otherSetting.model).getMaximum());
	}

	/**
	 * @param minValue
	 */
	public void setMinimum(DATA minValue) {
		NumberSettingModel<DATA> model = (NumberSettingModel<DATA>) this.model;
		model.setMinimum((Comparable<DATA>) minValue);
	}

	/**
	 * @param maxValue
	 */
	public void setMaximum(DATA maxValue) {
		NumberSettingModel<DATA> model = (NumberSettingModel<DATA>) this.model;
		model.setMaximum((Comparable<DATA>) maxValue);
	}

	protected abstract DATA getDefaultMinValue();

	/**
	 * @return
	 */
	public DATA getMinValue() {
		NumberSettingModel<DATA> model = (NumberSettingModel<DATA>) this.model;
		return (DATA) model.getMinimum();
	}

	protected abstract DATA getDefaultMaxValue();

	/**
	 * @return
	 */
	public DATA getMaxValue() {
		NumberSettingModel<DATA> model = (NumberSettingModel<DATA>) this.model;
		return (DATA) model.getMaximum();
	}
}