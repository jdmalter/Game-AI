package drawing;

import static java.util.Objects.requireNonNull;
import static vector.Arithmetic.add;
import static vector.Arithmetic.multiply;
import static vector.Arithmetic.subtract;
import static vector.Property.direction;
import static vector.Property.magnitude;
import static vector.Property.unit;

import java.util.function.Consumer;

import digraph.Digraph;
import processing.core.PApplet;
import target.Target;
import vector.Vector;

/**
 * Provides a stateless representation to draw a digraph.
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
public final class SmallGraph {

	/** The hexadecimal red, blue, green color of white. */
	private static final int FILL = 0xFFFFFFFF;
	/** The weight (in pixels) of the stroke. */
	private static final int STROKE_WEIGHT = 2;

	/**
	 * Cannot be instantiated by users.
	 */
	private SmallGraph() {

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
	public static Consumer<Digraph<Target, Float>> bind(PApplet pa) {
		requireNonNull(pa);

		return (dg) -> draw(pa, dg);
	}

	/**
	 * Draws edges then vertices.
	 * 
	 * <p>
	 * This is based on the <a href=
	 * "https://processing.org/discourse/beta/num_1219607845.html">Processing
	 * Arrow</a> with some modifications.
	 * 
	 * @param pa
	 *            A base class for sketching.
	 * @param dg
	 *            A mathematical structure that consists of two sets of
	 *            elements: a set of vertices and a set of edges. Edges are
	 *            weighted ordered pairs of vertices.
	 * 
	 */
	public static void draw(PApplet pa, Digraph<Target, Float> dg) {
		pa.fill(FILL);
		dg.get().forEach((tail) -> {
			dg.get(tail).get().forEach((head) -> {
				drawEdge(pa, head, tail);
			});
		});
		dg.get().forEach(SourcePoint.bind(pa));
	}

	/**
	 * If there is no intersection of verticies at the head and tail, draws a
	 * line and triangle pointing from the tail to the head position vectors.
	 * 
	 * @param pa
	 *            A base class for sketching.
	 * @param h
	 *            A position vector of the head.
	 * @param t
	 *            A position vector of the tail.
	 */
	public static void drawEdge(PApplet pa, Target h, Target t) {
		// find the difference and subtract the radius of the vertex
		Vector difference = subtract(h.position(), t.position());

		// if there is no intersection of vertices
		if (magnitude(difference) > h.satisfaction()) {
			// remove diameter of vertex from the difference
			Vector rosTail = multiply(unit(difference), t.satisfaction() / 2);
			Vector rosHead = multiply(unit(difference), h.satisfaction() / 2);
			Vector arrow = subtract(difference, add(rosTail, rosHead));

			// draw an arrow
			pa.pushMatrix(); // draw a line with the difference
			pa.translate(t.position().x() + rosHead.x(), t.position().y() + rosHead.y());
			pa.stroke(FILL);
			pa.strokeWeight(STROKE_WEIGHT);
			pa.line(0, 0, arrow.x(), arrow.y());
			pa.noStroke();

			// draw a rotated arrow with height
			float height = 0.75f * h.satisfaction();
			pa.translate(arrow.x(), arrow.y());
			pa.rotate(direction(arrow));
			pa.fill(FILL);
			pa.triangle(0, 0, -height, height / 2, -height, -height / 2);
			pa.popMatrix();
		}
	}

}
