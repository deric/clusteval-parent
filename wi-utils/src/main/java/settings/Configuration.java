package settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import settings.Setting.SETTING_TYPE;

/**
 * A class holding all options with corresponding settings.
 * 
 * @author Christian Wiwie
 * 
 */
public class Configuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2676154457640062897L;

	private static Map<String, BooleanSetting> booleanOptions = new HashMap<String, BooleanSetting>();
	private static Map<String, IntegerSetting> integerOptions = new HashMap<String, IntegerSetting>();
	private static Map<String, LongSetting> longOptions = new HashMap<String, LongSetting>();
	private static Map<String, FloatSetting> floatOptions = new HashMap<String, FloatSetting>();
	private static Map<String, DoubleSetting> doubleOptions = new HashMap<String, DoubleSetting>();
	private static Map<String, StringSetting> stringOptions = new HashMap<String, StringSetting>();
	private static Map<String, PathSetting> pathOptions = new HashMap<String, PathSetting>();

	private static Map<String, List<Setting<?>>> optionGroups = new HashMap<String, List<Setting<?>>>();
	private static Map<Setting<?>, String> optionToGroup = new HashMap<Setting<?>, String>();

	private static Map<String, String> optionIdToTitle = new HashMap<String, String>();

	private static void enable(final String opt_) {
		if (!booleanOptions.containsKey(opt_)) {
			throw new IllegalArgumentException("Unknown parameter: " + opt_);
		}
		booleanOptions.get(opt_).setValue(Boolean.TRUE);
	}

	/**
	 * @param opt_
	 * @return
	 */
	public static boolean enabled(final String opt_) {
		if (booleanOptions.containsKey(opt_)) {
			return booleanOptions.get(opt_).getValue();
		}
		return false;
	}

	/**
	 * @param opt_
	 * @return
	 */
	public static BooleanSetting getBooleanSetting(final String opt_) {
		if (booleanOptions.containsKey(opt_)) {
			return booleanOptions.get(opt_);
		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static Boolean getBooleanValue(final String opt_) {
		return getBooleanSetting(opt_).getValue();
	}

	public static DoubleSetting getDoubleSetting(final String opt_) {
		if (doubleOptions.containsKey(opt_)) {
			return doubleOptions.get(opt_);
		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static Double getDoubleValue(final String opt_) {
		return getDoubleSetting(opt_).getValue();
	}

	public static FloatSetting getFloatSetting(final String opt_) {
		if (floatOptions.containsKey(opt_)) {
			return floatOptions.get(opt_);
		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static Float getFloatValue(final String opt_) {
		return getFloatSetting(opt_).getValue();
	}

	public static IntegerSetting getIntegerSetting(final String opt_) {
		if (integerOptions.containsKey(opt_)) {
			return integerOptions.get(opt_);
		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static Integer getIntegerValue(final String opt_) {
		return getIntegerSetting(opt_).getValue();
	}

	public static LongSetting getLongSetting(final String opt_) {
		if (longOptions.containsKey(opt_)) {
			return longOptions.get(opt_);
		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static Long getLongValue(final String opt_) {
		return getLongSetting(opt_).getValue();
	}

	public static PathSetting getPathSetting(final String opt_) {
		if (pathOptions.containsKey(opt_)) {
			return pathOptions.get(opt_);
		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static String getPathValue(final String opt_) {
		return getPathSetting(opt_).getValue();
	}

	public static Setting<?> getSetting(final String opt_) {
		try {
			return getSetting(opt_, SETTING_TYPE.BOOL);
		} catch (final IllegalArgumentException e) {

		}
		try {
			return getSetting(opt_, SETTING_TYPE.INT);
		} catch (final IllegalArgumentException e) {

		}
		try {
			return getSetting(opt_, SETTING_TYPE.LONG);
		} catch (final IllegalArgumentException e) {

		}
		try {
			return getSetting(opt_, SETTING_TYPE.FLOAT);
		} catch (final IllegalArgumentException e) {

		}
		try {
			return getSetting(opt_, SETTING_TYPE.DOUBLE);
		} catch (final IllegalArgumentException e) {

		}
		try {
			return getSetting(opt_, SETTING_TYPE.STRING);
		} catch (final IllegalArgumentException e) {

		}
		try {
			return getSetting(opt_, SETTING_TYPE.PATH);
		} catch (final IllegalArgumentException e) {

		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static StringSetting getStringSetting(final String opt_) {
		if (stringOptions.containsKey(opt_)) {
			return stringOptions.get(opt_);
		}
		throw new IllegalArgumentException("Unknown parameter: " + opt_);
	}

	public static String getStringValue(final String opt_) {
		return getStringSetting(opt_).getValue();
	}

	public static void parseFromCommandLineParams(final String[] args) {
		for (final String rawSetting : args) {
			// we expect every setting to have a '='
			final String[] keyValue = rawSetting.split("=");
			if (keyValue.length != 2 || !keyValue[0].startsWith("-")) {
				throw new IllegalArgumentException(
						"The parameter '"
								+ rawSetting
								+ "' is not valid.\nPlease make sure that you follow the format -key=value");
			}
			keyValue[0] = (keyValue[0].length() > 0
					? keyValue[0].substring(1)
					: "");
			if (keyValue[1].startsWith("\"") || keyValue[1].startsWith("'")) {
				keyValue[1] = keyValue[1].substring(1);
			}
			if (keyValue[1].endsWith("\"") || keyValue[1].endsWith("'")) {
				keyValue[1] = keyValue[1]
						.substring(0, keyValue[1].length() - 1);
			}
			try {
				setValue(keyValue[0], keyValue[1]);
			} catch (final ParseException e) {
				throw new IllegalArgumentException(
						"The value of the parameter '" + keyValue[0]
								+ "' is not valid.\nThe expected type is "
								+ booleanOptions.get(keyValue[0]).getValue());
			}
		}
	}

	/**
	 * Parses settings including their values from a file and stores them.
	 * 
	 * @param string
	 *            The file path.
	 */
	@SuppressWarnings("unchecked")
	public static void parseFromFile(String file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					new File(file)));
			Configuration.booleanOptions = (Map<String, BooleanSetting>) ois
					.readObject();
			Configuration.doubleOptions = (Map<String, DoubleSetting>) ois
					.readObject();
			Configuration.floatOptions = (Map<String, FloatSetting>) ois
					.readObject();
			Configuration.integerOptions = (Map<String, IntegerSetting>) ois
					.readObject();
			Configuration.longOptions = (Map<String, LongSetting>) ois
					.readObject();
			Configuration.pathOptions = (Map<String, PathSetting>) ois
					.readObject();
			Configuration.stringOptions = (Map<String, StringSetting>) ois
					.readObject();
			Configuration.optionGroups = (Map<String, List<Setting<?>>>) ois
					.readObject();
			Configuration.optionIdToTitle = (Map<String, String>) ois
					.readObject();
			Configuration.optionToGroup = (Map<Setting<?>, String>) ois
					.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes all settings including their values to a file.
	 * 
	 * @param file
	 *            The absolute file path.
	 */
	public static void writeToFile(String file) {
		try {
			ObjectOutputStream ois = new ObjectOutputStream(
					new FileOutputStream(new File(file)));
			ois.writeObject(Configuration.booleanOptions);
			ois.writeObject(Configuration.doubleOptions);
			ois.writeObject(Configuration.floatOptions);
			ois.writeObject(Configuration.integerOptions);
			ois.writeObject(Configuration.longOptions);
			ois.writeObject(Configuration.pathOptions);
			ois.writeObject(Configuration.stringOptions);
			ois.writeObject(Configuration.optionGroups);
			ois.writeObject(Configuration.optionIdToTitle);
			ois.writeObject(Configuration.optionToGroup);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes the given setting
	 * 
	 * @param opt_
	 * @param settingType
	 */
	public static void remove(final String opt_, final SETTING_TYPE settingType) {
		if (settingType.equals(SETTING_TYPE.BOOL)) {
			booleanOptions.remove(opt_);
		} else if (settingType.equals(SETTING_TYPE.INT)) {
			integerOptions.remove(opt_);
		} else if (settingType.equals(SETTING_TYPE.LONG)) {
			longOptions.remove(opt_);
		} else if (settingType.equals(SETTING_TYPE.FLOAT)) {
			floatOptions.remove(opt_);
		} else if (settingType.equals(SETTING_TYPE.DOUBLE)) {
			doubleOptions.remove(opt_);
		} else if (settingType.equals(SETTING_TYPE.STRING)) {
			stringOptions.remove(opt_);
		} else if (settingType.equals(SETTING_TYPE.PATH)) {
			pathOptions.remove(opt_);
		}
	}

	/**
	 * @param newOption
	 * @return
	 */
	public static Setting<?> register(Setting<?> newOption) {
		SETTING_TYPE settingType = newOption.getType();
		String optionId = newOption.getId();

		if (settingType.equals(SETTING_TYPE.BOOL)) {
			booleanOptions.put(optionId, (BooleanSetting) newOption);
		} else if (settingType.equals(SETTING_TYPE.INT)) {
			integerOptions.put(optionId, (IntegerSetting) newOption);
		} else if (settingType.equals(SETTING_TYPE.LONG)) {
			longOptions.put(optionId, (LongSetting) newOption);
		} else if (settingType.equals(SETTING_TYPE.FLOAT)) {
			floatOptions.put(optionId, (FloatSetting) newOption);
		} else if (settingType.equals(SETTING_TYPE.DOUBLE)) {
			doubleOptions.put(optionId, (DoubleSetting) newOption);
		} else if (settingType.equals(SETTING_TYPE.STRING)) {
			stringOptions.put(optionId, (StringSetting) newOption);
		} else if (settingType.equals(SETTING_TYPE.PATH)) {
			pathOptions.put(optionId, (PathSetting) newOption);
		} else {
			// should never happen
			throw new IllegalArgumentException("Invalid settings type");
		}
		optionIdToTitle.put(optionId, newOption.getTitle());
		return newOption;
	}

	/**
	 * @param optionId
	 * @param settingType
	 * @return
	 */
	public static Setting<?> register(final String optionId,
			final SETTING_TYPE settingType) {
		return register(optionId, optionId, settingType);
	}

	/**
	 * Registers a new setting with the given name, title and type.
	 * 
	 * @param optionId
	 * @param optionTitle
	 * @param settingType
	 * @return
	 */
	public static Setting<?> register(final String optionId,
			final String optionTitle, final SETTING_TYPE settingType) {
		if (settingType.equals(SETTING_TYPE.BOOL)) {
			return register(new BooleanSetting(optionId, optionTitle));
		} else if (settingType.equals(SETTING_TYPE.INT)) {
			return register(new IntegerSetting(optionId, optionTitle));
		} else if (settingType.equals(SETTING_TYPE.LONG)) {
			return register(new LongSetting(optionId, optionTitle));
		} else if (settingType.equals(SETTING_TYPE.FLOAT)) {
			return register(new FloatSetting(optionId, optionTitle));
		} else if (settingType.equals(SETTING_TYPE.DOUBLE)) {
			return register(new DoubleSetting(optionId, optionTitle));
		} else if (settingType.equals(SETTING_TYPE.STRING)) {
			return register(new StringSetting(optionId, optionTitle));
		} else if (settingType.equals(SETTING_TYPE.PATH)) {
			return register(new PathSetting(optionId, optionTitle));
		} else {
			// should never happen
			throw new IllegalArgumentException("Invalid settings type");
		}
	}

	/**
	 * @param optionId
	 * @param settingType
	 * @return
	 */
	public static Setting<?> getSetting(String optionId,
			SETTING_TYPE settingType) {
		if (settingType.equals(SETTING_TYPE.BOOL)) {
			return getBooleanSetting(optionId);
		} else if (settingType.equals(SETTING_TYPE.INT)) {
			return getIntegerSetting(optionId);
		} else if (settingType.equals(SETTING_TYPE.LONG)) {
			return getLongSetting(optionId);
		} else if (settingType.equals(SETTING_TYPE.FLOAT)) {
			return getFloatSetting(optionId);
		} else if (settingType.equals(SETTING_TYPE.DOUBLE)) {
			return getDoubleSetting(optionId);
		} else if (settingType.equals(SETTING_TYPE.STRING)) {
			return getStringSetting(optionId);
		} else if (settingType.equals(SETTING_TYPE.PATH)) {
			return getPathSetting(optionId);
		}
		throw new IllegalArgumentException("Invalid setting type: "
				+ settingType);
	}

	/**
	 * @param opt_
	 * @param settingType
	 * @return
	 */
	public static boolean isRegistered(final String opt_,
			final SETTING_TYPE settingType) {
		if (settingType.equals(SETTING_TYPE.BOOL)) {
			return booleanOptions.containsKey(opt_);
		} else if (settingType.equals(SETTING_TYPE.INT)) {
			return integerOptions.containsKey(opt_);
		} else if (settingType.equals(SETTING_TYPE.LONG)) {
			return longOptions.containsKey(opt_);
		} else if (settingType.equals(SETTING_TYPE.FLOAT)) {
			return floatOptions.containsKey(opt_);
		} else if (settingType.equals(SETTING_TYPE.DOUBLE)) {
			return doubleOptions.containsKey(opt_);
		} else if (settingType.equals(SETTING_TYPE.STRING)) {
			return stringOptions.containsKey(opt_);
		} else if (settingType.equals(SETTING_TYPE.PATH)) {
			return pathOptions.containsKey(opt_);
		}
		return false;
	}

	/**
	 * @param optionId
	 * @param settingType
	 */
	public static void registerAndEnable(final String optionId,
			final SETTING_TYPE settingType) {
		registerAndEnable(optionId, optionId, settingType);
	}

	/**
	 * Registers the setting as in
	 * {@link #register(String, String, SETTING_TYPE)}, and then enables it, if
	 * it is of boolean type.
	 * 
	 * @param optionId
	 * @param optionTitle
	 * @param settingType
	 */
	public static void registerAndEnable(final String optionId,
			final String optionTitle, final SETTING_TYPE settingType) {
		register(optionId, optionTitle, settingType);
		if (settingType.equals(SETTING_TYPE.BOOL)) {
			enable(optionId);
		}
	}

	/**
	 * @param optionId
	 * @param settingType
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static Setting<?> registerAndInit(final String optionId,
			final SETTING_TYPE settingType, final String value)
			throws ParseException {
		return registerAndInit(optionId, optionId, settingType, value);
	}

	/**
	 * Registers the setting as in
	 * {@link #register(String, String, SETTING_TYPE)} and then sets the value.
	 * 
	 * @param optionId
	 * @param optionTitle
	 * @param settingType
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static Setting<?> registerAndInit(final String optionId,
			final String optionTitle, final SETTING_TYPE settingType,
			final String value) throws ParseException {
		final Setting<?> result = register(optionId, optionTitle, settingType);
		setValue(optionId, value);
		return result;
	}

	/**
	 * @param opt_
	 * @param value
	 */
	public static void setBooleanValue(final String opt_, final Boolean value) {
		getBooleanSetting(opt_).setValue(value);
	}

	/**
	 * @param opt_
	 * @param value
	 */
	public static void setDoubleValue(final String opt_, final Double value) {
		getDoubleSetting(opt_).setValue(value);
	}

	/**
	 * @param opt_
	 * @param value
	 */
	public static void setFloatValue(final String opt_, final Float value) {
		getFloatSetting(opt_).setValue(value);
	}

	/**
	 * @param opt_
	 * @param value
	 */
	public static void setIntegerValue(final String opt_, final Integer value) {
		getIntegerSetting(opt_).setValue(value);
	}

	/**
	 * @param opt_
	 * @param value
	 */
	public static void setLongValue(final String opt_, final Long value) {
		getLongSetting(opt_).setValue(value);
	}

	/**
	 * @param opt_
	 * @param value
	 */
	public static void setPathValue(final String opt_, final String value) {
		getPathSetting(opt_).setValue(value);
	}

	/**
	 * @param opt_
	 * @param value
	 */
	public static void setStringValue(final String opt_, final String value) {
		getStringSetting(opt_).setValue(value);
	}

	/**
	 * @param opt_
	 * @param value
	 * @throws ParseException
	 */
	public static void setValue(final String opt_, final String value)
			throws ParseException {
		final Setting<?> setting = getSetting(opt_);
		setting.parseValueFromString(value);
	}

	/**
	 * The setting is not registered, if it already existed.
	 * 
	 * @param optionId
	 * @param optionTitle
	 * @param type
	 */
	public static Setting<?> tryRegister(final String optionId,
			final String optionTitle, SETTING_TYPE type) {
		if (!Configuration.isRegistered(optionId, type))
			return Configuration.register(optionId, optionTitle, type);
		return Configuration.getSetting(optionId, type);
	}

	/**
	 * The setting is not registered, if it already existed. The value is
	 * updated in any case.
	 * 
	 * @param optionId
	 * @param optionTitle
	 * @param type
	 * @param value
	 * @throws ParseException
	 */
	public static Setting<?> tryRegisterAndUpdate(final String optionId,
			final String optionTitle, SETTING_TYPE type, String value)
			throws ParseException {
		Setting<?> result = tryRegister(optionId, optionTitle, type);
		setValue(optionId, value);
		return result;
	}

	/**
	 * First checks whether this option has already been registered and set.
	 * Only if not, it will be registered and initialized now.
	 * 
	 * @param optionId
	 * @param optionTitle
	 * @param type
	 * @param value
	 * 
	 * @param string
	 * @param bool
	 * @param string2
	 * @return
	 * @throws ParseException
	 */
	public static Setting<?> tryRegisterAndInit(String optionId,
			String optionTitle, SETTING_TYPE type, String value)
			throws ParseException {
		if (!Configuration.isRegistered(optionId, type)) {
			return Configuration.registerAndInit(optionId, optionTitle, type,
					value);
		}
		return null;
	}

	/**
	 * You can group your settings.
	 * 
	 * @param groupName
	 * @param option
	 */
	public static void addOptionToGroup(String groupName, Setting<?> option) {
		if (optionToGroup.containsKey(option)) {
			optionGroups.get(optionToGroup.remove(option)).remove(option);
		}

		if (!optionGroups.containsKey(groupName))
			optionGroups.put(groupName, new ArrayList<Setting<?>>());
		optionGroups.get(groupName).add(option);
		optionToGroup.put(option, groupName);
	}

	/**
	 * @return
	 */
	public static Iterator<Entry<String, List<Setting<?>>>> groupIterator() {
		return optionGroups.entrySet().iterator();
	}
}