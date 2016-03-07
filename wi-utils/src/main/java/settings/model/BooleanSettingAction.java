/**
 * 
 */
package settings.model;

import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;

/**
 * @author Christian Wiwie
 * 
 */
public class BooleanSettingAction extends AbstractSettingAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7727738248730549655L;

	/**
	 * @param model
	 */
	public BooleanSettingAction(ISettingModel<?> model) {
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
		this.model.setValue(((AbstractButton) e.getSource()).isSelected());
	}

}
