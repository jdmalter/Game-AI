package target;

import static vector.Arithmetic.subtract;
import static vector.Property.magnitude;

import vector.Vector;

/**
 * Provides satisfaction and decceleration predicates.
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
public final class Predicate {

	/**
	 * Cannot be instantiated by users.
	 */
	private Predicate() {

	}

	/**
	 * @param t
	 *            The target position and radius.
	 * @param p
	 *            A vector attempting to satisfy the target.
	 * @return Whether the length of the difference between the target's
	 *         position and the vector is less than the target's radius of
	 *         satisfaction.
	 */
	public static boolean satisfied(Target t, Vector p) {
		return magnitude(subtract(t.position(), p)) < t.satisfaction();
	}

	/**
	 * @param t
	 *            The target position and radius.
	 * @param p
	 *            A vector attempting to deccelerate to the target.
	 * @return Whether the length of the difference between the target's
	 *         position and the vector is less than the target's radius of
	 *         decceleration.
	 */
	public static boolean deccelerated(Target t, Vector p) {
		return magnitude(subtract(t.position(), p)) < t.decceleration();
	}

}
