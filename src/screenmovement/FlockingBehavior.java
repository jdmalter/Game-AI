package screenmovement;

import static function.HigherOrder.comparator;
import static function.HigherOrder.compose;
import static java.util.Arrays.asList;
import static steering.Blender.weightedBlend;
import static steering.Complex.avoid;
import static steering.FixedRadiusNearestNeighbor.neighbors;
import static steering.Matching.align;
import static steering.Matching.arrive;
import static steering.Mutation.restrict;
import static steering.Mutation.setAcceleration;
import static steering.Mutation.update;
import static target.Factory.apply;
import static utility.Random.nextBinomial;
import static utility.Random.nextFloat;
import static vector.Arithmetic.divide;
import static vector.Arithmetic.multiply;
import static vector.Arithmetic.subtract;
import static vector.Factory.ZERO;
import static vector.Factory.create;
import static vector.Property.direction;
import static vector.Property.magnitude;
import static vector.Property.unit;
import static vector.Streams.sum;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import screen.Movement;
import steering.Steering;
import target.Target;
import utility.Mathf;
import vector.Property;
import vector.Vector;

/**
 * Runs the flocking behavior algorithm.
 * 
 * <p>
 * Moved instance fields below static fields because instance fields prefer to
 * use initialized static fields.
 * 
 * @author Jacob Malter
 */
public class FlockingBehavior extends Movement {

	/** The width of the display window in units of pixels. */
	private static final int WIDTH = 1440;
	/** The height of the display window in units of pixels. */
	private static final int HEIGHT = 810;
	/** The diameter of the drawn ellipses. */
	private static final float ELLIPSE_DIAMETER = 5f;

	/** How many characters are drawn. */
	private static final int CHARACTERS = 500;

	/** The maximum time in milliseconds to predict a collision. */
	private static final float COLLISION_TIME = 2000f;
	/** The radius of collision for characters. */
	private static final float COLLISION_RADIUS = 2.5f * ELLIPSE_DIAMETER;

	/** How much of blending should be separation? */
	private static final float WEIGHT_SEPARATION = 0.8f;
	/** How much of blending should be alignment? */
	private static final float WEIGHT_ALIGNMENT = 0.1f;
	/** How much of blending should be cohesion? */
	private static final float WEIGHT_COHESION = 0.1f;

	/** The mean number of frames between new wander targets. */
	private static final long MEAN = 90;
	/**
	 * The farthest number of frames between new wander targets from the mean.
	 */
	private static final long RANGE = 30;

	/** How far the wander can turn. */
	private static final float WANDER_RATE = PI / 2;

	/** The radius of satisfaction for targets. */
	private static final float ARRIVE_SATISFACTION = 2;
	/** The radius of deceleration for targets. */
	private static final float ARRIVE_DECELERATION = 2;

	/** Creates targets with the applied radius of satisfaction. */
	private static final Function<Vector, Target> TARGET_FACTORY = apply(ARRIVE_SATISFACTION, ARRIVE_DECELERATION);

	/** The radius of zero for angular targets. */
	private static final float ALIGN_ZERO = 0.0001f;
	/** The radius of satisfaction for angular targets. */
	private static final float ALIGN_SATISFACTION = PI / 360;
	/** The radius of deceleration for angular targets. */
	private static final float ALIGN_DECELERATION = PI / 3;

	/** The maximum speed of the character. */
	private static final float MAX_SPEED = 0.1f;
	/** The maximum angular speed of the character. */
	private static final float MAX_ANGULAR_SPEED = 0.1f;
	/** The maximum acceleration of the character. */
	private static final float MAX_ACCELERATION = 0.001f;
	/** The maximum angular acceleration of the character. */
	private static final float MAX_ANGULAR_ACCELERATION = 0.005f;

	/**
	 * The maximum distance between two characters below which two characters
	 * are considered neighbors.
	 */
	private static final float LOCALITY_RADIUS = 250;

