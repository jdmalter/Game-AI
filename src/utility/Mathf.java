package utility;

/**
 * Provides float constants, exponentiation functions, and trigonometric
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
public final class Mathf {

	/** Pi or half tau. */
	public static final float PI = (float) (Math.PI);
	/** Tau or two pi. */
	public static final float TAU = (float) (2 * Math.PI);

	/**
	 * Cannot be instantiated by users.
	 */
	private Mathf() {

	}

	/**
	 * This implementation wraps {@link java.lang.Math#abs(float)}.
	 * 
	 * <p>
	 * This function was created to consolidate all math functions into one
	 * class instead of between {@link java.lang.Math} and this class.
	 * 
	 * @param a
	 *            argument whose absolute value is to be determined
	 * @return the absolute value of the argument.
	 */
	public static float abs(float a) {
		return Math.abs(a);
	}

	/**
	 * This implementation wraps {@link java.lang.Math#log(double)}.
	 * 
	 * <p>
	 * This function was created to consolidate all math functions into one
	 * class instead of between {@link java.lang.Math} and this class.
	 * 
	 * @param a
	 *            a value
	 * @return the value ln&nbsp;{@code a}, the natural logarithm of {@code a}.
	 */
	public static float log(float a) {
		return (float) Math.log(a);
	}

	/**
	 * <p>
	 * This function was created to consolidate all math functions into one
	 * class instead of between {@link java.lang.Math} and this class.
	 * 
	 * @param a
	 *            a value
	 * @return the base 2 logarithm of a.
	 */
	public static float log2(float a) {
		return log(a) / log(2);
	}

	/**
	 * @param t
	 *            An angle in radians.
	 * @return An angle in radians between -{@link Mathf#PI} and
	 *         {@link Mathf#PI}.
	 */
	public static float angle(float t) {
		t %= Mathf.TAU;
		return (t > Mathf.PI) ? (t - Mathf.TAU) : (t < -Mathf.PI ? t + Mathf.TAU : t);
	}

	/**
	 * This implementation wraps {@link java.lang.Math#sin(double)}.
	 * 
	 * @param a
	 *            an angle, in radians.
	 * @return the sine of the argument.
	 */
	public static float sin(float a) {
		return (float) Math.sin(a);
	}

	/**
	 * This implementation wraps {@link java.lang.Math#cos(double)}.
	 * 
	 * @param a
	 *            an angle, in radians.
	 * @return the cosine of the argument.
	 */
	public static float cos(float a) {
		return (float) Math.cos(a);
	}

	/**
	 * This implementation wraps {@link java.lang.Math#floor(double)}.
	 * 
	 * @param a
	 *            a value.
	 * @return the largest (closest to positive infinity) floating-point value
	 *         that less than or equal to the argument and is equal to a
	 *         mathematical integer.
	 */
	public static float floor(float a) {
		return (float) Math.floor(a);
	}

	/**
	 * This implementation wraps {@link java.lang.Math#atan2(double, double)}.
	 * 
	 * @param y
	 *            the ordinate coordinate
	 * @param x
	 *            the abscissa coordinate
	 * @return the theta component of the point (r, theta) in polar coordinates
	 *         that corresponds to the point (x, y) in Cartesian coordinates.
	 */
	public static float atan2(float y, float x) {
		return (float) Math.atan2(y, x);
	}

	/**
	 * This implementation wraps {@link java.lang.Math#pow(double, double)}.
	 * 
	 * @param a
	 *            the base.
	 * @param b
	 *            the exponent.
	 * @return the value of a ^ b.
	 */
	public static float pow(float a, float b) {
		return (float) Math.pow(a, b);
	}

	/**
	 * This implementation wraps {@link java.lang.Math#hypot(double, double)}.
	 * 
	 * @param x
	 *            a value
	 * @param y
	 *            a value
	 * @return sqrt(x ^ 2 + y ^ 2) without intermediate overflow or underflow
	 */
	public static float hypot(float x, float y) {
		return (float) Math.hypot(x, y);
	}

}
