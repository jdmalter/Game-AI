package screendecisionmaking;

import static decisiontree.Tree.composeBinary;
import static decisiontree.Tree.leaf;
import static drawing.Drive.bind;
import static function.HigherOrder.andThen;
import static function.HigherOrder.compose;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static sequence.Sequences.conj;
import static sequence.Sequences.empty;
import static sequence.Sequences.seq;
import static sequence.Sequences.stream;
import static steering.Mutation.restrict;
import static steering.Mutation.update;
import static utility.Comparators.ASCENDING;
import static utility.Random.nextFloat;
import static vector.Arithmetic.subtract;
import static vector.Factory.create;
import static vector.Property.magnitude;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import decisiontree.Tree;
import finiteautomaton.FiniteAutomaton;
import finiteautomaton.NondeterministicFiniteAutomaton;
import function.TriFunction;
import screen.IndoorEnvironment;
import sequence.Sequence;
import target.Target;

/**
 * Runs the decision tree algorithm.
 * 
 * <p>
 * After {@code DecisionMaking} added {@link #sense()}, {@link #think()},
 * {@link #act(Target)}, and {@link #quantize()} path finding behaviors in this
 * class were appropriately split among those methods.
 * 
 * @author Jacob Malter
 */
public class DecisionTree extends IndoorEnvironment<DecisionTree.Action> {

	/**
	 * States for the character to take.
	 * 
	 * @author Jacob Malter
	 */
	public enum State {
		SLEEPING, AQUIRING_FOOD, EATING, USING_BATHROOM, WANDERING, DEAD;
	}

	/**
	 * Affects on the indoor environment.
	 * 
	 * @author Jacob Malter
	 */
	public enum Action {
		SLEEP, GET_FOOD, EAT, BATHROOM, WANDER, DIE;
	}

	/** The argb color of green used to set the color of the drive bar. */
	private static final int DRIVE_COLOR = 0xFF00FF00;
	/** */
	private static final float DRIVE_WIDTH = 65;
	/** */
	private static final float DRIVE_HEIGHT = 2.5f;

	/** The minimum value for a drive. */
	private static final float MIN_DRIVE = 0.0f;
	/** The maximum value for a drive. */
	private static final float MAX_DRIVE = 1.0f;

	/** How much sleep should increase by per millisecond. */
	private static final float MILLISECONDS_TO_MAX_SLEEP = 50000;
	/** How much sleep should reduce by per millisecond. */
	private static final float MILLISECONDS_TO_MIN_SLEEP = 10000;

	/** How much hunger should increase by per millisecond. */
	private static final float MILLISECONDS_TO_MAX_HUNGER = 9000;
	/** How much hunger should reduce by per millisecond. */
	private static final float MILLISECONDS_TO_MIN_HUNGER = 3000;

	/** How much bathroom should increase by per millisecond. */
	private static final float MILLISECONDS_TO_MAX_BATHROOM = 60000;
	/** How much bathroom should reduce by per millisecond. */
	private static final float MILLISECONDS_TO_MIN_BATHROOM = 2000;

	/** How much bathroom should change by for a unit change in hunger. */
	private static final float HUNGER_BATHROOM_RATIO = 0.25f;
	/** What percent reduction in hunger and bathroom while sleeping. */
	private static final float SLEEPING_INTERACTION_RATIO = 0.5f;
	/** What percent reduction in bathroom while eating. */
	private static final float EATING_INTERACTION_RATIO = 0.25f;
	/** What percent reduction in hunger while using bathroom. */
	private static final float USING_BATHROOM_INTERACTION_RATIO = 0.5f;

	/** The maximum speed of the character. */
	private static final float WANDER_MAX_SPEED = 0.08f;
	/** The maximum angular speed of the character. */
	private static final float WANDER_MAX_ANGULAR_SPEED = 0.075f;
	/** The maximum acceleration of the character. */
	private static final float WANDER_MAX_ACCELERATION = 0.001f;
	/** The maximum angular acceleration of the character. */
	private static final float WANDER_MAX_ANGULAR_ACCELERATION = 0.008f;

	/**
	 * A new draw function that has its pa, c, w, and h set to the provided pa,
	 * c, w, and h.
	 */
	private final TriFunction<String, Float, Float, Consumer<Float>> drawDrive;
	/** A new draw function that has all inputs set except sleep. */
	private final Consumer<Float> drawSleep;
	/** A new draw function that has all inputs set except hunger. */
	private final Consumer<Float> drawHunger;
	/** A new draw function that has all inputs set except bathroom. */
	private final Consumer<Float> drawBathroom;

