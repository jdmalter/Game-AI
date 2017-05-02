package finiteautomaton;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * A finite automaton can be defined by five components: a state set, an
 * alphabet, a transition function, an initial state, and a goal set.
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
 *            any alphabet
 */
public abstract class FiniteAutomaton<S, A> {

	/** A finite, non-empty set of states. */
	private final Set<S> states = new HashSet<S>();
	/** A finite, non-empty set of symbols. */
	private final Set<A> alphabet = new HashSet<A>();
	/** The state transition function. */
	private final BiFunction<S, A, Stream<S>> transition;
	/** An initial state and an element in states. */
	private final S initial;
	/** The set of final states and a possibly empty subset of states. */
	private final Set<S> goals = new HashSet<S>();

	/**
	 * @param states
	 *            A finite, non-empty set of states.
	 * @param alphabet
	 *            A finite, non-empty set of symbols.
	 * @param transition
	 *            The state transition function.
	 * @param initial
	 *            An initial state and an element in states.
	 * @param goals
	 *            The set of final states and a possibly empty subset of states.
	 * @throws NullPointerException
	 *             if transition is null
	 * @throws IllegalArgumentException
	 *             if states is empty, alphabet is empty, initial is not an
	 *             element in states, or goals is not a subset of states
	 */
	protected FiniteAutomaton(Set<? extends S> states, Set<? extends A> alphabet,
			BiFunction<S, A, Stream<S>> transition, S initial, Set<? extends S> goals) {
		this.states.addAll(states);
		this.alphabet.addAll(alphabet);
		this.transition = requireNonNull(transition);
		this.initial = initial;
		this.goals.addAll(goals);

		if (this.states.isEmpty()) {
			throw new IllegalArgumentException("states must not be empty");

		} else if (this.alphabet.isEmpty()) {
			throw new IllegalArgumentException("alphabet must not be empty");

		} else if (!this.states.contains(this.initial)) {
			throw new IllegalArgumentException("initial must be an element in states");

		} else if (!this.goals.stream().allMatch(this.states::contains)) {
			throw new IllegalArgumentException("goals must be subset of states");
		}
	}

	/**
	 * @return A finite, non-empty set of states.
	 */
	public final Stream<S> states() {
		return states.stream();
	}

	/**
	 * @return A finite, non-empty set of symbols.
	 */
	public final Stream<A> alphabet() {
		return alphabet.stream();
	}

	/**
	 * If states does not contain {@code t} or alphabet does not contain
	 * {@code u}, then an empty optional is returned. Calls the state transition
	 * function.
	 * 
	 * @param t
	 *            The current state and an element in states.
	 * @param u
	 *            The current symbol and an element in alphabet.
	 * @return A finite set of states that are elements of states.
	 */
	public final Optional<Stream<S>> transition(S t, A u) {
		return states.contains(t) && alphabet.contains(u) ? of(transition.apply(t, u).filter(states::contains))
				: empty();
	}

	/**
	 * @return An initial state and an element in states.
	 */
	public final S initial() {
		return initial;
	}

	/**
	 * @param s
	 *            The current state and an element in states.
	 * @return Whether a given state is an element of the set of final states.
	 */
	public final boolean goal(S s) {
		return goals.contains(s);
	}

}