	/**
	 * Every replacer for the current character with the result of the function
	 * that accepts the current character and produces a new character.
	 */
	private Set<Consumer<Function<Steering, Steering>>> replacers;
	/**
	 * How many times {@link #draw()} has been called since the last target
	 * update.
	 */
	private int lastframe;

	/**
	 * Sets width, height, and diameter.
	 */
	public FlockingBehavior() {
		super(WIDTH, HEIGHT, ELLIPSE_DIAMETER);
	}

	@Override
	public void settings() {
		super.settings();
		replacers = new HashSet<Consumer<Function<Steering, Steering>>>();
		for (int i = 0; i < CHARACTERS; i++) {
			Vector position = create(nextFloat() * width, nextFloat() * height);
			float angle = nextFloat() * TAU;
			Steering character = new Steering(position, ZERO, ZERO, angle, 0, 0);
			replacers.add(addCharacter(character));
		}
	}

	private Vector wind = multiply(create(nextFloat() * TAU), MAX_ACCELERATION);

	@Override
	public void draw() {
		float elapsed = elapsed();
		super.draw();

		if (frames() - lastframe > nextBinomial(MEAN, RANGE)) {
			lastframe = frames();
			float angle = (nextBinomial() * WANDER_RATE) + direction(wind);
			wind = multiply(create(angle), MAX_ACCELERATION);
		}

		Map<Vector, Collection<Steering>> buckets = bucketCharacters(LOCALITY_RADIUS);

		replacers.parallelStream().forEach((replacer) -> {
			replacer.accept((character) -> {
				Vector arbitartion = character.acceleration();
				if (frames() % 5 == 0) {
					Collection<Steering> nearNeighbors = neighbors(buckets, character, LOCALITY_RADIUS);

					// funny note: using max with lessThan predicate is
					// equivalent to using min with greaterThan predicate

					Steering reference = character; // eclipse is not smart

					if (nearNeighbors.size() > 0) {

						// Find separtion vector from choosing avoid steering
						// vector with greatest magnitude.
						Vector separation = nearNeighbors.parallelStream().map((neighbor) -> {
							return avoid(reference, neighbor, COLLISION_TIME, COLLISION_RADIUS, MAX_ACCELERATION);
						}).max(compose(comparator(Mathf::lessThan), Property::magnitude)::apply).get();

						// Compute alignment from average velocity.
						Vector averageVelocity = divide(sum(nearNeighbors.stream().map(Steering::velocity)),
								nearNeighbors.size());
						Vector alignment = multiply(unit(subtract(reference.velocity(), averageVelocity)),
								MAX_ACCELERATION);

						// Find cohesion from average position.
						Vector averagePosition = divide(sum(nearNeighbors.stream().map(Steering::position)),
								nearNeighbors.size());
						Target cohesionTarget = TARGET_FACTORY.apply(averagePosition);
						Vector cohesion = multiply(unit(arrive(reference, cohesionTarget, ARRIVE_SATISFACTION,
								ARRIVE_DECELERATION, MAX_SPEED, elapsed)), MAX_ACCELERATION);

						if (magnitude(separation) < MAX_ACCELERATION * 0.1) {
							arbitartion = weightedBlend(asList(alignment, cohesion), asList(1f, 1f));
						} else if (magnitude(separation) < MAX_ACCELERATION * 0.5) {
							arbitartion = weightedBlend(asList(separation, alignment, cohesion),
									asList(WEIGHT_SEPARATION, WEIGHT_ALIGNMENT, WEIGHT_COHESION));
						} else {
							arbitartion = separation;
						}
					}
				}
				arbitartion = weightedBlend(asList(arbitartion, wind), asList(1f, 0.5f));
				float align = align(character, ALIGN_ZERO, ALIGN_SATISFACTION, ALIGN_DECELERATION,
						MAX_ANGULAR_ACCELERATION, elapsed);

				character = setAcceleration(character, arbitartion, align);
				character = restrict(character, width, height, MAX_SPEED, MAX_ANGULAR_SPEED, MAX_ACCELERATION,
						MAX_ANGULAR_ACCELERATION);
				character = update(character, elapsed);

				return character;
			});
		});
	}

}
