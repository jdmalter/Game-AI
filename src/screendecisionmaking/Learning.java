package screendecisionmaking;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static sequence.Sequences.conj;
import static sequence.Sequences.empty;
import static steering.Complex.pursue;
import static steering.Matching.align;
import static steering.Matching.arrive;
import static steering.Matching.seek;
import static steering.Mutation.restrict;
import static steering.Mutation.setAcceleration;
import static steering.Mutation.update;
import static target.Predicate.satisfied;
import static utility.Random.nextFloat;
import static vector.Factory.ZERO;
import static vector.Factory.create;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import decisiontree.Tree;
import learning.Model;
import learning.Models;
import sequence.Sequence;
import steering.Steering;
import target.Target;
import vector.Vector;

/**
 * Runs the decision tree learning algorithm.
 * 
 * @author Jacob Malter
 */
public class Learning extends DecisionTree {

	/** The radius of zero for angular targets. */
	private static final float ALIGN_ZERO = 0.0001f;
	/** The radius of satisfaction for angular targets. */
	private static final float ALIGN_SATISFACTION = PI / 360;
	/** The radius of deceleration for angular targets. */
	private static final float ALIGN_DECELERATION = PI / 2;

	/** The maximum speed of the character. */
	private static final float SEEK_MAX_SPEED = 0.175f;
	/** The maximum speed of the character. */
	private static final float WANDER_MAX_SPEED = 0.10f;
	/** The maximum angular speed of the character. */
	private static final float MAX_ANGULAR_SPEED = 0.1f;
	/** The maximum acceleration of the character. */
	private static final float MAX_ACCELERATION = 0.00175f;
	/** The maximum angular acceleration of the character. */
	private static final float MAX_ANGULAR_ACCELERATION = 0.0125f;

	/**
	 * How far into the future pursue predicts the targeted steering's motion.
	 */
	private static final float PREDICTION = 2.5f;

	/** How many frames pass between dropping each breadcrumb? */
	private static final int FREQUENCY = 30;

	/**
	 * Replaces the current character with the result of the function that
	 * accepts the current character and produces a new character.
	 */
	private Consumer<Function<Steering, Steering>> replacer;
	/**
	 * replaces the current path with the result of the function that accepts
	 * the current path and produces and returns a vew of a new path with
	 * respect to some replacer
	 */
	private Function<Function<Sequence<Target>, Sequence<Target>>, Sequence<Target>> path;
	/**
	 * A bifunction which takes the initial state that the character starts in
	 * and the goal state that the character ends in, produces an optional
	 * possibly empty sequence of targets that reaches the goal target from the
	 * initial target, and update pathReplacer and targetReplacer with
	 * respective new values.
	 */
	private BiFunction<Target, Target, Optional<Sequence<Target>>> pathFind;
	/** The quantized target that the monster starts at. */
	private Target quantize;
	/** The previous action taken on the environment. */
	private BehaviorTree.Action action = BehaviorTree.Action.WANDER_EVERYWHERE;
	/** A learned decision tree to mimic a behavior tree. */
	private Tree<List<BehaviorTree.Action>> tree;

	/** Creates models from the current environment's state. */
	private final Supplier<Model> supplier = () -> {
		// Use some intial conditions
		// Whether kill condition is satisfied
		boolean kill = quantize == quantize();

		// Whether pursue condition is satisfied
		boolean pursue = visible(quantize, quantize());

		// Whether sequence is completely satisfied
		Sequence<Target> sequence = path();
		while (sequence.rest().isPresent()) {
			sequence = sequence.next();
		}
		boolean satisfied = !kill && satisfied(sequence.first(), quantize.position());

		// Print out regression
		return new Model(kill, pursue, satisfied, action);
	};

	/**
	 * Resets target.
	 * 
	 * @return The initial monster used at the start of decision making.
	 */
	private Steering createMonster() {
		Vector[] positions = of(create(625, 350), create(500, 135), create(60, 45)).map(super::quantize)
				.map(Target::position).collect(toSet()).toArray(new Vector[] {});
		Vector position = positions[(int) (positions.length * nextFloat())];
		return new Steering(position, ZERO, ZERO, -PI / 2, 0, 0);
	}

	private Sequence<Target> path() {
		return path.apply(identity());
	}

	@Override
	public void kill() {
		super.kill();
		replacer.accept((monster) -> {
			monster = createMonster();

			Target first = quantize(monster.position());
			path.apply((path) -> conj(empty(path), first));

			return monster;
		});
		action = BehaviorTree.Action.KILL;
	}

