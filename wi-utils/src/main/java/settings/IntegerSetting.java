package settings;

import java.io.Serializable;
import java.text.ParseException;

import settings.model.ISettingModel;
import settings.model.IntegerSettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class IntegerSetting extends NumberSetting<Integer>
		implements
			Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1281045310450341788L;

	/**
	 * @param name_
	 */
	public IntegerSetting(final String name_) {
		this(name_, name_, null);
	}

	/**
	 * @param name_
	 * @param title
	 */
	public IntegerSetting(final String name_, final String title) {
		this(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public IntegerSetting(final String name_, final String title,
			final Integer initValue) {
		this(name_, title, initValue, null, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 * @param minValue
	 * @param maxValue
	 */
	public IntegerSetting(final String name_, final String title,
			final Integer initValue, final Integer minValue,
			final Integer maxValue) {
		super(name_, title, initValue, minValue, maxValue);
	}

	/**
	 * @param otherSetting
	 */
	public IntegerSetting(NumberSetting<Integer> otherSetting) {
		super(otherSetting);

	}

	@Override
	public void parseValueFromString(final String rawString)
			throws ParseException {
		try {
			this.model.setValue(Integer.valueOf(rawString));
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
	public settings.Setting.SETTING_TYPE getType() {
		return SETTING_TYPE.INT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMinValue()
	 */
	@Override
	protected Integer getDefaultMinValue() {
		return Integer.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMaxValue()
	 */
	@Override
	protected Integer getDefaultMaxValue() {
		return Integer.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<Integer> getNewModel() {
		return new IntegerSettingModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#clone()
	 */
	@Override
	public IntegerSetting clone() {
		return new IntegerSetting(this);
	}
}
