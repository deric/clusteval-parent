/**
 * 
 */
package settings.model;

import java.awt.event.ActionEvent;

/**
 * @author Christian Wiwie
 * 
 */
public class RestrictedSettingAction extends AbstractSettingAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1926087078751966300L;

	/**
	 * @param model
	 */
	public RestrictedSettingAction(ISettingModel<?> model) {
		super(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		model.setValue(e.getActionCommand());
	}
}
