package finiteautomaton;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * A non-deterministic finite automaton whose transition function defines a some
 * (possibly changing) output state(s) from states for some pairs of state in
 * states and symbol in alphabet.
 * 
 * @author Jacob Malter
 *
 * @param <S>
 *            any state
 * @param <A>
 *            any alphabet
 */
public class NondeterministicFiniteAutomaton<S, A> extends FiniteAutomaton<S, A> {

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
	public NondeterministicFiniteAutomaton(Set<? extends S> states, Set<? extends A> alphabet,
			BiFunction<S, A, Stream<S>> transition, S initial, Set<? extends S> goals) {
		super(states, alphabet, transition, initial, goals);
	}

}
