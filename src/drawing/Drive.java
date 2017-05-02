package drawing;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import function.TriFunction;
import processing.core.PApplet;

/**
 * Provides a stateless representation to draw a drive bar.
 * 
 * <p>
 * I want to easily draw simple characters to the screen. A stateless functional
 * component is a stateless function that renders some parameters to a screen.
 * Through stateless functional components, my drawings encourage best
 * practices, become simple and easy to understand, and improve performance.
 * 
 * <p>
 * Stateless functional components exclusively focus on drawing to a screen in
 * processing. In my code for breadcrumb and character, the draw function only
 * computes coordinates for shapes and uses the PApplet to render shapes on a
 * screen. With improved focus, these components force to place data in higher
 * level data structures. Since all data is in higher level data structures, it
 * is easier to parameterize the drawing of breadcrumbs and characters. When it
 * is easy to parameterize draw functions, client code changes drawing behavior
 * easily through changing a passed parameter to draw functions.
 * 
 * <p>
 * Static draw functions contain as little code as possible. Conceptually, a
 * stateless functional component is a pure function. For the same reasons as
 * pure functions, these components focus only on drawing a simple character to
 * a screen. Since inputs are transformed into screen objects or outputs, the
 * amount of code is minimized.
 * 
 * <p>
 * Stateless functional components are cheap. In memory, there are no instances
 * of presentational breadcrumbs or characters being drawn, so therefore memory
 * usage is reduced. In computation, there are fewer checks on parametrized
 * state than fields, and fewer checks involved fewer instructors or less
 * computation. Considering computation and memory, stateless functional
 * components have advantages over stateful presentation objects.
 * 
 * <p>
 * Here is some additional reading:
 * <ul>
 * <li><a href=
 * "https://hackernoon.com/react-stateless-functional-components-nine-wins-you-might-have-overlooked-997b0d933dbc#.sgawzhldi">React
 * Stateless Functional Components: Nine Wins You Might Have Overlooked
 * </a></li>
 * <li><a href=
 * "https://tylermcginnis.com/functional-components-vs-stateless-functional-components-vs-stateless-components/">Functional
 * Components vs. Stateless Functional Components vs. Stateless
 * Components</a></li>
 * </ul>
 * 
 * @author Jacob Malter
 */
public final class Drive {

	/**
	 * The argb color of black used to set the color of the drive window.
	 */
	private static final int BLACK = 0xFF000000;
	/**
	 * The argb color of white used to set the color of the text.
	 */
	private static final int WHITE = 0xFFFFFFFF;
	/**
	 * Percentage of outer black rectangle used for badding inner drive bar.
	 */
	private static final float PADDING = 2f;
	/**
	 * The height of the drive bar area dedicated to the string.
	 */
	private static final float HEIGHT = 15;

	/**
	 * Cannot be instantiated by users.
	 */
	private Drive() {

	}

	/**
	 * This is based on the <a href=
	 * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/bind">Javascript
	 * bind function</a> with some modifications.
	 * 
	 * @param pa
	 *            A base class for sketching.
	 * @param c
	 *            The argb color used to set the color of the drive bar.
	 * @param w
	 *            Width of the drive bar by default.
	 * @param h
	 *            Height of the drive bar by default.
	 * @return A new function that takes s, x, and y and returns a new draw
	 *         function that has all parameters set except for p.
	 * @throws NullPointerException
	 *             if pa is null
	 */
	public static TriFunction<String, Float, Float, Consumer<Float>> bind(PApplet pa, int c, float w, float h) {
		requireNonNull(pa);

		return (s, x, y) -> (p) -> draw(pa, c, w, h, s, x, y, p);
	}

	/**
	 * Draws a drive bar area with the provided string and a drive bar of the
	 * provided color, width, height, and proportion.
	 * 
	 * <p>
	 * This implmentation uses {@value #HEIGHT} height for the text and
	 * {@value #PADDING} padding around the drive bar.
	 * 
	 * @param pa
	 *            A base class for sketching.
	 * @param c
	 *            The argb color used to set the color of the drive bar.
	 * @param w
	 *            Width of the drive bar by default.
	 * @param h
	 *            Height of the drive bar by default.
	 * @param s
	 *            The string to display with the drive.
	 * @param x
	 *            X-coordinate of the drive bar by default.
	 * @param y
	 *            Y-coordinate of the drive bar by default.
	 * @param p
	 *            Proportion of width of drive bar.
	 */
	public static void draw(PApplet pa, int c, float w, float h, String s, float x, float y, float p) {
		pa.fill(BLACK);
		pa.rect(x, y, w + (2 * PADDING), HEIGHT + h + (2 * PADDING));

		pa.fill(WHITE);
		pa.textAlign(PApplet.LEFT, PApplet.CENTER);
		pa.text(s, x + PADDING, y + (HEIGHT / 2) - PADDING);

		pa.fill(c);
		pa.rect(x + PADDING, y + PADDING + HEIGHT, p * w, h);
	}

}