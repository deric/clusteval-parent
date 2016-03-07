package settings;

import java.io.Serializable;
import java.text.ParseException;

import settings.model.FloatSettingModel;
import settings.model.ISettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class FloatSetting extends NumberSetting<Float> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2393171458172256210L;

	/**
	 * @param name_
	 */
	public FloatSetting(final String name_) {
		this(name_, name_, null);
	}

	/**
	 * @param name_
	 * @param title
	 */
	public FloatSetting(final String name_, final String title) {
		super(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public FloatSetting(final String name_, final String title,
			final Float initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public FloatSetting(NumberSetting<Float> otherSetting) {
		super(otherSetting);

	}

	@Override
	public void parseValueFromString(final String rawString)
			throws ParseException {
		try {
			this.setValue(Float.valueOf(rawString));
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
		return SETTING_TYPE.FLOAT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMinValue()
	 */
	@Override
	protected Float getDefaultMinValue() {
		return Float.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.NumberSetting#getDefaultMaxValue()
	 */
	@Override
	protected Float getDefaultMaxValue() {
		return Float.MAX_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<Float> getNewModel() {
		return new FloatSettingModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#clone()
	 */
	@Override
	public FloatSetting clone() {
		return new FloatSetting(this);
	}

}