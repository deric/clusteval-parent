package settings;

import java.io.Serializable;
import java.text.ParseException;

import settings.model.BooleanSettingModel;
import settings.model.ISettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class BooleanSetting extends Setting<Boolean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6177387597645981439L;

	/**
	 * @param name_
	 * @param title
	 */
	public BooleanSetting(final String name_) {
		this(name_, name_, null);
	}
	
	/**
	 * @param name_
	 * @param title
	 */
	public BooleanSetting(final String name_, final String title) {
		this(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public BooleanSetting(final String name_, final String title,
			final Boolean initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public BooleanSetting(final BooleanSetting otherSetting) {
		super(otherSetting);
	}

	@Override
	public void parseValueFromString(String rawString) throws ParseException {
		rawString = rawString.toLowerCase();
		final boolean isTrue = "t".equals(rawString)
				|| "true".equals(rawString) || "y".equals(rawString);
		if (!(isTrue || "f".equals(rawString) || "false".equals(rawString) || "n"
				.equals(rawString))) {
			throw new ParseException("The given string is no boolean value", 0);
		}
		this.model.setValue(isTrue ? true : false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getType()
	 */
	@Override
	public settings.Setting.SETTING_TYPE getType() {
		return SETTING_TYPE.BOOL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<Boolean> getNewModel() {
		return new BooleanSettingModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#clone()
	 */
	@Override
	public Setting<?> clone() {
		return new BooleanSetting(this);
	}
}