package problem;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import function.TriFunction;

/**
 * A problem can be defined by five components: initial state, actions,
 * transition model, goal test, and path cost.
 * 
 * <p>
 * After deliberation, converted return type of getters from functions to those
 * function's return types to cut out extra steps in client code. With
 * function's return types, client code avoids using actions.apply or
 * goalTest.test instead of passing parameters to actions or goalTest. Since the
 * :: operator allows instance methods to be used as functions like before this
 * change, no functionality has been lost because of this change.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * @author Jacob Malter
 *
 * @param <S>
 *            any state
 * @param <A>
 *            any action
 */
public abstract class Problem<S, A> {

	/** The initial state that the agent starts in. */
	private final S initial;
	/** Each action in actions that is applicable for a given state. */
	private final Function<? super S, Set<? extends A>> actions;
	/** Returns state reachable from a given state and action. */
	private final BiFunction<? super S, ? super A, ? extends S> result;
	/** Returns whether a given state is a goal state. */
	private final Predicate<? super S> goalTest;
	/** Returns the cost to take an action between two states. */
	private final TriFunction<? super S, ? super A, ? super S, Float> stepCost;

	/**
	 * @param initial
	 *            The initial state that the agent starts in.
	 * @param actions
	 *            Each action in actions that is applicable for a given state.
	 * @param result
	 *            Returns state reachable from a given state and action.
	 * @param goalTest
	 *            Returns whether a given state is a goal state.
	 * @param stepCost
	 *            Returns the cost to take an action between two states.
	 * @throws NullPointerException
	 *             if actions, result, goalTest, or stepCost is null
	 */
	public Problem(S initial, Function<? super S, Set<? extends A>> actions,
			BiFunction<? super S, ? super A, ? extends S> result, Predicate<? super S> goalTest,
			TriFunction<? super S, ? super A, ? super S, Float> stepCost) {
		this.initial = initial;
		this.actions = requireNonNull(actions);
		this.result = requireNonNull(result);
		this.goalTest = requireNonNull(goalTest);
		this.stepCost = requireNonNull(stepCost);
	}

	/**
	 * Gets the respective constructor parameter.
	 * 
	 * @return The initial state that the agent starts in.
	 */
	public final S initial() {
		return initial;
	}

	/**
	 * Applies the respective constructor function to the given argument.
	 * 
	 * @param t
	 *            Some state.
	 * @return Each action in actions that is applicable for a given state.
	 */
	public final Set<? extends A> actions(S t) {
		return actions.apply(t);
	}

	/**
	 * Applies the respective constructor function to the given arguments.
	 * 
	 * @param t
	 *            Some state with available actions.
	 * @param u
	 *            Some action available in the given state.
	 * @return State reachable from a given state and action.
	 */
	public final S result(S t, A u) {
		return result.apply(t, u);
	}

	/**
	 * Evaluates the respective constructor predicate to the given argument.
	 * 
	 * @param t
	 *            Some state.
	 * @return Whether a given state is a goal state.
	 */
	public final boolean goalTest(S t) {
		return goalTest.test(t);
	}

	/**
	 * Applies the respective constructor function to the given arguments.
	 * 
	 * @param t
	 *            State where action is taken.
	 * @param u
	 *            Action taken to arrive in resulting state.
	 * @param v
	 *            Resulting state from taking the given action.
	 * @return The cost to take an action between two states.
	 */
	public final float stepCost(S t, A u, S v) {
		return stepCost.apply(t, u, v);
	}

}
