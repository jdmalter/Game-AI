package screen;

import processing.core.PApplet;

/**
 * A base class for consistent look and basic operations between screens.
 * 
 * <p>
 * After creating decision making, I realized there is more abstractions to be
 * removed from Screen. To make each class simpler, I split Screen into Screen
 * and Movement. Screen handles frames, elapsed time, and background drawing.
 * 
 * @author Jacob Malter
 */
public abstract class Screen extends PApplet {

	/**
	 * The argb color of black used to set the color of the background window.
	 */
	private static final int BLACK = 0xFF000000;

	/** The width of the display window in units of pixels. */
	public static final int WIDTH = 800;
	/** The height of the display window in units of pixels. */
	public static final int HEIGHT = 800;

	/**
	 * How many times {@link #draw()} has been called.
	 */
	private int frames = 0;
	/** The last time in milliseconds of the last call to {@link #draw()}. */
	private double timer = System.currentTimeMillis();

	/**
	 * Sets width and height to defaults.
	 */
	public Screen() {
		this(WIDTH, HEIGHT);
	}

	/**
	 * Sets width and height.
	 * 
	 * @param w
	 *            The width of the display window in units of pixels.
	 * @param h
	 *            The height of the display window in units of pixels.
	 */
	public Screen(int w, int h) {
		width = w;
		height = h;
	}

	/**
	 * @return How many times {@link #draw()} has been called since the last
	 *         call to {@link #settings()}.
	 */
	public int frames() {
		return frames;
	}

	/**
	 * If {@link #draw()} has not been called, the elapsed time is when
	 * {@link #settings()} was called.
	 * 
	 * <o> I moved the update of timer from elapsed to draw so that subclasses
	 * can use elapsed without reseting the timer.
	 * 
	 * @return The difference in milliseconds between the current time and the
	 *         last call to {@link #draw()}.
	 */
	public float elapsed() {
		return (float) (System.currentTimeMillis() - timer + Float.MIN_NORMAL);
	}

	/**
	 * Sets the background to {@value #BLACK}.
	 */
	public void background() {
		background(BLACK);
	}

	@Override
	public void exit() {
		dispose(); // use an alternative behavior
	}

	@Override
	public void settings() {
		size(width, height);
	}

	@Override
	public void draw() {
		noStroke();
		background();
		frames++;
		timer = System.currentTimeMillis();
	}

}
