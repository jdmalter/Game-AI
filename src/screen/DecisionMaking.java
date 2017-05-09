package screen;

import static function.HigherOrder.compose;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.function.Function.identity;
import static search.Heuristics.euclidean;
import static sequence.Sequences.conj;
import static sequence.Sequences.empty;
import static steering.Matching.align;
import static steering.Matching.arrive;
import static steering.Matching.seek;
import static steering.Mutation.restrict;
import static steering.Mutation.setAcceleration;
import static steering.Mutation.update;
import static target.Predicate.satisfied;
import static utility.Comparators.ASCENDING;
import static vector.Arithmetic.subtract;
import static vector.Factory.ZERO;
import static vector.Factory.create;
import static vector.Property.magnitude;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import digraphproblem.DigraphProblem;
import domain.FloorGraph;
import queuesearch.PriorityQueueSearch;
import search.AStarSearch;
import sequence.Sequence;
import steering.Steering;
import target.Target;
import vector.Vector;

/**
 * A base class for decision making.
 * 
 * <p>
 * After creating decision making, I realized there is more abstractions to be
 * removed from Screen. To make each class simpler, I split Screen into Screen
 * and Movement. Screen handles frames, elapsed time, and background drawing.
 * Movement handles draws breadcrumbs and characters.
 * 
 * <p>
 * Moved instance fields below static fields because instance fields prefer to
 * use initialized static fields. Created {@link function.HigherOrder} to make
 * {@link #quantize(Vector)} and other comparators more compact.
 * 
 * <p>
 * Added {@link #pathFind(Target, Target)}, {@link #pathFollow(Sequence)},
 * {@link #pathFollowing()}, and {@link #quantize()} to make common operations
 * and finding the character's position easy to write. To follow a path a new
 * path with an goal target, call code equivalent to:
 * 
 * <p>
 * {@code pathFind(quantize(), goal).ifPresent(super::pathFollow);}.
 * 
 * <p>
 * Added type parameter to decision making so that subclasses can pass actions
 * from {@link #think()} to {@link #act(Object)}. Additionally, created
 * {@link #cycle()} to provide a default call for those methods.
 * 
 * <p>
 * Moved graph creation from this class to {@link IndoorEnvironment} to make
 * this class easy to navigate.
 * 
 * @param <A>
 *            any action
 * 
 * @author Jacob Malter
 */
public abstract class DecisionMaking<A> extends Movement {

	/** The radius of zero for angular targets. */
	private static final float ALIGN_ZERO = 0.0001f;
	/** The radius of satisfaction for angular targets. */
	private static final float ALIGN_SATISFACTION = PI / 360;
	/** The radius of deceleration for angular targets. */
	private static final float ALIGN_DECELERATION = PI / 2;

	/** The maximum speed of the character. */
	private static final float MAX_SPEED = 0.15f;
	/** The maximum angular speed of the character. */
	private static final float MAX_ANGULAR_SPEED = 0.1f;
	/** The maximum acceleration of the character. */
	private static final float MAX_ACCELERATION = 0.00175f;
	/** The maximum angular acceleration of the character. */
	private static final float MAX_ANGULAR_ACCELERATION = 0.0125f;

	/** How many frames pass between dropping each breadcrumb? */
	private static final int FREQUENCY = 30;

	/** A HashDigraph that calculates edge values by a weight function. */
	private final FloorGraph<Target, Float> graph;
	/** The path for the respective replacer to follow. */
	private final Map<Consumer<Function<Steering, Steering>>, Sequence<Target>> paths = new HashMap<Consumer<Function<Steering, Steering>>, Sequence<Target>>();

