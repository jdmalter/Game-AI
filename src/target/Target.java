package target;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static utility.Mathf.abs;

import java.util.Objects;

import vector.Vector;

/**
 * A basic, immutable, unitless, two dimensional representation of a target data
 * structure.
 * 
 * <p>
 * After Homework 1, I moved position from the head of the fields to the tail of
 * the fields because it changed the most frequently which made the least sense
 * to partially apply first. Additionally, the constructor for Target became
 * protected so that client code must use the apply function in Factory.
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
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * @author Jacob Malter
 */
public final class Target {

	/** The distance below which errors are ignored. */
	private final float satisfaction;
	/** The distance below which velocity should be spead. */
	private final float decceleration;
	/** The position vector. */
	private final Vector position;
	/** Cache the hash. */
	private final int hashCode;

	/**
	 * Takes the absolute value of each radius.
	 * 
	 * @param satisfaction
	 *            The distance below which errors are ignored.
	 * @param decceleration
	 *            The distance below which velocity should be spead.
	 * @param position
	 *            The position vector.
	 * @throws NullPointerException
	 *             if position is null
	 */
	protected Target(float satisfaction, float decceleration, Vector position) {
		this.satisfaction = abs(satisfaction);
		this.decceleration = abs(decceleration);
		this.position = requireNonNull(position);
		this.hashCode = hash(this.satisfaction, this.decceleration, this.position);
	}

	/**
	 * @return The distance below which errors are ignored.
	 */
	public float satisfaction() {
		return satisfaction;
	}

	/**
	 * @return The distance below which velocity should be spead.
	 */
	public float decceleration() {
		return decceleration;
	}

	/**
	 * @return The position vector.
	 */
	public Vector position() {
		return position;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder().append("{:satisfaction ").append(satisfaction).append(" :decceleration ")
				.append(decceleration).append(" :position ").append(position).append("}").toString();
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

		if (!(obj instanceof Target)) {
			return false;
		}

		Target other = (Target) obj;
		// compiler's type inference isn't smart enough to use equals without
		// seeing Objects.equals
		return satisfaction == other.satisfaction && decceleration == other.decceleration
				&& Objects.equals(position, other.position);
	}

}
