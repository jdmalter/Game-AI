package screenmovement;

import static steering.Complex.wander;
import static steering.Matching.align;
import static steering.Matching.arrive;
import static steering.Mutation.restrict;
import static steering.Mutation.setAcceleration;
import static steering.Mutation.update;
import static target.Factory.apply;
import static utility.Random.nextBinomial;
import static utility.Random.nextFloat;
import static vector.Arithmetic.multiply;
import static vector.Factory.create;
import static vector.Property.direction;

import java.util.function.Consumer;
import java.util.function.Function;

import screen.Movement;
import steering.Steering;
import target.Target;
import vector.Vector;

/**
 * Runs the wander steering algorithm.
 * 
 * <p>
 * Moved instance fields below static fields because instance fields prefer to
 * use initialized static fields.
 * 
 * @author Jacob Malter
 */
public class WanderSteering extends Movement {

	/** The mean number of frames between new wander targets. */
	private static final long MEAN = 60;
	/**
	 * The farthest number of frames between new wander targets from the mean.
	 */
	private static final long RANGE = 30;

	/** How far the wander can turn. */
	private static final float WANDER_RATE = PI;
	/**
	 * On average, how far ahead of the character are the positions that wander
	 * returns?
	 */
	private static final float WANDER_OFFSET = 50;
	/**
	 * How are are the positions from the center of the wander offset circle?
	 */
	private static final float WANDER_RADIUS = 100;

	/** The radius of satisfaction for targets. */
	private static final float ARRIVE_SATISFACTION = 2;
	/** The radius of deceleration for targets. */
	private static final float ARRIVE_DECELERATION = 100;

	/** Creates targets with the applied radius of satisfaction. */
	private static final Function<Vector, Target> TARGET_FACTORY = apply(ARRIVE_SATISFACTION, ARRIVE_DECELERATION);

	/** The radius of zero for angular targets. */
	private static final float ALIGN_ZERO = 0.0001f;
	/** The radius of satisfaction for angular targets. */
	private static final float ALIGN_SATISFACTION = PI / 360;
	/** The radius of deceleration for angular targets. */
	private static final float ALIGN_DECELERATION = PI / 3;

	/** The maximum speed of the character. */
	private static final float MAX_SPEED = 0.15f;
	/** The maximum angular speed of the character. */
	private static final float MAX_ANGULAR_SPEED = 0.1f;
	/** The maximum acceleration of the character. */
	private static final float MAX_ACCELERATION = 0.0002f;
	/** The maximum angular acceleration of the character. */
	private static final float MAX_ANGULAR_ACCELERATION = 0.0075f;

	/** How many frames pass between dropping each breadcrumb? */
	private static final int FREQUENCY = 30;

	/**
	 * Replaces the current character with the result of the function that
	 * accepts the current character and produces a new character.
	 */
	private Consumer<Function<Steering, Steering>> replacer;
	/**
	 * How many times {@link #draw()} has been called since the last target
	 * update.
	 */
	private int lastframe;

	@Override
	public void settings() {
		super.settings();
		Vector position = create(nextBinomial(width / 2, width / 2), nextBinomial(height / 2, height / 2));
		Vector velocity = multiply(create(nextFloat() * TAU), nextBinomial() * MAX_SPEED);
		Vector acceleration = multiply(create(nextFloat() * TAU), nextBinomial() * MAX_ACCELERATION);
		float angle = direction(velocity);
		float angularVelocity = nextFloat() * TAU;
		Steering character = new Steering(position, velocity, acceleration, angle, angularVelocity, 0);
		replacer = addCharacter(character);
		lastframe = frames();
	}

	@Override
	public void draw() {
		float elapsed = elapsed();
		super.draw();

		replacer.accept((character) -> {
			if (frames() % FREQUENCY == 0) {
				dropBreadcrumb(character.position());
			}

			Vector acceleration = character.acceleration();
			if (frames() - lastframe > nextBinomial(MEAN, RANGE)) {
				lastframe = frames();
				Target target = TARGET_FACTORY.apply(wander(character, WANDER_RATE, WANDER_OFFSET, WANDER_RADIUS));
				acceleration = arrive(character, target, ARRIVE_SATISFACTION, ARRIVE_DECELERATION, MAX_SPEED, elapsed);
			}
			float align = align(character, ALIGN_ZERO, ALIGN_SATISFACTION, ALIGN_DECELERATION, MAX_ANGULAR_ACCELERATION,
					elapsed);

			character = setAcceleration(character, acceleration, align);
			character = restrict(character, width, height, MAX_SPEED, MAX_ANGULAR_SPEED, MAX_ACCELERATION,
					MAX_ANGULAR_ACCELERATION);
			character = update(character, elapsed);

			return character;
		});
	}

}
