package steering;

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
 * Provides steering setting, restriction, and update functions.
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
	 * @param s
	 *            A steering data structure.
	 * @param a
	 *            An acceleration vector.
	 * @param aa
	 *            An angular acceleration.
	 * @return An updated steering data structure whose acceleration equals the
	 *         given acceleration vector and angular acceleration equals the
	 *         given angular acceleration.
	 */
	public static Steering setAcceleration(Steering s, Vector a, float aa) {
		return new Steering(s.position(), s.velocity(), a, s.angle(), s.angularVelocity(), aa);
	}

	/**
	 * @param s
	 *            A steering data structure.
	 * @param xl
	 *            The horizontal position limit.
	 * @param yl
	 *            The vertical position limit.
	 * @param sl
	 *            The speed limit.
	 * @param asl
	 *            The angular speed limit.
	 * @param al
	 *            The acceleration limit.
	 * @param aal
	 *            The angular acceleration limit.
	 * @return An updated steering data structure whose angular and linear
	 *         speeds and accelerations are less than their respective limits.
	 */
	public static Steering restrict(Steering s, float xl, float yl, float sl, float asl, float al, float aal) {
		// clip linear position
		float x = s.position().x();
		x = x > xl ? 0 : (x < 0 ? xl : x);
		float y = s.position().y();
		y = y > yl ? 0 : (y < 0 ? yl : y);
		Vector position = create(x, y);

		// clip angular position
		float angle = angle(s.angle());

		// clip linear velocity
		float speed = magnitude(s.velocity());
		float limitedSpeed = abs(speed) < sl ? speed : sl;
		Vector velocity = limitedSpeed == 0 ? ZERO : multiply(unit(s.velocity()), limitedSpeed);

		// clip angular velocity
		float absoluteAcceleration = magnitude(s.acceleration());
		float limitedAcceleration = abs(absoluteAcceleration) < al ? absoluteAcceleration : al;
		Vector acceleration = absoluteAcceleration == 0 ? ZERO : multiply(unit(s.acceleration()), limitedAcceleration);

		// clip linear acceleration
		float angularSpeed = abs(s.angularVelocity());
		float direction = angularSpeed == 0 ? 1 : s.angularVelocity() / angularSpeed;
		angularSpeed = angularSpeed < asl ? angularSpeed : asl;
		float angularVelocity = angularSpeed < asl ? s.angularVelocity() : direction * angularSpeed;

		// clip angular acceleration
		float absoluteAngularAcceleration = abs(s.angularAcceleration());
		direction = absoluteAngularAcceleration == 0 ? 1 : s.angularAcceleration() / absoluteAngularAcceleration;
		absoluteAngularAcceleration = absoluteAngularAcceleration < aal ? absoluteAngularAcceleration : aal;
		float angularAcceleration = absoluteAngularAcceleration < aal ? s.angularAcceleration()
				: direction * absoluteAngularAcceleration;

		return new Steering(position, velocity, acceleration, angle, angularVelocity, angularAcceleration);
	}

	/**
	 * @param s
	 *            A steering data structure.
	 * @param dt
	 *            The change in time.
	 * @return An updated steering data structure whose angular and linear
	 *         positions and velocities increased by the product of their
	 *         respective velocities and accelerations and the change in time.
	 */
	public static Steering update(Steering s, float dt) {
		Vector position = add(s.position(), multiply(s.velocity(), dt));
		float angle = s.angle() + s.angularVelocity() * dt;

		Vector velocity = add(s.velocity(), multiply(s.acceleration(), dt));
		float angularVelocity = s.angularVelocity() + s.angularAcceleration() * dt;

		return new Steering(position, velocity, s.acceleration(), angle, angularVelocity, s.angularAcceleration());
	}

}
