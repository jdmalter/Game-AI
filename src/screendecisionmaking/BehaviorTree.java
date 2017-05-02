package screendecisionmaking;

import static behaviortree.Composite.randomSelect;
import static behaviortree.Composite.select;
import static behaviortree.Composite.sequence;
import static behaviortree.Decorator.randomize;
import static java.util.Objects.requireNonNull;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static behaviortree.Decorator.infiniteSelect;
import behaviortree.Tree;
import function.Procedure;
import learning.Model;
import sequence.Sequence;
import steering.Steering;
import target.Target;
import vector.Vector;

/**
 * Runs the behavior tree algorithm.
 * 
 * @author Jacob Malter
 */
public class BehaviorTree extends DecisionTree {

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
	 * the likelihood that the wander all is executed during each call
	 */
	private static final float PROBABILITY_WANDER_EVERYWHERE = 0.05f;
	/**
	 * the likelihood that the wander into outside is executed during each call
	 */
	private static final float PROBABILITY_WANDER_OUTSIDE = 0.03f;
	/**
	 * the likelihood that the wander into bedroom is executed during each call
	 */
	private static final float PROBABILITY_WANDER_BEDROOM = 0.03f;
	/**
	 * the likelihood that the wander into bathroom is executed during each call
	 */
	private static final float PROBABILITY_WANDER_BATHROOM = 0.03f;

	/**
	 * How far into the future pursue predicts the targeted steering's motion.
	 */
	private static final float PREDICTION = 2.5f;

	/** How many frames pass between dropping each breadcrumb? */
	private static final int FREQUENCY = 30;

	/** A set of consumers of models describing a regression. */
	private final Set<Consumer<Model>> listeners = new HashSet<Consumer<Model>>();

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
	private Action action;

	/**
	 * A helper class that records the called action in the behavior tree.
	 * 
	 * @author Jacob Malter
	 */
	public enum Action {
		/** Kill action. */
		KILL('K'),

		/** Pursue action. */
		MOVE_TO_CHARACTER('M'),

		/** Path following action. */
		PATH_FOLLOW('P'),

		/** Wandering actions. */
		WANDER_OUTSIDE('O'), WANDER_BATHROOM('B'), WANDER_BEDROOM('D'), WANDER_EVERYWHERE('E');

		/** the name of this enum constant */
		private final Character name;

		/**
		 * @param name
		 *            the name of this enum constant
		 * @throws NullPointerException
		 *             if name is null
		 */
		private Action(Character name) {
			this.name = requireNonNull(name);
		}

		@Override
		public String toString() {
			return name.toString();
		}

	}

	/**
	 * A helper class that outlines rectangular region to wander throughout.
	 * 
	 * @author Jacob Malter
	 */
	private class Region implements Tree {

		/** x-coordinate of the rectangular wander area */
		private final float x;
		/** y-coordinate of the rectangular wander area */
		private final float y;
		/** width of the rectangular wander area */
		private final float w;
		/** height of the rectangular wander area */
		private final float h;
		/** The action that corresponds to wandering into this region. */
		private final Action action;

		/**
		 * 
		 * @param x
		 *            x-coordinate of the rectangular wander area
		 * @param y
		 *            y-coordinate of the rectangular wander area
		 * @param w
		 *            width of the rectangular wander area
		 * @param h
		 *            height of the rectangular wander area
		 * @param action
		 *            The action that corresponds to wandering into this region.
		 * @throws NullPointerException
		 *             if action is null
		 */
		private Region(float x, float y, float w, float h, Action action) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.action = requireNonNull(action);
		}

		@Override
		public boolean behave() {
			wanderInto(x, y, w, h);
			BehaviorTree.this.action = this.action;
			return true;
		}

	}

	/**
	 * behavior trees
	 */

	/** A sequenced behavior tree for the monster to pursue the character. */
	private final Tree kill = sequence(() -> quantize == quantize(), decorate(this::kill));

	/** A sequenced behavior tree for the monster to seek the character. */
	private final Tree pursue = sequence(() -> visible(quantize, quantize()), decorate(this::moveToCharacter));

	/** Whether there is an unsatisfied target on the path. */
	private final Tree isNotPathFollowing = sequence(() -> !path().rest().isPresent(),
			() -> satisfied(path().first(), quantize.position()));

	/**
	 * Behavior trees to wander around the outside.
	 */
	private final Region outside = new Region(0, 0, 320, 220, Action.WANDER_OUTSIDE);
	private final Tree randomOutside = randomize(outside, PROBABILITY_WANDER_OUTSIDE);

	/**
	 * Behavior trees to wander around the bedroom.
	 */
	private final Region bedroom = new Region(350, 25, 270, 270, Action.WANDER_BEDROOM);
	private final Tree randomBedroom = randomize(bedroom, PROBABILITY_WANDER_BEDROOM);

	/**
	 * Behavior trees to wander around the bathoom.
	 */
	private final Region bathroom = new Region(510, 315, 290, 125, Action.WANDER_BATHROOM);
	private final Tree randonBathroom = randomize(bathroom, PROBABILITY_WANDER_BATHROOM);

	/** Behavior trees to wander around everywhere. */
	private final Region everywhere = new Region(0, 0, 800, 800, Action.WANDER_EVERYWHERE);
	private final Tree randomEverywhere = randomize(everywhere, PROBABILITY_WANDER_EVERYWHERE);

	/** A select tree to find the first place to wander. */
	private final Tree wander = sequence(isNotPathFollowing,
			infiniteSelect(randomSelect(randomOutside, randomBedroom, randonBathroom, randomEverywhere)));

	/** Follows the path by arriving/seeking at the next target on the path. */
	private final Tree pathfollow = decorate(this::pathFollow);

	/**
	 * A selected tree between pursue, seek, wander, and wander everywhere.
	 */
	private final Tree tree = select(kill, pursue, wander, pathfollow);

	/**
	 * @param p
	 *            Some procedure.
	 * @return A behavior tree that performs the procedure, then returns true.
	 * @throws NullPointerException
	 *             if p is null
	 */
	private Tree decorate(Procedure p) {
		requireNonNull(p);

		return () -> {
			p.call();
			return true;
		};
	}

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
		action = Action.KILL;
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
		action = Action.MOVE_TO_CHARACTER;
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
		action = Action.PATH_FOLLOW;
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

	/**
	 * Records some inputs and output for the regressions
	 * 
	 * @param procedure
	 *            Behavior that sets action
	 */
	private void notify(Procedure procedure) {
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
		// adding !kill increases the amount of information kill provides
		boolean satisfied = !kill && satisfied(sequence.first(), quantize.position());

		// Call behavior that sets action
		procedure.call();

		// Print out regression
		Model output = new Model(kill, pursue, satisfied, action);
		listeners.forEach((consumer) -> consumer.accept(output));
	}

	/**
	 * Consumers are called with output from the regression.
	 * 
	 * @param consumer
	 *            consumer of string to be added to this set
	 * @return true if this set did not already contain the specified consumer
	 *         of string
	 * @throws NullPointerException
	 *             if consumer is null
	 */
	public boolean listen(Consumer<Model> consumer) {
		return listeners.add(requireNonNull(consumer));
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
	}

	@Override
	public void draw() {
		// tree must go before draw so that elapsed is relative to LAST call to
		// draw and not current call
		notify(tree::behave);

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
