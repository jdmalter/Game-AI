package screenmovement;

import static java.util.Arrays.asList;
import static steering.Mutation.restrict;
import static steering.Mutation.update;
import static target.Factory.apply;
import static target.Predicate.satisfied;
import static utility.Mathf.angle;
import static vector.Arithmetic.subtract;
import static vector.Factory.ZERO;
import static vector.Factory.create;
import static vector.Property.direction;
import static vector.Property.magnitude;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import screen.Movement;
import steering.Steering;
import target.Target;
import vector.Vector;

/**
 * Runs the kinematic motion algorithm.
 * 
 * <p>
 * Moved instance fields below static fields because instance fields prefer to
 * use initialized static fields.
 * 
 * <p>
 * I accomplished kinematic motion in the kinematic package. To record data,
 * Kinematic, an immutable data structure, stores linear and angular position
 * and velocity data. Matching provides linear and angular position seek with
 * pure functions. Mutation class contains setting, restriction, and update with
 * pure functions. Inside the screen package, BasicMotion applies the previous
 * classes to traverse a character around the edge of the screen.
 * 
 * <p>
 * The radius of satisfaction for the targets impacts motion. As the radius of
 * satisfaction increases, the character changes its target further away from
 * its current targeted corner. A high radius of satisfaction produces a more
 * rounded path around the edges of the screen, but a lower radius of
 * satisfaction produces a sharper path around the edges of the screen. However
 * if the radius of satisfaction is too small, there is some brief oscillation
 * around the targets. To create sensible behavior, iterate the radius of
 * satisfaction with smaller steps until the path looks sensible.
 * 
 * <p>
 * The maximum linear and angular speed changes the look of motion. As the
 * velocity increases, the character oscillates around its target before its
 * motion stops. For linear position, the character move forward and back around
 * the target for some random time, and for angular position, the character
 * spins around wildly while making no progress towards matching its angle with
 * its velocity. When the velocity decreases, the character tends to
 * (unsurprisingly) move slower.
 * 
 * @author Jacob Malter
 */
public class BasicMotion extends Movement {

	/** The width of the display window in units of pixels. */
	private static final int WIDTH = 400;
	/** The height of the display window in units of pixels. */
	private static final int HEIGHT = 400;

	/** How far from the edge are the targets? */
	private static final float PADDING = 50;

	/** The radius of satisfaction for targets. */
	private static final float SATISFACTION = 1.5f;
	/** Creates targets with the applied radius of satisfaction. */
	private static final BiFunction<Float, Float, Target> TARGET_FACTORY = (x, y) -> apply(SATISFACTION, SATISFACTION)
			.apply(create(x, y));

	/** The maximum speed of the character. */
	private static final float MAX_SPEED = 0.16f;
	/** The maximum angular speed of the character. */
	private static final float MAX_ANGULAR_SPEED = 0.003f;

	/** How many frames pass between dropping each breadcrumb? */
	private static final int FREQUENCY = 30;

	/** A position in the path. */
	private int index;
	/** An ordered collection of target data structures. */
	private List<Target> path;

	/**
	 * Replaces the current character with the result of the function that
	 * accepts the current character and produces a new character.
	 */
	private Consumer<Function<Steering, Steering>> replacer;

	/**
	 * @param s
	 *            A steering data structure.
	 * @param t
	 *            A target data structure.
	 * @return A linear velocity which equals the difference of the target
	 *         position and the current position.
	 */
	private static Vector seek(Steering s, Target t) {
		return subtract(t.position(), s.position());
	}

	/**
	 * @param s
	 *            A steering data structure.
	 * @return An angular velocity which equals the difference between the angle
	 *         of the velocity and the current angle.
	 */
	private static float align(Steering s) {
		float angularVelocity = magnitude(s.velocity()) == 0 ? 0 : direction(s.velocity()) - s.angle();
		return angle(angularVelocity);
	}

	/**
	 * Sets width, height, and diameter.
	 */
	public BasicMotion() {
		width = WIDTH;
		height = HEIGHT;
	}

	@Override
	public void settings() {
		super.settings();
		index = 0;
		path = asList(TARGET_FACTORY.apply(PADDING, height - PADDING),
				TARGET_FACTORY.apply(width - PADDING, height - PADDING), TARGET_FACTORY.apply(width - PADDING, PADDING),
				TARGET_FACTORY.apply(PADDING, PADDING));
		Vector position = create(PADDING, height - PADDING);
		Steering character = new Steering(position, ZERO, ZERO, 0, 0, 0);
		replacer = addCharacter(character);
	}

	@Override
	public void draw() {
		float elapsed = elapsed();
		super.draw();

		replacer.accept((character) -> {
			if (frames() % FREQUENCY == 0) {
				dropBreadcrumb(character.position());
			}

			if (satisfied(path.get(index), character.position())) {
				index = (index + 1) % path.size();
			}

			Vector velocity = seek(character, path.get(index));
			float angularVelocity = align(character);

			character = new Steering(character.position(), velocity, character.acceleration(), character.angle(),
					angularVelocity, character.angularAcceleration());
			character = restrict(character, width, height, MAX_SPEED, MAX_ANGULAR_SPEED, 0, 0);
			character = update(character, elapsed);

			return character;
		});
	}

}
