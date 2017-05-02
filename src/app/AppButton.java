package app;

import static java.util.Objects.requireNonNull;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * A base class for a consistent look between buttons.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * @author Jacob Malter
 */
public class AppButton extends JButton {

	/** auto generated */
	private static final long serialVersionUID = 3054746978363291622L;
	/** the desired Font for this button */
	private static final Font FONT = new Font("Arial", Font.BOLD, 30);

	/**
	 * Creates a button with text and an ActionListener.
	 * 
	 * @param text
	 *            the text of the button
	 * @param listener
	 *            the ActionListener to be added
	 * @throws NullPointerException
	 *             if text or listener is null
	 */
	public AppButton(String text, ActionListener listener) {
		super(requireNonNull(text));
		setFocusable(false);
		setFont(FONT);
		addActionListener(requireNonNull(listener));
	}

}
