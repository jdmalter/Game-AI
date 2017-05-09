package search;

import static java.util.Objects.requireNonNull;
import static vector.Arithmetic.subtract;
import static vector.Property.magnitude;

import java.util.function.Function;

import vector.Vector;

/**
 * Provides constants and out of the box heuristics.
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
public final class Heuristics {

	/**
	 * Cannot be instantiated by users.
	 */
	private Heuristics() {

	}

	/**
	 * @param guess
	 *            the estimated cost of the cheapest solution from any given
	 *            state.
	 * @return A heurisitic that always returns the given guess.
	 * @throws IllegalArgumentException
	 *             if guess is non-negative
	 */
	public static <S> Function<? super S, Float> constant(float guess) {
		if (guess < 0) {
			throw new IllegalArgumentException("guess must be non-negative");
		}

		return (state) -> guess;
	}

	/**
	 * @return A heurisitic that always returns zero.
	 */
	public static <S> Function<? super S, Float> zero() {
		return constant(0);
	}

	/**
	 * @return A heurisitic that always returns positive infinity.
	 */
	public static <S> Function<? super S, Float> infinity() {
		return constant(Float.POSITIVE_INFINITY);
	}

	/**
	 * @param goal
	 *            A vector goal state.
	 * @return A heurisitic that returns the manhattan distance to the goal.
	 * @throws NullPointerException
	 *             if goal is null
	 */
	public static Function<? super Vector, Float> manhattan(Vector goal) {
		requireNonNull(goal);

		return (state) -> goal.x() - state.x() + goal.y() - state.y();
	}

	/**
	 * @param goal
	 *            A vector goal state.
	 * @return A heurisitic that returns the euclidean distance to the goal.
	 * @throws NullPointerException
	 *             if goal is null
	 */
	public static Function<? super Vector, Float> euclidean(Vector goal) {
		requireNonNull(goal);

		return (state) -> magnitude(subtract(goal, state));
	}

}
