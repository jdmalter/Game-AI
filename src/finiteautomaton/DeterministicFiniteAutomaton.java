package finiteautomaton;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import utility.Pair;

/**
 * A deterministic finite automaton whose transition function defines a constant
 * output state from states for every pair of state in states and symbol in
 * alphabet.
 * 
 * @author Jacob Malter
 *
 * @param <S>
 *            any state
 * @param <A>
 *            any alphabet
 */
public class DeterministicFiniteAutomaton<S, A> extends FiniteAutomaton<S, A> {

	/** A detailed message given to {@code IllegalArgumentException}. */
	private static final String ILLEGAL_MAP = "transition fails to define some pairs of state and symbol\n";

	/**
	 * @param map
	 *            A table definition of the state transition function.
	 * @return All states from map.
	 */
	private static <S, A> Set<? extends S> states(Map<S, Map<A, Set<S>>> map) {
		return concat(map.keySet().stream(), map.values().stream().map(Map::entrySet).map(Set::stream)
				.flatMap(identity()).map(Entry::getValue).map(Set::stream).flatMap(identity())).collect(toSet());
	}

	/**
	 * @param map
	 *            A table definition of the state transition function.
	 * @return All symbols from map.
	 */
	private static <S, A> Set<? extends A> alphabet(Map<S, Map<A, Set<S>>> map) {
		return map.values().stream().map(Map::entrySet).map(Set::stream).flatMap(identity()).map(Entry::getKey)
				.collect(toSet());
	}

	/**
	 * @param map
	 *            A table definition of the state transition function.
	 * @return The state transition function.
	 */
	private static <S, A> BiFunction<S, A, Stream<S>> transition(Map<S, Map<A, Set<S>>> map) {
		return (s, a) -> {
			return map.containsKey(s) && map.get(s).containsKey(a) ? map.get(s).get(a).stream() : empty();
		};
	}

	/**
	 * Copies entries from given map to a new map instance to prevent
	 * modification after DeterministicFiniteAutomaton is instantiated.
	 * 
	 * @param map
	 *            A table definition of the state transition function.
	 * @return The same table definition of the state transition function.
	 */
	private static <S, A> Map<S, Map<A, Set<S>>> copy(Map<S, Map<A, Set<S>>> map) {
		// prevent map from being externally modified
		return map.entrySet().stream().map((entryS) -> {

			// prevent map from being externally modified
			Map<A, Set<S>> valueS = entryS.getValue().entrySet().stream().map((entryA) -> {

				// prevent set from being externally modified
				Set<S> valueA = entryA.getValue().stream().collect(toSet());
				return new Pair<A, Set<S>>(entryA.getKey(), valueA);

				// collect into map
			}).collect(toMap(Pair::a, Pair::b));
			return new Pair<S, Map<A, Set<S>>>(entryS.getKey(), valueS);

			// collect into map
		}).collect(toMap(Pair::a, Pair::b));
	}

	/**
	 * 
	 * @param map
	 *            A table definition of the state transition function.
	 * @param initial
	 *            An initial state and an element in states.
	 * @param goals
	 *            The set of final states and a possibly empty subset of states.
	 * @throws NullPointerException
	 *             if map is null
	 * @throws IllegalArgumentException
	 *             map fails to define at least one pair of state in states and
	 *             symbol in alphabet
	 */
	public DeterministicFiniteAutomaton(Map<S, Map<A, Set<S>>> map, S initial, Set<? extends S> goals) {
		super(states(requireNonNull(map)), alphabet(map), transition(copy(map)), initial, goals);

		String message = states().collect(StringBuilder::new, (accumulator, state) -> {

			// append string of new errors
			accumulator.append(alphabet().map((symbol) -> {
				return transition(state, symbol).orElse(empty()).count() == 0 ? state + "\t" + symbol + "\n" : "";
			}).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append));

		}, StringBuilder::append).toString();

		if (!message.isEmpty()) {
			throw new IllegalArgumentException(ILLEGAL_MAP + message);
		}
	}

}
