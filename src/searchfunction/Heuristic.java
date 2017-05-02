package searchfunction;

import static java.util.Objects.*;
import java.util.function.Function;

/**
 * Represents a function that accepts one state and produces the estimated cost
 * of the cheapest solution from the given state.
 * 
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #apply(Object)}.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * <p>
 * Recently, moved some informed search functional interfaces into new
 * searchfunction package because it is easier to search for search functions in
 * a package named searchfunction rather than utility.
 * 
 * @author Jacob Malter
 *
 * @param <S>
 *            any state
 */
@FunctionalInterface
public interface Heuristic<S> {

	/**
	 * Applies this heuristic to the argument state.
	 *
	 * @param state
	 *            the heuristic argument
	 * @return the estimated cost of the cheapest solution from the given state
	 */
	float apply(S state);

	/**
	 * Returns a composed heuristic that first applies the {@code before}
	 * function to its input, and then applies this heuristic to the result. If
	 * evaluation of either function or heuristic throws an exception, it is
	 * relayed to the caller of the composed heuristic.
	 *
	 * @param <V>
	 *            the type of input to the {@code before} function, and to the
	 *            composed heuristic
	 * @param before
	 *            the function to apply before this heuristic is applied
	 * @return a composed heuristic that first applies the {@code before}
	 *         function and then applies this heuristic
	 * @throws NullPointerException
	 *             if before is null
	 */
	default <V> Heuristic<V> compose(Function<? super V, ? extends S> before) {
		requireNonNull(before);

		return (V v) -> apply(before.apply(v));
	}

}
