package de.wiwie.wiutils.settings;

import java.io.Serializable;
import java.text.ParseException;

import de.wiwie.wiutils.settings.model.ISettingModel;
import de.wiwie.wiutils.settings.model.LongSettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class LongSetting extends NumberSetting<Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6414432191151600196L;

	/**
	 * @param name_
	 */
	public LongSetting(final String name_) {
		this(name_, name_, null);
	}
	/**
	 * @param name_
	 * @param title
	 */
	public LongSetting(final String name_, final String title) {
		super(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public LongSetting(final String name_, final String title,
			final Long initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public LongSetting(NumberSetting<Long> otherSetting) {
		super(otherSetting);

	}

	@Override
	public void parseValueFromString(final String rawString)
			throws ParseException {
		try {
			this.model.setValue(Long.valueOf(rawString));
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
		return SETTING_TYPE.LONG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMinValue()
	 */
	@Override
	protected Long getDefaultMinValue() {
		return Long.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMaxValue()
	 */
	@Override
	protected Long getDefaultMaxValue() {
		return Long.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<Long> getNewModel() {
		return new LongSettingModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#clone()
	 */
	@Override
	public LongSetting clone() {
		return new LongSetting(this);
	}
}
