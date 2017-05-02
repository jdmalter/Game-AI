package sequence;

import static java.util.Optional.of;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * Provides empty, count, seq, conj, stream, concat, and reverse operations on
 * sequences.
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
public final class Sequences {

	/**
	 * Cannot be instantiated by users.
	 */
	private Sequences() {

	}

	/**
	 * This implementation was inspired by
	 * <a href="https://clojuredocs.org/clojure.core/empty">Clojure</a>.
	 * 
	 * @param seq
	 *            A provided sequence.
	 * @return An empty collection of the same type as sequence.
	 */
	public static <T> Optional<Sequence<T>> empty(Optional<Sequence<T>> seq) {
		// must write Optional to avoid compiler confusion
		return Optional.empty();
	}

	/**
	 * This implementation was inspired by
	 * <a href="https://clojuredocs.org/clojure.core/empty">Clojure</a>.
	 * 
	 * @param seq
	 *            A provided sequence.
	 * @return An empty collection of the same type as sequence.
	 */
	public static <T> Optional<Sequence<T>> empty(Sequence<T> seq) {
		// must write Optional to avoid compiler confusion
		return Optional.empty();
	}

	/**
	 * This implementation was inspired by
	 * <a href="https://clojuredocs.org/clojure.core/count">Clojure</a>.
	 * 
	 * <p>
	 * This implementation must traverse the entire sequence to determine the
	 * number of items in the sequence!
	 * 
	 * @param seq
	 *            A provided sequence.
	 * @return The number of items in the sequence.
	 */
	public static <T> long count(Optional<Sequence<T>> seq) {
		return seq.map((nonempty) -> {
			return reduce(nonempty, 1L, (count, first) -> count + 1);
		}).orElse(0L);
	}

	/**
	 * This implementation was inspired by
	 * <a href="https://clojuredocs.org/clojure.core/count">Clojure</a>.
	 * 
	 * <p>
	 * This implementation must traverse the entire sequence to determine the
	 * number of items in the sequence!
	 * 
	 * @param seq
	 *            A provided sequence.
	 * @return The number of items in the sequence.
	 */
	public static <T> long count(Sequence<T> seq) {
		return count(of(seq));
	}

	/**
	 * This implementation was inspired by
	 * <a href="https://clojuredocs.org/clojure.core/seq">Clojure</a>.
	 * 
	 * <p>
	 * Unlike most functions in these packages, I accept an
	 * {@link java.lang.Iterable}. This function requires non-associative conj
	 * operations. While streams are powerful, streams fail to provided
	 * non-associative reduce methods. Instead of streams, iterables provided
	 * non-associative traversal for widely used groups of elements. To make my
	 * API as flexible as possible, iterable offers this factory class the
	 * widest range of acceptable input.
	 * 
	 * @param iterable
	 *            A provided iterable.
	 * @return A sequence over the iterable.
	 */
	public static <T> Optional<Sequence<T>> seq(Stream<T> stream) {
		return stream.reduce(empty(Optional.empty()), (seq, first) -> of(conj(seq, first)), Sequences::concat);
	}

	/**
	 * @param seq
	 *            A provided possibly empty sequence of the items after first.
	 * @param first
	 *            A provided first item in the sequence.
	 * @return A new sequence with the sequence added behind the first.
	 */
	public static <T> Sequence<T> conj(Optional<Sequence<T>> seq, T first) {
		return seq.map((nonempty) -> new Sequence<T>(first, nonempty)).orElse(new Sequence<T>(first));
	}

	/**
	 * @param seq
	 *            A provided non-empty sequence of the items after first.
	 * @param first
	 *            A provided first item in the sequence.
	 * @return A new sequence with the sequence added behind the first.
	 */
	public static <T> Sequence<T> conj(Sequence<T> seq, T first) {
		return new Sequence<T>(first, seq);
	}

	/**
	 * <p>
	 * This implementation must traverse the entire sequence to build the
	 * sequential stream!
	 * 
	 * @param seq
	 *            A provided sequence.
	 * @return A stream over the items in sequence.
	 */
	public static <T> Stream<T> stream(Optional<Sequence<T>> seq) {
		return seq.map((nonempty) -> {
			return reduce(nonempty, Stream.<T>builder(), Builder::add).build();
		}).orElse(Stream.empty());
	}

	/**
	 * <p>
	 * This implementation must traverse the entire sequence to build the
	 * sequential stream!
	 * 
	 * @param seq
	 *            A provided sequence.
	 * @return A stream over the items in sequence.
	 */
	public static <T> Stream<T> stream(Sequence<T> seq) {
		return stream(of(seq));
	}

	/**
	 * Reduce seriously eliminates some duplicate code!
	 */

	/**
	 * This is based on the
	 * <a href= "https://www.w3schools.com/jsref/jsref_reduce.asp">Javascript
	 * reduce function</a> with some modifications.
	 * 
	 * @param seq
	 *            A provided sequence.
	 * @param identity
	 *            The initial return value.
	 * @param accumulator
	 *            A bifunction that takes the current return value and the first
	 *            item in the sequence and returns a new return value.
	 * @return A single return value.
	 */
	public static <T, U> U reduce(Sequence<? extends T> seq, U identity, BiFunction<U, ? super T, U> accumulator) {
		// combine the first item in sequence
		identity = accumulator.apply(identity, seq.first());

		// while there are more items to accumulate
		while (seq.rest().isPresent()) {
			// move to those items
			seq = seq.next();

			// and accumulate the new first item
			identity = accumulator.apply(identity, seq.first());
		}

		// return accumulation when there are no more items
		return identity;
	}

	/**
	 * If b is empty, then a is returned. If a is empty, then b is returned.
	 * Otherwise, this implementation does exactly what you'd expect.
	 * 
	 * @param a
	 *            First provided sequence.
	 * @param b
	 *            Second provided sequence.
	 * @return A sequence which contains the items of the sequence followed by
	 *         the items of the second sequence.
	 */
	public static <T> Optional<Sequence<T>> concat(Optional<Sequence<T>> a, Optional<Sequence<T>> b) {
		return b.map((nonempty) -> {
			return reverse(a).map((reverse) -> {
				return of(reduce(reverse, conj(b, reverse.first()), (concat, first) -> {
					return conj(concat, first);
				}));
			}).orElse(b);
		}).orElse(a);
	}

	/**
	 * @param seq
	 *            A provided sequence.
	 * @return A sequence whose items are in reverse order.
	 */
	public static <T> Optional<Sequence<T>> reverse(Optional<Sequence<T>> seq) {
		return seq.map((nonempty) -> {
			return of(reduce(nonempty, conj(empty(seq), nonempty.first()), (reverse, first) -> {
				return conj(reverse, first);
			}));
		}).orElse(seq);
	}

}
