package drawing;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import processing.core.PApplet;
import target.Target;
import vector.Vector;

/**
 * Provides a stateless representation to draw a target.
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
public final class SourcePoint {

	/** The hexadecimal red, blue, green color of white. */
	private static final int STROKE = 0xFFFFFFFF;
	/** The diameter of the drawn ellipse. */
	private static final float WEIGHT = 1.5f;

	/**
	 * Cannot be instantiated by users.
	 */
	private SourcePoint() {

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
	public static Consumer<Target> bind(PApplet pa) {
		requireNonNull(pa);

		return (t) -> draw(pa, t);
	}

	/**
	 * Draws a target on the parent with two unfilled ellipses of at the
	 * position.
	 * 
	 * @param pa
	 *            A base class for sketching.
	 * @param t
	 *            The target being drawn.
	 */
	public static void draw(PApplet pa, Target t) {
		pa.noFill();
		pa.stroke(STROKE);
		pa.strokeWeight(WEIGHT);

		// draw the point
		Vector p = t.position();
		pa.ellipse(p.x(), p.y(), t.satisfaction(), t.satisfaction());
		pa.ellipse(p.x(), p.y(), t.decceleration(), t.decceleration());
	}

}