	/**
	 * Finds a new path from the monster's current location to where the monster
	 * expects the character to move to soon. Seeks along the new path.
	 */
	public void moveToCharacter() {
		replacer.accept((monster) -> {
			Steering reference = monster;
			replacer().accept((character) -> {
				Vector predicted = pursue(reference, character, PREDICTION);
				pathFind.apply(quantize, quantize(predicted));
				return character;
			});

			Vector seek = seek(monster, path().first());
			return setAcceleration(monster, seek, monster.angularAcceleration());
		});
		action = BehaviorTree.Action.MOVE_TO_CHARACTER;
	}

	/**
	 * Follows the current path until completion. If there is only one target on
	 * path, arrives at target and keeps target as path until a new path is set.
	 */
	public void pathFollow() {
		replacer.accept((monster) -> {
			Steering reference = monster;
			path.apply((path) -> {
				return path.rest().isPresent() && satisfied(path.first(), reference.position()) ? path.next() : path;
			});

			Target target = path().first();
			Vector arbitration = !path().rest().isPresent() ? arrive(monster, target, target.satisfaction(),
					target.decceleration(), WANDER_MAX_SPEED, elapsed()) : seek(monster, target);

			return setAcceleration(monster, arbitration, monster.angularAcceleration());
		});
		action = BehaviorTree.Action.PATH_FOLLOW;
	}

	/**
	 * @param x
	 *            x-coordinate of the rectangular wander area
	 * @param y
	 *            y-coordinate of the rectangular wander area
	 * @param w
	 *            width of the rectangular wander area
	 * @param h
	 *            height of the rectangular wander area
	 * @return a sequenced behavior tree that first guarantess the existence of
	 *         a path to some point in the rectangular wander area and then path
	 *         follows on that path.
	 */
	public void wanderInto(float x, float y, float w, float h) {
		Vector goal = create(x + (w * nextFloat()), y + (h * nextFloat()));
		pathFind.apply(quantize, quantize(goal));
		replacer.accept((monster) -> {
			Steering reference = monster;
			path.apply((path) -> {
				return path.rest().isPresent() && satisfied(path.first(), reference.position()) ? path.next() : path;
			});

			Target target = path().first();
			Vector arbitration = !path().rest().isPresent() ? arrive(monster, target, target.satisfaction(),
					target.decceleration(), WANDER_MAX_SPEED, elapsed()) : seek(monster, target);

			return setAcceleration(monster, arbitration, monster.angularAcceleration());
		});
	}

	@Override
	public void settings() {
		super.settings();
		replacer = addCharacter(createMonster());
		path = paths(replacer);
		pathFind = pathFind(path);
		replacer.accept((monster) -> {
			quantize = quantize(monster.position());
			return monster;
		});

		try {
			tree = Models.train(supplier);
		} catch (IOException e) {
			System.err.println(e);
			exit();
		}
	}

	@Override
	public void draw() {
		// tree must go before draw so that elapsed is relative to LAST call to
		// draw and not current call
		List<BehaviorTree.Action> actions = tree.decide();
		BehaviorTree.Action action = actions.get((int) (nextFloat() * actions.size()));
		switch (action) {
		case KILL:
			kill();
			break;
		case MOVE_TO_CHARACTER:
			moveToCharacter();
			break;
		case PATH_FOLLOW:
			pathFollow();
			break;
		case WANDER_BATHROOM:
			wanderInto(510, 315, 290, 125);
			this.action = action;
			break;
		case WANDER_BEDROOM:
			wanderInto(350, 25, 270, 270);
			this.action = action;
			break;
		case WANDER_EVERYWHERE:
			wanderInto(0, 0, 800, 800);
			this.action = action;
			break;
		case WANDER_OUTSIDE:
			wanderInto(0, 0, 320, 220);
			this.action = action;
			break;
		}

		float elapsed = elapsed();
		super.draw();

		replacer.accept((monster) -> {
			if (frames() % FREQUENCY == 0) {
				dropBreadcrumb(monster.position());
			}

			float align = align(monster, ALIGN_ZERO, ALIGN_SATISFACTION, ALIGN_DECELERATION, MAX_ANGULAR_ACCELERATION,
					elapsed);

			monster = setAcceleration(monster, monster.acceleration(), align);
			monster = restrict(monster, width, height,
					visible(quantize, quantize()) ? SEEK_MAX_SPEED : WANDER_MAX_SPEED, MAX_ANGULAR_SPEED,
					MAX_ACCELERATION, MAX_ANGULAR_ACCELERATION);
			monster = update(monster, elapsed);

			quantize = quantize(monster.position());

			return monster;
		});
	}

}
