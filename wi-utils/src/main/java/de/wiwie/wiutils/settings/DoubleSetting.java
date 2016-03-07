package de.wiwie.wiutils.settings;

import java.io.Serializable;
import java.text.ParseException;

import de.wiwie.wiutils.settings.model.DoubleSettingModel;
import de.wiwie.wiutils.settings.model.ISettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class DoubleSetting extends NumberSetting<Double>
		implements
			Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7922187290695781156L;

	/**
	 * @param name_
	 */
	public DoubleSetting(final String name_) {
		this(name_, name_, null);
	}

	/**
	 * @param name_
	 * @param title
	 */
	public DoubleSetting(final String name_, final String title) {
		super(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public DoubleSetting(final String name_, final String title,
			final Double initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public DoubleSetting(NumberSetting<Double> otherSetting) {
		super(otherSetting);

	}

	@Override
	public void parseValueFromString(final String rawString)
			throws ParseException {
		try {
			this.setValue(Double.valueOf(rawString));
		} catch (final NumberFormatException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getType()
	 */
	@Override
	public de.wiwie.wiutils.settings.Setting.SETTING_TYPE getType() {
		return SETTING_TYPE.DOUBLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMinValue()
	 */
	@Override
	protected Double getDefaultMinValue() {
		return Double.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMaxValue()
	 */
	@Override
	protected Double getDefaultMaxValue() {
		return Double.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<Double> getNewModel() {
		return new DoubleSettingModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#clone()
	 */
	@Override
	public DoubleSetting clone() {
		return new DoubleSetting(this);
	}
}
