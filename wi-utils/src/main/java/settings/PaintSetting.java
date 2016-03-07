package settings;

import java.awt.Paint;
import java.io.Serializable;
import java.text.ParseException;

import settings.model.ISettingModel;
import settings.model.PaintSettingModel;

/**
 * @author Christian Wiwie
 * 
 */
public class PaintSetting extends Setting<Paint> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4233074057531694750L;

	/**
	 * @param name_
	 */
	public PaintSetting(final String name_) {
		this(name_, name_);
	}

	/**
	 * @param name_
	 * @param title
	 */
	public PaintSetting(final String name_, final String title) {
		this(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public PaintSetting(final String name_, final String title,
			final Paint initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public PaintSetting(final Setting<Paint> otherSetting) {
		super(otherSetting);
	}

	@Override
	public void parseValueFromString(String rawString) throws ParseException {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getType()
	 */
	@Override
	public settings.Setting.SETTING_TYPE getType() {
		return SETTING_TYPE.PAINT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#getNewModel()
	 */
	@Override
	protected ISettingModel<Paint> getNewModel() {
		return new PaintSettingModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.Setting#clone()
	 */
	@Override
	public PaintSetting clone() {
		return new PaintSetting(this);
	}
}