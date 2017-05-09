package vector;

import static utility.Mathf.atan2;
import static utility.Mathf.hypot;
import static vector.Factory.create;

import utility.Mathf;

/**
 * Provides direction, magnitude, and unit vector functions.
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
 * @author Jacob Malter
 */
public final class Property {

	/**
	 * Cannot be instantiated by users.
	 */
	private Property() {

	}

	/**
	 * This implementation uses {@link Mathf#atan2(double, double)}.
	 * 
	 * @param a
	 *            The vector whose direction is taken.
	 * @return The angle in radians from the positive horizontal axis.
	 */
	public static float direction(Vector a) {
		return atan2(a.y(), a.x());
	}

	/**
	 * This implementation uses {@link Mathf#hypot(double, double)}.
	 * 
	 * @param a
	 *            The vector whose magnitude is taken.
	 * @return The r component of polar coordinates that corresponds vector in
	 *         Cartesian coordinates.
	 */
	public static float magnitude(Vector a) {
		return hypot(a.x(), a.y());
	}

	// changed unit to use direction because Mathf.atan2 better handles wierd
	// angles and radians

	/**
	 * This implementation uses {@link Arithmetic#divide(Vector, double)} and
	 * {@link Arithmetic#magnitude(Vector)}.
	 * 
	 * @param a
	 *            The vector whose angle is taken.
	 * @return A unit vector whose angle is the same as a and whose length is 1.
	 */
	public static Vector unit(Vector a) {
		return create(direction(a));
	}

}
