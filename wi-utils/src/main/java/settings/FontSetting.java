package settings;

import java.awt.Font;
import java.io.Serializable;
import java.text.ParseException;

import settings.model.FontSettingModel;
import settings.model.ISettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class FontSetting extends Setting<Font> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6505978292234731437L;

	/**
	 * @param name_
	 */
	public FontSetting(final String name_) {
		this(name_, name_);
	}

	/**
	 * @param name_
	 * @param title
	 */
	public FontSetting(final String name_, final String title) {
		this(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public FontSetting(final String name_, final String title,
			final Font initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public FontSetting(final Setting<Font> otherSetting) {
		super(otherSetting);
	}

	@Override
	public void parseValueFromString(String rawString) throws ParseException {
		this.setValue(Font.decode(rawString));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getType()
	 */
	@Override
	public settings.Setting.SETTING_TYPE getType() {
		return SETTING_TYPE.FONT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<Font> getNewModel() {
		return new FontSettingModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#clone()
	 */
	@Override
	public FontSetting clone() {
		return new FontSetting(this);
	}
}