	/**
	 * Added fsm to eliminate boolean flags and enable easier addition of more
	 * states and transitions.
	 */

	/**
	 * A finite state machine can be defined by five components: a state set, an
	 * alphabet, a transition function, an initial state, and a goal set.
	 */
	private final FiniteAutomaton<State, Action> finiteStateMachine = nfa();

	/**
	 * replaces the current path with the result of the function that accepts
	 * the current path and produces and returns a vew of a new path with
	 * respect to some replacer
	 */
	private Function<Function<Sequence<Target>, Sequence<Target>>, Sequence<Target>> path;

	/** */
	private BiFunction<Target, Target, Optional<Sequence<Target>>> pathFind;

	/** A list of quantized positions of beds for sleeping. */
	private Sequence<Target> beds;
	/** A list of quantized positions of fridge for collecting food. */
	private Sequence<Target> fridge;
	/** A list of quantized positions of chairs for eating. */
	private Sequence<Target> chairs;
	/** A list of quantized positions of toilets for using bathroom. */
	private Sequence<Target> toilets;

	/**
	 * below, some data are fields because they is external knowledge that
	 * frequently changes when sensory information is updated
	 */

	/** The current state of the character. */
	private State state = finiteStateMachine.initial();

	/** A drive to sleep where larger numbers have more influence on actions. */
	private float sleep = MIN_DRIVE;
	/** A drive to eat where larger numbers have more influence on actions. */
	private float hunger = MAX_DRIVE;
	/**
	 * A drive to use the bathroom where larger numbers have more influence on
	 * actions.
	 */
	private float bathroom = MIN_DRIVE;

	/**
	 * decision trees
	 */

	/** A decision tree when sleep is the top priority while not sleeping. */
	private final Tree<Action> isNotSleeping = composeBinary(isMax(() -> sleep), leaf(Action.SLEEP),
			leaf(Action.WANDER));
	/** A decision tree when hunger is the top priority while not eating. */
	private final Tree<Action> isNotEating = composeBinary(isMax(() -> hunger),
			composeBinary(() -> state == State.EATING, leaf(Action.EAT), leaf(Action.GET_FOOD)), leaf(Action.WANDER));
	/**
	 * A decision tree when bathroom is the top priority while not using
	 * bathroom.
	 */
	private final Tree<Action> isNotUsingBathroom = composeBinary(isMax(() -> bathroom), leaf(Action.BATHROOM),
			leaf(Action.WANDER));
	/** Prioritizes bathroom, hunger, and sleep trees. */
	private final Tree<Action> otherwise = composeBinary(() -> bathroom >= hunger,
			composeBinary(() -> bathroom >= sleep, isNotUsingBathroom, isNotSleeping),
			composeBinary(() -> hunger >= sleep, isNotEating, isNotSleeping));

	/** A decision tree while sleeping. */
	private final Tree<Action> isSleeping = composeBinary(isMin(() -> sleep), leaf(Action.WANDER), leaf(Action.SLEEP));
	/** A decision tree while eating. */
	private final Tree<Action> isEating = composeBinary(isMin(() -> hunger), leaf(Action.WANDER), leaf(Action.EAT));
	/** A decision tree while using bathroom. */
	private final Tree<Action> isUsingBathroom = composeBinary(isMin(() -> bathroom), leaf(Action.WANDER),
			leaf(Action.BATHROOM));
	/**
	 * Checks drive flags to short circut into respective decision tree or
	 * delegates to otherwise.
	 */
	private final Tree<Action> tree = composeBinary(() -> state == State.SLEEPING, isSleeping,
			composeBinary(() -> state == State.USING_BATHROOM, isUsingBathroom,
					composeBinary(() -> state == State.EATING, isEating, otherwise)));

	/**
	 * isMax and isMin remove duplicate code to check drives
	 */

	/**
	 * @param supplier
	 *            the supplier to get before check for max drive is applied
	 * @return a composed supplier that first gets supplier and then applies the
	 *         check for max drive
	 */
	private static Supplier<Boolean> isMax(Supplier<Float> supplier) {
		return andThen(supplier, (drive) -> drive == MAX_DRIVE);
	}

	/**
	 * @param supplier
	 *            the supplier to get before check for min drive is applied
	 * @return a composed supplier that first gets supplier and then applies the
	 *         check for min drive
	 */
	private static Supplier<Boolean> isMin(Supplier<Float> supplier) {
		return andThen(supplier, (drive) -> drive == MIN_DRIVE);
	}

