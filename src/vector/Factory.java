package vector;

import static java.util.Objects.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import utility.Mathf;

/**
 * Provides constants, factory functions, mapping functions, and application
 * functions.
 * 
 * <p>
 * I want to avoid unexpected returns from function calls. Through pure
 * functions, my static functions only focus on what work needs to be done,
 * quickly compose into complex behaviors, and safely achieve parallelism.
 * 
 * <p>
 * Pure functions map input arguments to output values. In my design, helper
 * classes provide pure functions through static methods. Within each function,
 * input transforms into output. When each line of code works with input or
 * mapped input, code in each function avoid unnecessary work. Without
 * unnecessary lines of code, pure functions minimize the length of functions.
 * 
 * <p>
 * Helper functions remain predictable regardless of who calls functions. Inside
 * the lowest level pure functions, code guarantees consistent return values or
 * calls safe Java library static functions. When it is guaranteed that the
 * output only depends on the input, these functions avoid bugs caused by
 * mutable external state. Since pure functions eliminate an entire class of
 * bugs, there is a lower cost to regularly reuse and rely on those functions.
 * As the cost of developing with pure functions decreases while holding
 * constant development resources, the amount of code produced increases, and
 * therefore more working code enables more complex functions to be successfully
 * completed.
 * 
 * <p>
 * Pure functions easily become parallelized. Again, pure functions return the
 * same output of for the same input regardless of execution context. Since a
 * list of inputs can be split into threads, each input can be passed in
 * parallel to a pure function to return results. Using the Java stream package,
 * a collection of inputs can be mapped into outputs in parallel without bugs
 * arising from external state. Throughout my code, there are many successful
 * examples of streams using pure functions to quickly perform operations on
 * large data structures.
 * 
 * <p>
 * Here is some additional reading:
 * <ul>
 * <li><a href=
 * "https://medium.com/javascript-scene/master-the-javascript-interview-what-is-a-pure-function-d1c076bec976#.vsrql8u01">Javascript
 * pure functions</a></li>
 * <li><a href=
 * "https://www.sitepoint.com/functional-programming-pure-functions/">Pure
 * functions</a></li>
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
public final class Factory {

	/**
	 * Cannot be instantiated by users.
	 */
	private Factory() {

	}

	/** A vector whose length is 0. */
	public static final Vector ZERO = new Vector(0, 0);
	/** A unit vector representing the positive horizontal axis. */
	public static final Vector I = new Vector(1, 0);
	/** A unit vector representing the positive vertical axis. */
	public static final Vector J = new Vector(0, 1);
	/** A unit vector representing the positive diagonal axis. */
	public static final Vector K = new Vector(1, 1);

	/**
	 * @param x
	 *            The magnitude in a horizontal dimension.
	 * @param y
	 *            The magnitude in a vertical dimension.
	 * @return A new Vector that represents the given magnitudes in the
	 *         horizontal and vertical dimensions.
	 */
	public static Vector create(float x, float y) {
		return new Vector(x, y);
	}

	/**
	 * This implementation uses {@link utility.Mathf#cos(double)} and
	 * {@link utility.Mathf#sin(double)}.
	 * 
	 * @param t
	 *            An angle in radians.
	 * @return A unit vector who angle is that same as t and whose length is 1.
	 */
	public static Vector create(float t) {
		float cos = Mathf.cos(t);
		float sin = Mathf.sin(t);
		return create(cos, sin);
	}

	/**
	 * @param f
	 *            A bifunction applied to each pair of components.
	 * @param a
	 *            The first vector being mapped.
	 * @param b
	 *            The second vector being mapped.
	 * @return A vector whose components are the result of the bifunction on
	 *         their respective pairs of previous values.
	 */
	public static Vector map(BiFunction<Float, Float, Float> f, Vector a, Vector b) {
		float fx = f.apply(a.x(), b.x());
		float fy = f.apply(a.y(), b.y());
		return create(fx, fy);
	}

	/**
	 * This is based on the <a href=
	 * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/apply">Javascript
	 * apply function</a> with some modifications.
	 * 
	 * @param f
	 *            A bifunction applied to each pair of components.
	 * @return A new map function that has its f set to the provided f.
	 * @throws NullPointerException
	 *             if f is null
	 */
	public static BiFunction<Vector, Vector, Vector> apply(BiFunction<Float, Float, Float> f) {
		requireNonNull(f);

		return (a, b) -> map(f, a, b);
	}

	/**
	 * @param f
	 *            A function applied to each component.
	 * @param a
	 *            The vector being mapped.
	 * @return A vector whose components are the result of the function on their
	 *         respective previous values.
	 */
	public static Vector map(Function<Float, Float> f, Vector a) {
		float fx = f.apply(a.x());
		float fy = f.apply(a.y());
		return create(fx, fy);
	}

	/**
	 * This is based on the <a href=
	 * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/apply">Javascript
	 * apply function</a> with some modifications.
	 * 
	 * @param f
	 *            A function applied to each component.
	 * @return A new map function that has its f set to the provided f.
	 * @throws NullPointerException
	 *             if f is null
	 */
	public static Function<Vector, Vector> apply(Function<Float, Float> f) {
		requireNonNull(f);

		return (a) -> map(f, a);
	}

}
