package drawing;

import static java.util.Objects.requireNonNull;

import function.QuadConsumer;
import processing.core.PApplet;
import utility.Mathf;
import vector.Vector;

/**
 * Provides a stateless representation to draw a character.
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
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * @author Jacob Malter
 */
public final class Character {

	/** Sixity degrees in radians. */
	private static final float TRIANGLE_POINT_ANGLE_FROM_CENTER = Mathf.PI / 6;

	/**
	 * Cannot be instantiated by users.
	 */
	private Character() {

	}

	/**
	 * This is based on the <a href=
	 * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/bind">Javascript
	 * bind function</a> with some modifications.
	 * 
	 * @param pa
	 *            A base class for sketching.
	 * @return A new draw function that has its pa set to the provided pa.
	 * @throws NullPointerException
	 *             if pa is null
	 */
	public static QuadConsumer<Integer, Float, Vector, Float> bind(PApplet pa) {
		requireNonNull(pa);

		return (c, d, p, t) -> draw(pa, c, d, p, t);
	}

	/**
	 * Draws a character on the parent filled in with a color at the position
	 * with the theta.
	 * 
	 * @param pa
	 *            A base class for sketching.
	 * @param c
	 *            The hexadecimal alpha, red, blue, green color.
	 * @param d
	 *            The diameter of the drawn ellipse.
	 * @param p
	 *            The position vector of the character.
	 * @param t
	 *            The angular position of the character.
	 */
	public static void draw(PApplet pa, int c, float d, Vector p, float t) {
		float angle1 = t - TRIANGLE_POINT_ANGLE_FROM_CENTER;
		float angle2 = t;
		float angle3 = t + TRIANGLE_POINT_ANGLE_FROM_CENTER;

		float x1 = p.x() + d * Mathf.cos(angle1);
		float y1 = p.y() + d * Mathf.sin(angle1);
		float x2 = p.x() + d * Mathf.cos(angle2) * 1.5f;
		float y2 = p.y() + d * Mathf.sin(angle2) * 1.5f;
		float x3 = p.x() + d * Mathf.cos(angle3);
		float y3 = p.y() + d * Mathf.sin(angle3);

		pa.fill(c);
		pa.ellipse(p.x(), p.y(), d, d);
		pa.triangle(x1, y1, x2, y2, x3, y3);
	}

}
