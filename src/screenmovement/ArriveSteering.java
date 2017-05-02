package screenmovement;

import static drawing.SourcePoint.bind;
import static steering.Matching.align;
import static steering.Matching.arrive;
import static steering.Mutation.restrict;
import static steering.Mutation.setAcceleration;
import static steering.Mutation.update;
import static target.Factory.apply;
import static utility.Random.nextBinomial;
import static utility.Random.nextFloat;
import static vector.Factory.ZERO;
import static vector.Factory.create;

import java.util.function.Consumer;
import java.util.function.Function;

import screen.Movement;
import steering.Steering;
import target.Target;
import vector.Vector;

/**
 * Runs the arrive steering algorithm.
 * 
 * <p>
 * Moved instance fields below static fields because instance fields prefer to
 * use initialized static fields.
 * 
 * @author Jacob Malter
 */
public class ArriveSteering extends Movement {

	/** The diameter of the drawn ellipses. */
	private static final float ELLIPSE_DIAMETER = 20f;
	/** The width of the display window in units of pixels. */
	private static final int WIDTH = 800;
	/** The height of the display window in units of pixels. */
	private static final int HEIGHT = 800;

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
	private static final float MAX_SPEED = 0.25f;
	/** The maximum angular speed of the character. */
	private static final float MAX_ANGULAR_SPEED = 0.1f;
	/** The maximum acceleration of the character. */
	private static final float MAX_ACCELERATION = 0.0005f;
	/** The maximum angular acceleration of the character. */
	private static final float MAX_ANGULAR_ACCELERATION = 0.0075f;

	/** How many frames pass between dropping each breadcrumb? */
	private static final int FREQUENCY = 30;

	/** A new draw function that has its pa set to the provided pa. */
	private final Consumer<Target> drawSource;

	/**
	 * Replaces the current character with the result of the function that
	 * accepts the current character and produces a new character.
	 */
	private Consumer<Function<Steering, Steering>> replacer;
	/** The current target for the character to arrive at. */
	private Target target;

	/**
	 * Sets width, height, and diameter.
	 */
	public ArriveSteering() {
		super(WIDTH, HEIGHT, ELLIPSE_DIAMETER);
		drawSource = bind(this);
	}

	@Override
	public void mousePressed() {
		Vector position = create(mouseX, mouseY);
		target = TARGET_FACTORY.apply(position);
	}

	@Override
	public void settings() {
		super.settings();
		Vector position = create(nextBinomial(width / 2, width / 2), nextBinomial(height / 2, height / 2));
		float angle = nextFloat() * TAU;
		Steering character = new Steering(position, ZERO, ZERO, angle, 0, 0);
		replacer = addCharacter(character);
		target = TARGET_FACTORY.apply(character.position());
	}

	@Override
	public void draw() {
		float elapsed = elapsed();
		super.draw();

		drawSource.accept(target);
		replacer.accept((character) -> {
			if (frames() % FREQUENCY == 0) {
				dropBreadcrumb(character.position());
			}

			Vector arrive = arrive(character, target, ARRIVE_SATISFACTION, ARRIVE_DECELERATION, MAX_SPEED, elapsed);
			float align = align(character, ALIGN_ZERO, ALIGN_SATISFACTION, ALIGN_DECELERATION, MAX_ANGULAR_ACCELERATION,
					elapsed);

			character = setAcceleration(character, arrive, align);
			character = restrict(character, width, height, MAX_SPEED, MAX_ANGULAR_SPEED, MAX_ACCELERATION,
					MAX_ANGULAR_ACCELERATION);
			character = update(character, elapsed);

			return character;
		});
	}

}
