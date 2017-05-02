package steering;

import static java.util.Objects.requireNonNull;

import vector.Vector;

/**
 * A basic, immutable, two dimensional representation of a steering data
 * structure.
 * 
 * <p>
 * Although there are technically no units, here it is implied that units are
 * pixels, pixels per second, pixels per second per second, radians, radians per
 * second, and radians per second per second.
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
public final class Steering {

	/** The position vector. */
	private final Vector position;
	/** The velocity vector or change in the position vector. */
	private final Vector velocity;
	/** The acceleration vector or the change in the velocity vector. */
	private final Vector acceleration;
	/** The angular position. */
	private final float angle;
	/** The angular velocity or change in the angular position. */
	private final float angularVelocity;
	/** The angular acceleration of change in the angular velocity. */
	private final float angularAcceleration;

	/**
	 * @param position
	 *            The position vector.
	 * @param velocity
	 *            The velocity vector or change in the position vector.
	 * @param acceleration
	 *            The acceleration vector or the change in the velocity vector.
	 * @param angle
	 *            The angular position.
	 * @param angularVelocity
	 *            The angular velocity or change in the angular position.
	 * @param angularAcceleration
	 *            The angular acceleration of change in the angular velocity.
	 * @throws NullPointerException
	 *             if position, vector, or acceleration is null
	 */
	public Steering(Vector position, Vector velocity, Vector acceleration, float angle, float angularVelocity,
			float angularAcceleration) {
		this.position = requireNonNull(position);
		this.velocity = requireNonNull(velocity);
		this.acceleration = requireNonNull(acceleration);
		this.angle = angle;
		this.angularVelocity = angularVelocity;
		this.angularAcceleration = angularAcceleration;
	}

	/**
	 * @return The position vector.
	 */
	public Vector position() {
		return position;
	}

	/**
	 * @return The velocity vector or change in the position vector.
	 */
	public Vector velocity() {
		return velocity;
	}

	/**
	 * @return The acceleration vector or the change in the velocity vector.
	 */
	public Vector acceleration() {
		return acceleration;
	}

	/**
	 * @return The angular position.
	 */
	public float angle() {
		return angle;
	}

	/**
	 * @return The angular velocity or change in the angular position.
	 */
	public float angularVelocity() {
		return angularVelocity;
	}

	/**
	 * @return The angular acceleration of change in the angular velocity.
	 */
	public float angularAcceleration() {
		return angularAcceleration;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder().append("{:position ").append(position).append(" :velocity ").append(velocity)
				.append(" :acceleration ").append(acceleration).append(" :angle ").append(angle)
				.append(" :angularVelocity ").append(angularVelocity).append(" :angularAcceleration ")
				.append(angularAcceleration).append("}").toString();
	}

}
