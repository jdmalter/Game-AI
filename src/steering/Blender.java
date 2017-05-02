package steering;

import static utility.Streams.naturals;
import static vector.Arithmetic.multiply;
import static vector.Streams.sum;

import java.util.List;

import vector.Vector;

/**
 * Provides steering weighted blending function.
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
public final class Blender {

	/**
	 * Cannot be instantiated by users.
	 */
	private Blender() {

	}

	/**
	 * Blends over the range 0 to the smaller of the two list's sizes.
	 * 
	 * @param vectors
	 *            A list of vectors to blend.
	 * @param weights
	 *            A list of weights to scale their respective vectors.
	 * @return A vector which equals the sum of every vector multiplied by its
	 *         respective weight.
	 */
	public static Vector weightedBlend(List<Vector> vectors, List<Float> weights) {
		return sum(naturals().limit(Math.min(vectors.size(), weights.size())).map((index) -> {
			// blend respective weights
			return multiply(vectors.get(index), weights.get(index));
		}));
	}

}
