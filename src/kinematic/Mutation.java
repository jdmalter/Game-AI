package kinematic;

import static utility.Mathf.abs;
import static utility.Mathf.angle;
import static vector.Arithmetic.add;
import static vector.Arithmetic.multiply;
import static vector.Factory.ZERO;
import static vector.Factory.create;
import static vector.Property.magnitude;
import static vector.Property.unit;

import vector.Vector;

/**
 * Provides kinematic setting, restriction, and update functions.
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
public final class Mutation {

	/**
	 * Cannot be instantiated by users.
	 */
	private Mutation() {

	}

	/**
	 * @param k
	 *            A kinematic data structure.
	 * @param v
	 *            A velocity vector.
	 * @param av
	 *            An angular velocity.
	 * @return An updated kinematic data structure whose velocity equals the
	 *         given velocity vector and angular velocity equals the given
	 *         angular velocity.
	 */
	public static Kinematic setVelocity(Kinematic k, Vector v, float av) {
		return new Kinematic(k.position(), v, k.angle(), av);
	}

	/**
	 * @param k
	 *            A kinematic data structure.
	 * @param xl
	 *            The horizontal position limit.
	 * @param yl
	 *            The vertical position limit.
	 * @param sl
	 *            The speed limit.
	 * @param asl
	 *            The angular speed limit.
	 * @return An updated kinematic data structure whose angular and linear
	 *         speeds are less than their respective limits.
	 */
	public static Kinematic restrict(Kinematic k, float xl, float yl, float sl, float asl) {
		// clip linear position
		float x = k.position().x();
		x = x > xl ? 0 : (x < 0 ? xl : x);
		float y = k.position().y();
		y = y > yl ? 0 : (y < 0 ? yl : y);
		Vector position = create(x, y);

		// clip angular position
		float angle = angle(k.angle());

		// clip linear velocity
		float speed = magnitude(k.velocity());
		speed = abs(speed) < sl ? speed : sl;
		Vector velocity = magnitude(k.velocity()) == 0 ? ZERO : multiply(unit(k.velocity()), speed);

		// clip angular velocity
		float angularSpeed = abs(k.angularVelocity());
		float direction = angularSpeed == 0 ? 1 : k.angularVelocity() / angularSpeed;
		angularSpeed = angularSpeed < asl ? angularSpeed : asl;
		float angularVelocity = angularSpeed < asl ? k.angularVelocity() : direction * angularSpeed;

		return new Kinematic(position, velocity, angle, angularVelocity);
	}

	/**
	 * @param k
	 *            A kinematic data structure.
	 * @param dt
	 *            The change in time.
	 * @return An updated kinematic data structure whose angular and linear
	 *         positions increased by the product of their respective velocities
	 *         and the change in time.
	 */
	public static Kinematic update(Kinematic k, float dt) {
		Vector position = add(k.position(), multiply(k.velocity(), dt));
		float angle = k.angle() + k.angularVelocity() * dt;

		return new Kinematic(position, k.velocity(), angle, k.angularVelocity());
	}

}