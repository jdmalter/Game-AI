package utility;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * Provides comparator constants and functions.
 * 
 * @author Jacob Malter
 */
public final class Comparators {

	/**
	 * A comparison function which orders floats based on
	 * {@link Mathf#lessThan(float, float)}.
	 */
	public static final BiFunction<Float, Float, Integer> ASCENDING = comparator((a, b) -> a < b);
	/**
	 * A comparison function which orders floats based on
	 * {@link Mathf#greaterThan(float, float)}.
	 */
	public static final BiFunction<Float, Float, Integer> DESCENDING = comparator((a, b) -> a > b);

	/**
	 * Cannot be instantiated by users.
	 */
	private Comparators() {

	}

	/**
	 * Returns a composed function that compares two inputs. Applies
	 * {@code predicate} to its input in the order given. If {@code predicate}
	 * returns true, then -1 is returned. Otherwise, applies {@code predicate}
	 * to its input in the reversed order. If {@code predicate} returns true,
	 * then 1 is returned. Otherwise, inputs are equal, and 0 is returned. If
	 * evaluation of {@code predicate} throws an exception, it is relayed to the
	 * caller of the composed function.
	 * 
	 * <p>
	 * This function is inspired by Clojure Programming (Emerick, Carper, and
	 * Grand 107-108).
	 * 
	 * @param predicate
	 *            whether the first input is less than the second input
	 * @return a composed function that compares two inputs.
	 * @throws NullPointerException
	 *             if {@code predicate} is null
	 */
	public static <T> BiFunction<T, T, Integer> comparator(BiPredicate<? super T, ? super T> predicate) {
		requireNonNull(predicate);

		return (t, u) -> predicate.test(t, u) ? -1 : predicate.test(u, t) ? 1 : 0;
	}

}
