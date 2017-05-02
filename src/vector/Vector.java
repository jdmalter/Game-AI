package vector;

import static java.util.Objects.hash;

/**
 * A basic, immutable, unitless, two dimensional representation of magnitude and
 * direction.
 * 
 * <p>
 * I want to avoid managing data after being created. Through immutability, my
 * data structures encourage understandable simplicity, prevent defensive
 * programming, and easily achieve parallelism.
 * 
 * <p>
 * Immutable data structures become values rather than objects. In my design,
 * classes and fields accomplish immutability with final modifiers and a clean
 * API. To access data from values, getter methods produce values. Since fields
 * returned from getter methods are immutable, those fields always remain the
 * same. Treating objects as values minimizes the amount code in each object to
 * constructors, getter methods, and overridden methods. With the amount of code
 * minimized, these immutable data structures become simple and short;
 * therefore, these classes tend to be easier to understand and use.
 * 
 * <p>
 * Value based data structures contain more invariants than object based data
 * structures. In my constructors, instances reject invalid parameters
 * (including null parameters). Any value returned from a getter method must be
 * valid. When there is a guarantee that returned values are valid, client code
 * skips validation checks and focuses on what needs to be done rather than how
 * work is done. Throughout examples of value based data structures, those
 * functions use fewer checks on parameters and less instructions.
 * 
 * <p>
 * Immutable objects safely live in multi-threaded environments. When data is
 * guaranteed to remain the same, data in one execution context never changes
 * from updates in another execution context. Between threads, the same objects
 * provide data and optimization opportunities. Using the Java stream package,
 * traversing a collection of data structures guarantees safe access to any
 * element. When every element exists safely inside a collection, parallel
 * traversals through Java streams provide significantly faster execution at the
 * cost of trivial amounts of code.
 * 
 * <p>
 * Here is some additional reading:
 * <ul>
 * <li><a href="https://facebook.github.io/immutable-js/">Immutable.js</a></li>
 * <li><a href=
 * "https://www.sitepoint.com/functional-programming-ruby-value-objects/">Value
 * objects</a></li>
 * </ul>
 * 
 * @author Jacob Malter
 */
public final class Vector {

	/** The magnitude in a horizontal dimension. */
	private final float x;
	/** The magnitude in a vertical dimension. */
	private final float y;
	/** Cache the hash. */
	private final int hashCode;

	/**
	 * Initializes a new Vector that represents the given magnitudes in the
	 * horizontal and vertical dimensions.
	 * 
	 * @param x
	 *            The magnitude in a horizontal dimension.
	 * @param y
	 *            The magnitude in a vertical dimension.
	 */
	protected Vector(float x, float y) {
		this.x = x;
		this.y = y;
		this.hashCode = hash(this.x, this.y);
	}

	/**
	 * @return The magnitude in a horizontal dimension.
	 */
	public float x() {
		return x;
	}

	/**
	 * @return The magnitude in a vertical dimension.
	 */
	public float y() {
		return y;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{:x " + x + " :y " + y + "}"; // technically cachable
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Vector)) {
			return false;
		}

		Vector other = (Vector) obj;
		return x == other.x && y == other.y;
	}

}
