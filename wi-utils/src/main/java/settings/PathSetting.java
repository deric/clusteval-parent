package settings;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;

/**
 * @author Christian Wiwie
 * 
 */
public class PathSetting extends StringSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1633994458029944078L;

	/**
	 * @param name_
	 */
	public PathSetting(final String name_) {
		this(name_, name_, null);
	}

	/**
	 * @param name_
	 * @param title
	 */
	public PathSetting(final String name_, final String title) {
		this(name_, title, null);
	}

	/**
	 * @param name_
	 * @param title
	 * @param initValue
	 */
	public PathSetting(final String name_, final String title,
			final String initValue) {
		super(name_, title, initValue);
	}

	/**
	 * @param otherSetting
	 */
	public PathSetting(final Setting<String> otherSetting) {
		super(otherSetting);
	}

	@Override
	public void parseValueFromString(final String rawString)
			throws ParseException {
		try {
			this.model.setValue(new File(rawString).getCanonicalPath());
		} catch (final IOException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	@Override
	public SETTING_TYPE getType() {
		return SETTING_TYPE.PATH;
	}
}
