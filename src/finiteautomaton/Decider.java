package finiteautomaton;

import static java.util.function.Function.identity;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;

import java.util.stream.Stream;

/**
 * Decides whether a finite automata accepts a string.
 * 
 * @author Jacob Malter
 */
public final class Decider {

	/**
	 * Cannot be instantiated by users.
	 */
	private Decider() {

	}

	/**
	 * @param fa
	 *            A finite automaton can be defined by five components: a state
	 *            set, an alphabet, a transition function, an initial state, and
	 *            a goal set.
	 * @param string
	 *            A finite sequence of elements (possibly) from the finite
	 *            automaton's alphabet.
	 * @return Whether there exists sequence of states from the finite
	 *         automaton's states where the first state is the intial state, each
	 *         state is an element of the set of states returned from the
	 *         transition model given the previous state and symbol, and the
	 *         last state is an element of the finite automaton's goals.
	 */
	public static <S, A> boolean accept(FiniteAutomaton<S, A> fa, Stream<A> string) {
		/**
		 * reduce string into stream of states remaining after reduction on
		 * string
		 */
		return string.sequential().reduce(of(fa.initial()), (states, symbol) -> {

			/**
			 * map each previous state into a stream of following states and
			 * flatten into one stream
			 */
			return states.map((state) -> {
				return fa.transition(state, symbol).orElse(empty());
			}).flatMap(identity());

			/**
			 * concatenate streams into one stream and match any state to an
			 * element in goals
			 */
		}, Stream::concat).anyMatch(fa::goal);
	}

}