	/** The quantized target that the character starts at. */
	private Target quantize;

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
	 * @param w
	 *            The width of the display window in units of pixels.
	 * @param h
	 *            The height of the display window in units of pixels.
	 * @param d
	 *            The diameter of the drawn ellipses.
	 * @param graph
	 *            A HashDigraph that calculates edge values by a weight
	 *            function.
	 * @throws NullPointerException
	 *             if graph is null
	 * @throws IllegalArgumentException
	 *             if there are no targets in graph
	 */
	public DecisionMaking(int w, int h, float d, FloorGraph<Target, Float> graph) {
		super(w, h, d);
		this.graph = requireNonNull(graph);

		if (this.graph.get().count() == 0) {
			throw new IllegalArgumentException("there must exist some targets in graph");
		}
	}

	/**
	 * @return The quantized target that the character currently is located at.
	 */
	public Target quantize() {
		return quantize;
	}

	/**
	 * @param v
	 *            Some point in geometric space.
	 * @return The closest target in position to the given point of the targets
	 *         in the floor graph.
	 */
	public Target quantize(Vector v) {
		Function<Target, Float> distance = (target) -> magnitude(subtract(target.position(), v));
		return graph.get().min(compose(ASCENDING, distance)::apply).get();
	}

	/**
	 * @return Replaces the current character with the result of the function
	 *         that accepts the current character and produces a new character.
	 */
	public Consumer<Function<Steering, Steering>> replacer() {
		return replacer;
	}

	/**
	 * @param replacer
	 *            some character replacer function
	 * @return replaces the current path with the result of the function that
	 *         accepts the current path and produces and returns a view of a new
	 *         path with respect to some replacer
	 * @throws NullPointerException
	 *             replacer is null
	 * @throws IllegalArgumentException
	 *             paths does not contain replacer
	 */
	public Function<Function<Sequence<Target>, Sequence<Target>>, Sequence<Target>> paths(
			Consumer<Function<Steering, Steering>> replacer) {
		if (!paths.containsKey(requireNonNull(replacer))) {
			throw new IllegalArgumentException("paths does not contain replacer");
		}

		return (mapper) -> {
			Sequence<Target> path = paths.get(replacer);
			path = mapper.apply(path);
			paths.put(replacer, path);
			return path;
		};
	}

	/**
	 * @param initial
	 *            The initial state that the agent starts in.
	 * @param goal
	 *            The goal state that the agent ends in.
	 * @return An optional possibly empty sequence of targets that reaches the
	 *         goal target from the initial target.
	 */
	private Optional<Optional<Sequence<Target>>> pathFind(Target initial, Target goal) {
		// construct new A* search
		/**
		 * A priority queue search that stores explored targets moved into
		 * pathFind because it belongs with heurisitc and astar.
		 */
		PriorityQueueSearch<DigraphProblem<Target>, Target, Target> priorityQueueSearch = new PriorityQueueSearch<DigraphProblem<Target>, Target, Target>();
		Function<? super Target, Float> heurisitic = euclidean(goal.position()).compose(Target::position);
		AStarSearch<DigraphProblem<Target>, Target, Target> astar = new AStarSearch<DigraphProblem<Target>, Target, Target>(
				priorityQueueSearch, heurisitic);

		// search the problem with A*
		DigraphProblem<Target> problem = new DigraphProblem<Target>(graph, initial, goal);
		return astar.search(problem);
	}

	/**
	 * @param pathReplacer
	 *            replaces the current path with the result of the function that
	 *            accepts the current path and produces and returns a vew of a
	 *            new path with respect to some replacer
	 * @return A bifunction which takes the initial state that the character
	 *         starts in and the goal state that the character ends in, produces
	 *         an optional possibly empty sequence of targets that reaches the
	 *         goal target from the initial target, and update pathReplacer and
	 *         targetReplacer with respective new values.
	 * @throws NullPointerException
	 *             if pathReplacer or targetReplacer is null
	 */
	public BiFunction<Target, Target, Optional<Sequence<Target>>> pathFind(
			Function<Function<Sequence<Target>, Sequence<Target>>, Sequence<Target>> pathReplacer) {
		requireNonNull(pathReplacer);

		// if path find was successful, replace it
		return (initial, goal) -> pathFind(initial, goal).map((list) -> {
			return pathReplacer.apply((path) -> list.orElse(conj(empty(path), initial)));
		});
	}

