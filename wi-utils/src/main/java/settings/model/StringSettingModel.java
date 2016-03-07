/**
 * 
 */
package settings.model;

import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import settings.Setting;

/**
 * @author Christian Wiwie
 * 
 */
public class StringSettingModel extends ActionSettingModel<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8461849326900545871L;

	private PlainDocument doc;

	/**
	 * 
	 */
	public StringSettingModel(Setting<?> setting) {
		super(setting);
		doc = new PlainDocument();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getValue()
	 */
	@Override
	public Object getValue() {
		try {
			return this.doc.getText(0, this.doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object newValue) {
		try {
			this.doc.replace(0, this.doc.getLength(), (String) newValue, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getTypeSpecificAction()
	 */
	@Override
	public Action getTypeSpecificAction() {
		// TODO
		return null;
		// return new StringSettingAction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see settings.model.ISettingModel#getUIModel()
	 */
	@Override
	public PlainDocument getUIModel() {
		return this.doc;
	}
}
