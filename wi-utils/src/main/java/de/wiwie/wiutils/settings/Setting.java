package de.wiwie.wiutils.settings;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.wiwie.wiutils.settings.model.ISettingModel;

/**
 * @author Christian Wiwie
 * 
 * @param <DATA>
 */
public abstract class Setting<DATA> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7956147066435154493L;

	public enum SETTING_TYPE {
		BOOL, INT, LONG, FLOAT, DOUBLE, STRING, PATH, FONT, PAINT
	}

	public final static char SETTING_ID_SEPARATOR = '/';

	protected String id;

	protected String title;

	protected ISettingModel<DATA> model;
	// protected DATA val_;

	protected List<DATA> possibleValues;

	private boolean isEnabled;

	private List<ChangeListener> changeListener;

	private Setting<?> fallbackSetting;

	/**
	 * @param id
	 * @param title_
	 */
	public Setting(final String id, final String title_) {
		this(id, title_, null);
	}

	/**
	 * @param id
	 * @param title_
	 * @param initValue
	 */
	public Setting(final String id, final String title_, final DATA initValue) {
		this(id, title_, initValue, null);
	}

	/**
	 * @param id
	 * @param title
	 * @param initValue
	 * @param possibleValues
	 */
	public Setting(final String id, final String title, final DATA initValue,
			final List<DATA> possibleValues) {
		super();
		this.model = getNewModel();
		this.id = id;
		this.title = title;
		this.possibleValues = new ArrayList<DATA>();
		if (possibleValues != null)
			this.possibleValues.addAll(possibleValues);
		this.changeListener = new ArrayList<ChangeListener>();
		this.fallbackSetting = this;
		this.setValue(initValue);
		this.enable();
	}

	/**
	 * @param otherSetting
	 */
	public Setting(final Setting<DATA> otherSetting) {
		this(otherSetting.id, otherSetting.title, (DATA) otherSetting.model
				.getValue(), otherSetting.possibleValues);
	}

	/**
	 * @param otherSetting
	 * @return
	 */
	@Override
	public abstract Setting<?> clone();

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Setting))
			return false;
		Setting s = (Setting) o;
		if (!this.getType().equals(s.getType()))
			return false;
		return this.getId().equals(s.getId());
	}

	@Override
	public String toString() {
		return this.id;
	}

	/**
	 * @param listener
	 */
	public void addChangeListener(ChangeListener listener) {
		this.changeListener.add(listener);
	}

	/**
	 * 
	 */
	public void fireModelChanged() {
		for (ChangeListener listener : this.changeListener)
			listener.stateChanged(new ChangeEvent(this));
	}

	/**
	 * @return
	 */
	public boolean isEnabled() {
		return this.isEnabled;
	}

	/**
	 * 
	 */
	public void enable() {
		this.isEnabled = true;
		this.fireModelChanged();
	}

	/**
	 * 
	 */
	public void disable() {
		this.isEnabled = false;
		this.fireModelChanged();
	}

	/**
	 * @return
	 */
	protected abstract ISettingModel<DATA> getNewModel();

	/**
	 * @return
	 */
	public ISettingModel<DATA> getModel() {
		return this.model;
	}

	public String getTitle() {
		return this.title;
	}

	/**
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return
	 */
	public DATA getValue() {
		if (this.isEnabled())
			return (DATA) this.model.getValue();
		else
			return (DATA) this.fallbackSetting.getValue();
	}

	/**
	 * @param rawString
	 * @throws ParseException
	 */
	public abstract void parseValueFromString(String rawString)
			throws ParseException;

	/**
	 * @param newValue
	 */
	public void setValue(final DATA newValue) {
		if (newValue == null)
			return;
		if (this.possibleValues.isEmpty()
				|| this.possibleValues.contains(newValue))
			this.model.setValue(newValue);
		else
			throw new IllegalArgumentException("Invalid value \"" + newValue
					+ "\"\nAllowed values are: "
					+ this.possibleValues.toArray().toString());
	}

	/**
	 * @param newModel
	 */
	public void applyModel(final ISettingModel<DATA> newModel) {
		this.model = newModel;
	}

	/**
	 * @return
	 */
	public boolean isRestricted() {
		return !this.possibleValues.isEmpty();
	}

	/**
	 * @param possibleValue
	 * @return
	 */
	public Setting<DATA> addPossibleValue(DATA possibleValue) {
		this.possibleValues.add(possibleValue);
		return this;
	}

	/**
	 * @return
	 */
	public List<String> getPossibleValuesAsStrings() {
		if (!isRestricted())
			return null;

		List<String> result = new ArrayList<String>();
		for (DATA d : this.possibleValues)
			result.add(d.toString());
		return result;
	}

	/**
	 * @return
	 */
	public abstract SETTING_TYPE getType();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.getTitle().hashCode();
	}

	/**
	 * @param newTitle
	 */
	public void setTitle(String newTitle) {
		this.title = newTitle;
	}

	/**
	 * @param newId
	 */
	public void setId(String newId) {
		this.id = newId;
	}

	/**
	 * @param fallbackSetting
	 *            the defaultSetting to set
	 */
	public void setFallbackSetting(Setting<?> fallbackSetting) {
		this.fallbackSetting = fallbackSetting;
	}

	/**
	 * @return the defaultSetting
	 */
	public Setting<?> getDefaultSetting() {
		return fallbackSetting;
	}

	// public static <T extends Setting<?>> Setting<?> getInstance(
	// final SETTING_TYPE settingType, final String name) {
	// if (settingType.equals(SETTING_TYPE.BOOL))
	// return new BooleanSetting(name);
	// else if (settingType.equals(SETTING_TYPE.INT))
	// return new IntegerSetting(name);
	// else if (settingType.equals(SETTING_TYPE.LONG))
	// return new LongSetting(name);
	// else if (settingType.equals(SETTING_TYPE.FLOAT))
	// return new FloatSetting(name);
	// else if (settingType.equals(SETTING_TYPE.DOUBLE))
	// return new DoubleSetting(name);
	// else if (settingType.equals(SETTING_TYPE.STRING))
	// return new StringSetting(name);
	// else if (settingType.equals(SETTING_TYPE.PATH))
	// return new PathSetting(name);
	// // default boolean
	// else
	// return new BooleanSetting(name);
	// }
}