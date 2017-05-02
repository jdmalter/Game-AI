package steering;

import static utility.Mathf.angle;
import static vector.Arithmetic.divide;
import static vector.Arithmetic.multiply;
import static vector.Arithmetic.subtract;
import static vector.Factory.ZERO;
import static vector.Property.direction;
import static vector.Property.magnitude;
import static vector.Property.unit;

import target.Target;
import vector.Vector;

/**
 * Provides linear and angular position matching behaviors.
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
public final class Matching {

	/**
	 * Cannot be instantiated by users.
	 */
	private Matching() {

	}

	/**
	 * @param s
	 *            A steering data structure.
	 * @param t
	 *            A target data structure.
	 * @return An acceleration which equals the difference of the target
	 *         position and the current position.
	 */
	public static Vector seek(Steering s, Target t) {
		return subtract(t.position(), s.position());
	}

	/**
	 * @param s
	 *            A steering data structure.
	 * @param t
	 *            A target data structure.
	 * @param ros
	 *            The radius of satisfaction.
	 * @param rod
	 *            The radius of decceleration.
	 * @param c
	 *            The maximum speed.
	 * @param ttt
	 *            The time to target.
	 * @return An acceleration which changes the current velocity to the
	 *         recommended velocity (whose speed is less than c) during the time
	 *         to target veloocity.
	 */
	public static Vector arrive(Steering s, Target t, float ros, float rod, float c, float ttt) {
		Vector velocity = subtract(t.position(), s.position());
		float speed = magnitude(velocity);

		float goalSpeed = c;
		if (speed < ros) {
			goalSpeed = 0;
		} else if (speed < rod) {
			goalSpeed *= (speed / rod);
		}

		velocity = goalSpeed == 0 ? ZERO : multiply(unit(velocity), goalSpeed);
		Vector acceleration = subtract(velocity, s.velocity());
		return divide(acceleration, ttt);
	}

	/**
	 * @param s
	 *            A steering data structure.
	 * @param roz
	 *            The radius of zero.
	 * @param ros
	 *            The radius of satisfaction.
	 * @param rod
	 *            The radius of decceleration.
	 * @param c
	 *            The maximum angular speed.
	 * @param ttt
	 *            The time to target.
	 * @return An angular acceleration which changes the current angular
	 *         velocity to the recommended angular velocity (whose speed is less
	 *         than c) during the time to target veloocity.
	 */
	public static float align(Steering s, float roz, float ros, float rod, float c, float ttt) {
		float angularVelocity = Math.abs(magnitude(s.velocity())) < roz ? 0 : direction(s.velocity()) - s.angle();
		angularVelocity = angle(angularVelocity);
		float angularSpeed = Math.abs(angularVelocity);

		float goalAngularSpeed = c;
		if (angularSpeed < ros) {
			goalAngularSpeed = 0;
		} else if (angularSpeed < rod) {
			goalAngularSpeed *= (angularSpeed / rod);
		}

		angularVelocity = goalAngularSpeed == 0 ? 0 : (angularVelocity / angularSpeed) * goalAngularSpeed;
		float angularAcceleration = angularVelocity - s.angularVelocity();
		return angularAcceleration / ttt;
	}

}
