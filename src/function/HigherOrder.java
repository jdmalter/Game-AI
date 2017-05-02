package function;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides higher order functions.
 * 
 * <p>
 * I want to avoid unexpected returns from function calls. Through pure
 * functions, my static functions only focus on what work needs to be done,
 * quickly compose into complex behaviors, and safely achieve parallelism.
 * 
 * <p>
 * Pure functions map input arguments to output values. In my design, helper
 * classes provide pure functions through static methods. Within each function,
 * input transforms into output. When each line of code works with input or
 * mapped input, code in each function avoid unnecessary work. Without
 * unnecessary lines of code, pure functions minimize the length of functions.
 * 
 * <p>
 * Helper functions remain predictable regardless of who calls functions. Inside
 * the lowest level pure functions, code guarantees consistent return values or
 * calls safe Java library static functions. When it is guaranteed that the
 * output only depends on the input, these functions avoid bugs caused by
 * mutable external state. Since pure functions eliminate an entire class of
 * bugs, there is a lower cost to regularly reuse and rely on those functions.
 * As the cost of developing with pure functions decreases while holding
 * constant development resources, the amount of code produced increases, and
 * therefore more working code enables more complex functions to be successfully
 * completed.
 * 
 * <p>
 * Pure functions easily become parallelized. Again, pure functions return the
 * same output of for the same input regardless of execution context. Since a
 * list of inputs can be split into threads, each input can be passed in
 * parallel to a pure function to return results. Using the Java stream package,
 * a collection of inputs can be mapped into outputs in parallel without bugs
 * arising from external state. Throughout my code, there are many successful
 * examples of streams using pure functions to quickly perform operations on
 * large data structures.
 * 
 * <p>
 * Here is some additional reading:
 * <ul>
 * <li><a href=
 * "https://medium.com/javascript-scene/master-the-javascript-interview-what-is-a-pure-function-d1c076bec976#.vsrql8u01">Javascript
 * pure functions</a></li>
 * <li><a href=
 * "https://www.sitepoint.com/functional-programming-pure-functions/">Pure
 * functions</a></li>
 * </ul>
 * 
 * @author Jacob Malter
 */
public final class HigherOrder {

	/**
	 * Cannot be instantiated by users.
	 */
	private HigherOrder() {

	}

	/**
	 * Returns a composed supplier that first gets {@code supplier} and then
	 * applies the {@code after} function. If evaluation of either function
	 * throws an exception, it is relayed to the caller of the composed
	 * function.
	 * 
	 * @param supplier
	 *            the supplier to get before {@code after} is applied
	 * @param after
	 *            a function to apply the results of {@code supplier} to
	 * @return a composed supplier that first gets {@code supplier} and then
	 *         applies the {@code after} function
	 * @throws NullPointerException
	 *             if {@code supplier} or {@code after} is null
	 */
	public static <R, V> Supplier<V> andThen(Supplier<? extends R> supplier, Function<? super R, ? extends V> after) {
		requireNonNull(supplier);
		requireNonNull(after);

		return () -> after.apply(supplier.get());
	}

	/**
	 * Returns a composed function that first applies {@code before} to its
	 * input, and then applies the {@code function} to the result. If evaluation
	 * of either function throws an exception, it is relayed to the caller of
	 * the composed function.
	 * 
	 * @param function
	 *            a function to apply results of {@code before} to
	 * @param before
	 *            the function to apply before this function is applied
	 * @return a composed function that first applies {@code before} and then
	 *         applies the {@code function}
	 * @throws NullPointerException
	 *             if {@code function} or {@code before} is null
	 */
	public static <T, R, V> BiFunction<T, T, V> compose(BiFunction<R, R, ? extends V> function,
			Function<? super T, R> before) {
		requireNonNull(function);
		requireNonNull(before);

		return (t, u) -> function.apply(before.apply(t), before.apply(u));
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
