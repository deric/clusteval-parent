/**
 * 
 */
package gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;

/**
 * @author Christian Wiwie
 * 
 */
//TODO
public class EscapeClosableDialog implements KeyListener {

	private JDialog dialog;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4012744155603006169L;

	public EscapeClosableDialog(final JDialog dialog) {
		super();
		dialog.getParent().addKeyListener(this);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("Bla");
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			dialog.setVisible(false);
			dialog.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}
}