	/**
	 * @param v
	 *            The tail of the edge.
	 * @param u
	 *            The head of the edge.
	 * @return Whether the floor graph contains the given edge.
	 */
	public boolean visible(Target v, Target u) {
		return graph.contains(v, u);
	}

	/**
	 * @return a new character
	 */
	public Steering createCharacter() {
		Vector position = quantize(create(width / 2, height)).position();
		return new Steering(position, ZERO, ZERO, -PI / 2, 0, 0);
	}

	@Override
	public Consumer<Function<Steering, Steering>> addCharacter(Steering s) {
		Consumer<Function<Steering, Steering>> key = super.addCharacter(s);
		paths.put(key, conj(empty(empty()), quantize(s.position())));
		return key;
	}

	@Override
	public void settings() {
		super.settings();
		replacer = addCharacter(createCharacter());
		path = paths(replacer);
	}

	@Override
	public void draw() {
		float elapsed = elapsed();
		super.draw();

		replacer.accept((character) -> {
			if (frames() % FREQUENCY == 0) {
				dropBreadcrumb(character.position());
			}

			Steering reference = character;
			path.apply((path) -> {
				return path.rest().filter((nonempty) -> {
					return satisfied(path.first(), reference.position());
				}).orElse(path);
			});

			Target target = path.apply(identity()).first();
			Vector arbitration = path.apply(identity()).rest().map((nonempty) -> {
				return seek(reference, target);
			}).orElse(arrive(character, target, target.satisfaction(), target.decceleration(), MAX_SPEED, elapsed));
			float align = align(character, ALIGN_ZERO, ALIGN_SATISFACTION, ALIGN_DECELERATION, MAX_ANGULAR_ACCELERATION,
					elapsed);

			character = setAcceleration(character, arbitration, align);
			character = restrict(character, width, height, MAX_SPEED, MAX_ANGULAR_SPEED, MAX_ACCELERATION,
					MAX_ANGULAR_ACCELERATION);
			character = update(character, elapsed);

			quantize = quantize(character.position());

			return character;
		});
	}

	/**
	 * This implementation is equivalent to:
	 * 
	 * <p>
	 * {@code sense();
	 * act(think());}
	 * 
	 * <p>
	 * This method is inspired by the 10th slide in this <a href=
	 * "https://www.cs.utexas.edu/~fussell/courses/cs378/lectures/cs378-13.pdf">lecture</a>.
	 * 
	 * <p>
	 * Once again, this method was added to remove duplicate code and provide a
	 * default cycle.
	 */
	public void cycle() {
		sense();
		act(think());
	}

	/**
	 * Gathers percepts and updates drives and senses.
	 * 
	 * <p>
	 * This method is inspired by the 10th slide in this <a href=
	 * "https://www.cs.utexas.edu/~fussell/courses/cs378/lectures/cs378-13.pdf">lecture</a>.
	 */
	public abstract void sense();

	/**
	 * Decides which action to take based on senses.
	 * 
	 * <p>
	 * This method is inspired by the 10th slide in this <a href=
	 * "https://www.cs.utexas.edu/~fussell/courses/cs378/lectures/cs378-13.pdf">lecture</a>.
	 * 
	 * @return An affect on the indoor environment.
	 */
	public abstract A think();

	/**
	 * Executes an action.
	 * 
	 * <p>
	 * This method is inspired by the 10th slide in this <a href=
	 * "https://www.cs.utexas.edu/~fussell/courses/cs378/lectures/cs378-13.pdf">lecture</a>.
	 * 
	 * @param action
	 *            An affect on the indoor environment.
	 */
	public abstract void act(A action);

}
