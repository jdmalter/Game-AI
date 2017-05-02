package app;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import screendecisionmaking.BehaviorTree;
import screendecisionmaking.DecisionTree;
import screendecisionmaking.Learning;
import screenmovement.ArriveSteering;
import screenmovement.BasicMotion;
import screenmovement.FlockingBehavior;
import screenmovement.WanderSteering;
import screenpathfinding.PathFinding;

/**
 * Provides a list of buttons for {@code ClassesPanel} to render and start by a
 * processing applet.
 * 
 * @author Jacob Malter
 */
public final class Main extends JFrame {

	/** auto generated */
	private static final long serialVersionUID = -7328439355725792689L;
	/** title bar text */
	private static final String TITLE_BAR = "Main";
	/** the specified minimum width */
	private static final int MINIMUM_WIDTH = 250;

	/** An ordered collection of names to be displayed. */
	private static final List<ClassesPanel> panels = Arrays.asList(
			new ClassesPanel("Homework 1", BasicMotion.class, ArriveSteering.class, WanderSteering.class,
					FlockingBehavior.class),
			new ClassesPanel("Homework 2", PathFinding.class),
			new ClassesPanel("Homework 3", DecisionTree.class, BehaviorTree.class, Learning.class));

	/** panel first initialized and shown on start up */
	private final JPanel mainPanel = new JPanel();

	/**
	 * Sets up main panel and frame.
	 */
	public Main() {
		setUpMainPanel();
		add(mainPanel);
		setUpFrame(TITLE_BAR);
	}

	/**
	 * Sets layout of main panel. Delegates smaller components to other methods.
	 */
	private void setUpMainPanel() {
		mainPanel.setLayout(new GridLayout(0, 1));
		setUpButtons();
	}

	/**
	 * Map clases to buttons. Add those buttons to the main panel.
	 */
	private void setUpButtons() {
		panels.stream().map((panel) -> {
			return new AppButton(panel.getName(), (event) -> {
				// get ready to add a new panel
				mainPanel.removeAll();
				mainPanel.setLayout(new GridLayout(0, 1));

				// allow classes panel to return to the original state
				panel.add(createReturnButton(panel));

				// adjust frame to new state
				mainPanel.add(panel);
				setUpFrame(panel.getName());
			});
		}).forEach(mainPanel::add);
		mainPanel.add(createCloseButton());
	}

	/**
	 * @return A button with text and an ActionListener.
	 */
	private AppButton createReturnButton(ClassesPanel panel) {
		return new AppButton("Return", (event) -> {
			// swap out classes panel
			mainPanel.remove(panel);
			panel.remove((AppButton) event.getSource());

			// adjust frame to original state
			setUpMainPanel();
			setUpFrame(TITLE_BAR);
		}) {

			/** auto generated */
			private static final long serialVersionUID = 8713455096506272271L;

			{
				// static initializer block for return button
				setBackground(getBackground().brighter());
			}

		};
	}

	/**
	 * @return A button with text and an ActionListener.
	 */
	private AppButton createCloseButton() {
		return new AppButton("Close", (event) -> System.exit(0)) {

			/** auto generated */
			private static final long serialVersionUID = 6468429970799102813L;

			{
				// initializer block for close button
				setBackground(getBackground().brighter());
			}

		};
	}

	/**
	 * Adds finishing touches to frame.
	 * 
	 * @param title
	 *            the title to be displayed in the frame's border
	 */
	public void setUpFrame(String title) {
		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setMinimumSize(new Dimension(MINIMUM_WIDTH, 0));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Creates a new frame.
	 * 
	 * @param args
	 *            Command line arguments are not used in this program.
	 */
	public static void main(String[] args) {
		new Main();
	}

}