	/**
	 * Binds this to a PApplet drawer.
	 */
	public DecisionTree() {
		drawDrive = bind(this, DRIVE_COLOR, DRIVE_WIDTH, DRIVE_HEIGHT);
		drawSleep = drawDrive.apply("Sleep", 128f, 124f);
		drawHunger = drawDrive.apply("Hunger", 128f, 78.5f);
		drawBathroom = drawDrive.apply("Bathroom", 128f, 101f);

		beds = seq(of(quantize(create(750, 100)), quantize(create(750, 600)), quantize(create(750, 760)))).get();
		fridge = seq(of(quantize(create(75, 675)))).get();
		chairs = seq(of(quantize(create(85, 400)), quantize(create(165, 400)), quantize(create(200, 450)),
				quantize(create(165, 500)), quantize(create(85, 500)), quantize(create(50, 450)))).get();
		toilets = seq(of(quantize(create(625, 375)), quantize(create(590, 505)))).get();
	}

	/**
	 * @return A new nondeterminisitc finite automaton that describes the
	 *         character's behavior.
	 */
	private NondeterministicFiniteAutomaton<State, Action> nfa() {
		Set<State> states = new HashSet<State>();
		states.addAll(asList(State.values()));

		Set<Action> alphabet = new HashSet<Action>();
		alphabet.addAll(asList(Action.values()));

		BiFunction<State, Action, Stream<State>> transition = (state, action) -> {
			if (action == Action.WANDER) {
				return of(State.WANDERING);

			} else if (action == Action.DIE) {
				return of(State.DEAD);

			} else if (state == State.SLEEPING && action == Action.SLEEP) {
				return of(state);

			} else if (state == State.AQUIRING_FOOD) {
				if (at(fridge)) {
					return of(State.EATING);

				} else if (action == Action.GET_FOOD) {
					return of(state);

				} else {
					return empty();
				}

			} else if (state == State.EATING && action == Action.EAT) {
				return of(state);

			} else if (state == State.USING_BATHROOM && action == Action.BATHROOM) {
				return of(state);

			} else if (state == State.WANDERING) {
				if (action == Action.SLEEP) {
					return of(State.SLEEPING);

				} else if (action == Action.GET_FOOD) {
					return of(State.AQUIRING_FOOD);

				} else if (action == Action.BATHROOM) {
					return of(State.USING_BATHROOM);

				} else {
					return empty();
				}

			} else if (state == State.DEAD) {
				return of(finiteStateMachine.initial());

			} else {
				return empty();
			}
		};

		// start dead and become alive
		State initial = State.WANDERING;

		// all states are final states
		Set<State> goals = states;

		return new NondeterministicFiniteAutomaton<State, Action>(states, alphabet, transition, initial, goals);
	}

	/**
	 * Added helper methods at, min, and pathFind to reduce duplicated code.
	 */

	/**
	 * @param expected
	 *            Where the quantized target should be
	 * @return Whether the provided target and the expected target are equal
	 */
	private Predicate<Target> at(Target expected) {
		return (actual) -> Objects.equals(expected, actual);
	}

	/**
	 * new at method removes duplicate code for state transition, pathfinding,
	 * and drive updates
	 */

	/**
	 * @param seq
	 *            A provided sequence.
	 * @return Whether quantized target that the character currently is located
	 *         at matches any target in a stream over the targets in the
	 *         provided sequence.
	 */
	private boolean at(Sequence<Target> seq) {
		return stream(seq).anyMatch(at(quantize()));
	}

	/**
	 * @param target
	 *            A quantized target.
	 * @return The distance between the target's position and the initial's
	 *         position.
	 */
	private float distance(Target target) {
		return magnitude(subtract(target.position(), quantize().position()));
	}

	/**
	 * @param list
	 *            A list of targets to find the closest target.
	 * @return A target whose position distance to the initial position is
	 *         minimized.
	 */
	private Target min(Sequence<Target> list) {
		return stream(list).min(compose(ASCENDING, this::distance)::apply).get();
	}

	/**
	 * @return Whether there is a non-empty path.
	 */
	private boolean pathFollowing() {
		return path.apply(identity()).rest().isPresent();
	}

	/**
	 * goal removes duplciate code in pathfinding in act
	 */

	/**
	 * This implementation traverses the entire length of the current path.
	 * 
	 * @return The last target on the path.
	 */
	private Target goal() {
		// track items in the path
		Sequence<Target> current = path.apply(identity());

		// advance to the end of the path
		while (current.rest().isPresent()) {
			current = current.next();
		}

		// return the first item at the end of the path
		return current.first();
	}

