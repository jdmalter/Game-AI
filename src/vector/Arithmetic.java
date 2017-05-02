package vector;

import static vector.Factory.create;

import utility.Mathf;

/**
 * Provides dot, addition, subtraction, multiplication, division, and floor
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
 * @author Jacob Malter
 */
public final class Arithmetic {

	/**
	 * Cannot be instantiated by users.
	 */
	private Arithmetic() {

	}

	/**
	 * There are two defintions of dot product. Algebraically, dot product is
	 * the summation of products in each dimension. Geometrically, dot product
	 * is the product of each of the magnitudes of vectors a and b and the
	 * cosine of angle in radians between a and b.
	 * 
	 * @param a
	 *            A vector where the angle starts.
	 * @param b
	 *            A vector where the angle ends.
	 * @return A scalar quantity.
	 */
	public static float dot(Vector a, Vector b) {
		return a.x() * b.x() + a.y() * b.y();
	}

	/**
	 * @param a
	 *            An addend vector.
	 * @param b
	 *            An addend vector.
	 * @return A sum vector.
	 */
	public static Vector add(Vector a, Vector b) {
		return create(a.x() + b.x(), a.y() + b.y());
	}

	/**
	 * @param a
	 *            A minuend vector.
	 * @param b
	 *            A subtrahend vector.
	 * @return A difference vector.
	 */
	public static Vector subtract(Vector a, Vector b) {
		return create(a.x() - b.x(), a.y() - b.y());
	}

	/**
	 * @param a
	 *            A vector multiplier.
	 * @param c
	 *            A scalar multiplicand.
	 * @return A vector product.
	 */
	public static Vector multiply(Vector a, float c) {
		return create(a.x() * c, a.y() * c);
	}

	/**
	 * @param a
	 *            A dividend vector.
	 * @param c
	 *            A scalar divisor.
	 * @return A quotient vector.
	 */
	public static Vector divide(Vector a, float c) {
		return create(a.x() / c, a.y() / c);
	}

	// moved floor here from fixed raidus nearest neighbor where it was used so
	// that similar functions are found together

	/**
	 * The quotient vector's magnitudes equal the largest (closest to positive
	 * infinity) floating-point value that less than or equal to the quotient
	 * vector's float magnitudes and is equal to a mathematical integer.
	 * 
	 * This implementation uses {@link utility.Mathf#floor(double)}.
	 * 
	 * @param a
	 *            A dividend vector.
	 * @param c
	 *            A scalar divisor.
	 * @return A quotient vector.
	 */
	public static Vector floor(Vector a, float c) {
		return create(Mathf.floor(a.x() / c), Mathf.floor(a.y() / c));
	}

}
