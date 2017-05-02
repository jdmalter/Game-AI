package steering;

import static utility.Mathf.pow;
import static utility.Random.nextBinomial;
import static vector.Arithmetic.add;
import static vector.Arithmetic.dot;
import static vector.Arithmetic.multiply;
import static vector.Arithmetic.subtract;
import static vector.Factory.ZERO;
import static vector.Factory.create;
import static vector.Property.magnitude;
import static vector.Property.unit;

import vector.Vector;

/**
 * Provides steering wander, pursue, and avoid functions.
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
public final class Complex {

	/**
	 * Cannot be instantiated by users.
	 */
	private Complex() {

	}

	/**
	 * This function relies on some random binomial to select a position.
	 * 
	 * @param s
	 *            A steering data structure.
	 * @param c
	 *            The wander rate.
	 * @param o
	 *            The wander offset.
	 * @param r
	 *            The wander radius.
	 * @return Some random point from in front of the character.
	 */
	public static Vector wander(Steering s, float c, float o, float r) {
		float angle = (nextBinomial() * c) + s.angle();
		Vector position = add(s.position(), multiply(create(s.angle()), o));

		return add(position, multiply(create(angle), r));
	}

	/**
	 * @param s
	 *            A steering data structure.
	 * @param t
	 *            A targeted steering data structure.
	 * @param p
	 *            How far into the future pursue predicts the targeted
	 *            steering's motion.
	 * @return A position vector for where the targeted data structure will be
	 *         at the time of prediction.
	 */
	public static Vector pursue(Steering s, Steering t, float p) {
		Vector direction = subtract(t.position(), s.position());
		float distance = magnitude(direction);
		float speed = magnitude(s.velocity());
		float prediction = speed <= (distance / p) ? p : (distance / speed);

		return add(t.position(), multiply(t.velocity(), prediction));
	}

	/**
	 * 
	 * @param s
	 *            A steering data structure.
	 * @param t
	 *            A targeted steering data structure.
	 * @param mt
	 *            The maximum time.
	 * @param r
	 *            The radius of collision.
	 * @param c
	 *            The maximum acceleration.
	 * @return An acceleration vector for how to avoid a collision with the
	 *         target, or if no collision is possibile within the maximum time,
	 *         a vector whose length is 0 is returned.
	 */
	public static Vector avoid(Steering s, Steering t, float mt, float r, float c) {
		Vector relativePosition = subtract(s.position(), t.position());
		Vector relativeVelocity = subtract(s.velocity(), t.velocity());
		float time = -dot(relativePosition, relativeVelocity) / pow(magnitude(relativeVelocity), 2f);

		if (magnitude(relativePosition) < 2 * r) {
			return multiply(unit(relativePosition), c);
		}

		if (0 < time && time < mt) {
			Vector futureS = add(s.position(), multiply(s.velocity(), time));
			Vector futureT = add(s.position(), multiply(s.velocity(), time));
			Vector closest = subtract(futureS, futureT);

			if (magnitude(closest) < 2 * r) {
				return multiply(unit(closest), c - (c * time / mt));
			}
		}

		return ZERO;
	}

}
