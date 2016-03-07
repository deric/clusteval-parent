package de.wiwie.wiutils.settings;

import java.io.Serializable;
import java.text.ParseException;

import de.wiwie.wiutils.settings.model.ISettingModel;
import de.wiwie.wiutils.settings.model.StringSettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class StringSetting extends Setting<String> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9058079845959543506L;

	/**
	 * @param name_
	 */
	public StringSetting(final String name_) {
		this(name_, name_, null);
	}

	/**
	 * @param name_
	 * @param title
	 */
	public StringSetting(final String name_, final String title) {
		super(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public StringSetting(final String name_, final String title,
			final String initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public StringSetting(final Setting<String> otherSetting) {
		super(otherSetting);
	}

	@Override
	public StringSetting clone() {
		return new StringSetting(this);
	}

	@Override
	public void parseValueFromString(final String rawString)
			throws ParseException {
		this.setValue(rawString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getType()
	 */
	@Override
	public de.wiwie.wiutils.settings.Setting.SETTING_TYPE getType() {
		return SETTING_TYPE.STRING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<String> getNewModel() {
		return new StringSettingModel(this);
	}
}