	/**
	 * Sets the character's location and drives to their initial values. Clears
	 * the path.
	 */
	public void kill() {
		replacer().accept((character) -> {
			character = createCharacter();

			Target position = quantize(character.position());
			path.apply((path) -> conj(empty(path), position));

			transition(Action.DIE);

			sleep = MIN_DRIVE;
			hunger = MAX_DRIVE;
			bathroom = MIN_DRIVE;

			return character;
		});
	}

	/**
	 * If the transition fails, state remains at the previous state.
	 * 
	 * @param action
	 *            The current action and an element in Action.
	 * @return Whether the current state and provided action successfully
	 *         transitioned to any other state.
	 */
	private boolean transition(Action action) {
		Optional<State> result = finiteStateMachine.transition(state, action).orElse(empty()).findAny();
		state = result.orElse(state);
		return result.isPresent();
	}

	@Override
	public void settings() {
		super.settings();
		path = paths(replacer());
		pathFind = pathFind(path);
	}

	@Override
	public void draw() {
		// cycle must occur before draw so that elapsed time is accurate!!!
		cycle();

		super.draw();
		drawSleep.accept(sleep);
		drawHunger.accept(hunger);
		drawBathroom.accept(bathroom);

		if (state == State.WANDERING) {
			replacer().accept((character) -> {
				character = restrict(character, width, height, WANDER_MAX_SPEED, WANDER_MAX_ANGULAR_SPEED,
						WANDER_MAX_ACCELERATION, WANDER_MAX_ANGULAR_ACCELERATION);
				character = update(character, elapsed());

				return character;
			});
		}
	}

	@Override
	public void sense() {
		float elapsed = elapsed();

		if (!pathFollowing()) {
			if (state == State.SLEEPING && at(beds)) {
				sleep -= elapsed / MILLISECONDS_TO_MIN_SLEEP;
				hunger -= SLEEPING_INTERACTION_RATIO * elapsed / MILLISECONDS_TO_MAX_HUNGER;
				bathroom -= SLEEPING_INTERACTION_RATIO * elapsed / MILLISECONDS_TO_MAX_BATHROOM;
			} else {
				sleep += elapsed / MILLISECONDS_TO_MAX_SLEEP;
			}

			if (state == State.EATING && at(chairs)) {
				sleep -= EATING_INTERACTION_RATIO * elapsed / MILLISECONDS_TO_MAX_SLEEP;
				hunger -= elapsed / MILLISECONDS_TO_MIN_HUNGER;
				bathroom += HUNGER_BATHROOM_RATIO * elapsed / MILLISECONDS_TO_MIN_HUNGER;
			} else {
				hunger += elapsed / MILLISECONDS_TO_MAX_HUNGER;
			}

			if (state == State.USING_BATHROOM && at(toilets)) {
				hunger -= USING_BATHROOM_INTERACTION_RATIO * elapsed / MILLISECONDS_TO_MAX_HUNGER;
				bathroom -= elapsed / MILLISECONDS_TO_MIN_BATHROOM;
			} else {
				bathroom += elapsed / MILLISECONDS_TO_MAX_BATHROOM;
			}

		} else {
			sleep += elapsed / MILLISECONDS_TO_MAX_SLEEP;
			hunger += elapsed / MILLISECONDS_TO_MAX_HUNGER;
			bathroom += elapsed / MILLISECONDS_TO_MAX_BATHROOM;
		}

		/**
		 * Removed limit function because it was only used right here
		 */

		// limit drives from 0 to 1
		sleep = min(max(sleep, MIN_DRIVE), MAX_DRIVE);
		hunger = min(max(hunger, MIN_DRIVE), MAX_DRIVE);
		bathroom = min(max(bathroom, MIN_DRIVE), MAX_DRIVE);
	}

	@Override
	public Action think() {
		// all the work is done within the decision tree
		return tree.decide();
	}

	@Override
	public void act(Action action) {
		// must call transition to advance the current state of the character
		transition(action);

		// test whether the goal of the path equals a provided target
		Predicate<Target> goal = at(goal());

		if (state == State.SLEEPING && !stream(beds).anyMatch(goal)) {
			pathFind.apply(quantize(), min(beds));

		} else if (state == State.AQUIRING_FOOD && !stream(fridge).anyMatch(goal)) {
			pathFind.apply(quantize(), min(fridge));

		} else if (state == State.EATING && !stream(chairs).anyMatch(goal)) {
			pathFind.apply(quantize(), min(chairs));

		} else if (state == State.USING_BATHROOM && !stream(toilets).anyMatch(goal)) {
			pathFind.apply(quantize(), min(toilets));

		} else if (state == State.WANDERING && !pathFollowing()) {
			// quantize a random point in geometric space within space
			pathFind.apply(quantize(), quantize(create(width * nextFloat(), height * nextFloat())));
		}
	}

}
