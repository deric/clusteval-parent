/**
 * 
 */
package settings.model;

import javax.swing.Action;
import javax.swing.SpinnerNumberModel;

import settings.Setting;

/**
 * @author Christian Wiwie
 * 
 * @param <DATA>
 */
public abstract class NumberSettingModel<DATA extends Number>
		extends
			ActionSettingModel<DATA> implements INumberSettingModel<DATA> {

	/**
* 
*/
	private static final long serialVersionUID = -5602858960267538009L;

	private SpinnerNumberModel spinnerModel;

	/**
	 * @param setting
	 * 
	 */
	public NumberSettingModel(Setting<?> setting) {
		super(setting);
		this.spinnerModel = new SpinnerNumberModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getTypeSpecificAction()
	 */
	@Override
	public Action getTypeSpecificAction() {
		// For numbers we don't need an action
		return null;
	}

	/**
	 * @return the spinnerModel
	 */
	public SpinnerNumberModel getSpinnerModel() {
		return spinnerModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getValue()
	 */
	@Override
	public Object getValue() {
		return this.spinnerModel.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object newValue) {
		this.spinnerModel.setValue(newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getUIModel()
	 */
	@Override
	public Object getUIModel() {
		return this.spinnerModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.INumberSettingModel#getMinimum()
	 */
	@Override
	public Comparable getMinimum() {
		return this.spinnerModel.getMinimum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.INumberSettingModel#getMaximum()
	 */
	@Override
	public Comparable getMaximum() {
		return this.spinnerModel.getMaximum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.INumberSettingModel#setMaximum(java.lang.Comparable)
	 */
	@Override
	public void setMaximum(Comparable newMax) {
		this.spinnerModel.setMaximum(newMax);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.INumberSettingModel#setMinimum(java.lang.Comparable)
	 */
	@Override
	public void setMinimum(Comparable newMin) {
		this.spinnerModel.setMinimum(newMin);
	}
}