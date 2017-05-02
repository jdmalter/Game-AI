package app;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static processing.core.PApplet.main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JPanel;

/**
 * Renders a list of buttons to start by a processing applet.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * @author Jacob Malter
 */
public class ClassesPanel extends JPanel {

	/** auto generated */
	private static final long serialVersionUID = -7328439355725792689L;
	/** the specified minimum width */
	private static final int MINIMUM_WIDTH = 250;

	/** An ordered collection of classes to be displayed. */
	private final List<Class<?>> classes = new ArrayList<Class<?>>();

	/**
	 * @param name
	 *            the string that is to be this component's name
	 * @param classes
	 *            An ordered array of classes to be displayed.
	 * @throws NullPointerException
	 *             if name, classes, or any class in classes is null
	 */
	public ClassesPanel(String name, Class<?>... classes) {
		this(name, asList(classes));
	}

	/**
	 * If name, classes, or any class is null, a customized
	 * {@link java.lang.NullPointerException} is thrown.
	 * 
	 * @param name
	 *            the string that is to be this component's name
	 * @param classes
	 *            An ordered list of classes to be displayed.
	 * @throws NullPointerException
	 *             if name, classes, or any class in classes is null
	 */
	public ClassesPanel(String name, List<Class<?>> classes) {
		requireNonNull(name);
		requireNonNull(classes).stream().map(Objects::requireNonNull).forEach(this.classes::add);

		setUpMainPanel();
		setUpPanel(name);
	}

	/**
	 * Sets layout of main panel. Delegates smaller components to other methods.
	 */
	private void setUpMainPanel() {
		setLayout(new GridLayout(0, 1));
		setUpClasses();
	}

	/**
	 * Map clases to buttons. Add those buttons to the main panel.
	 */
	private void setUpClasses() {
		classes.stream().map(this::toPAppletButton).forEach(this::add);
	}

	/**
	 * Map clases to buttons.
	 * 
	 * @param mainClass
	 *            name of the class to load (with package if any)
	 * @return A button with text and an ActionListener.
	 */
	private AppButton toPAppletButton(Class<?> mainClass) {
		return new AppButton(mainClass.getSimpleName(), (event) -> main(mainClass));
	}

	/**
	 * Adds finishing touches to panel.
	 * 
	 * @param name
	 *            the string that is to be this component's name
	 */
	public void setUpPanel(String name) {
		setName(name);
		setMinimumSize(new Dimension(MINIMUM_WIDTH, 0));
		setVisible(true);
	}

}
