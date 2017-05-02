package function;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #apply(Object, Object, Object)}.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * <p>
 * Recently, moved some functional interfaces into new function package because
 * it is easier to search for functions in a package named function rather than
 * utility.
 * 
 * @author Jacob Malter
 *
 * @param <T>
 *            the type of the first argument to the function
 * @param <U>
 *            the type of the second argument to the function
 * @param <V>
 *            the type of the third argument to the function
 * @param <R>
 *            the type of the result of the function
 *
 *
 * @see Function
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t
	 *            the first function argument
	 * @param u
	 *            the second function argument
	 * @param v
	 *            the third function argument
	 * @return the function result
	 */
	R apply(T t, U u, V v);

	/**
	 * Returns a composed function that first applies this function to its
	 * input, and then applies the {@code after} function to the result. If
	 * evaluation of either function throws an exception, it is relayed to the
	 * caller of the composed function.
	 *
	 * @param <S>
	 *            the type of output of the {@code after} function, and of the
	 *            composed function
	 * @param after
	 *            the function to apply after this function is applied
	 * @return a composed function that first applies this function and then
	 *         applies the {@code after} function
	 * @throws NullPointerException
	 *             if {@code after} is null
	 */
	default <S> TriFunction<T, U, V, S> andThen(Function<? super R, ? extends S> after) {
		requireNonNull(after);

		return (T t, U u, V v) -> after.apply(apply(t, u, v));
	}
}
