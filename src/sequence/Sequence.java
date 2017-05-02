package sequence;

import static java.util.Objects.deepEquals;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * A basic, immutable, persistent, generic representation of a logical list.
 * 
 * <P>
 * I want to quickly work with a sequence representation to cleanly express
 * logical lists. In addition to any benefits from immutability, optionals
 * specifically define an easy to use API, effectively eliminate an entire class
 * of bugs, and maintains immutability.
 * 
 * <p>
 * Optionals declare how sequences should be interpreted. An optional sequence
 * includes empty sequences, but outside of optional, sequences must be
 * non-empty. For example, all sequences are terminated with an empty optional,
 * so all client code checks rest is not present to determine whether some
 * sequence contains any additional items. For pure functions, accepting an
 * optional sequence indicates that function accepts empty sequences, and
 * returning a sequence outside of an optional indicates that a function returns
 * non-empty sequences. Given a clearly defined API, users tend to learn how to
 * use my code faster than a poorly defined API.
 * 
 * <p>
 * Optionals eliminate the possibility to access items in empty sequences. Since
 * an empty sequence must be inside an optional, client code cannot directly
 * call Sequence methods. Without the ability to call Sequence methods, there
 * are no scenarios where client code attempts to get items from empty
 * sequences. If the client wants to access a possibly empty sequence, the
 * Optional API must be used and considered. Given some consideration, there is
 * little room for mindless mistakes.
 * 
 * <p>
 * Similar to other classes in this project, Sequence gains benefits from its
 * immutability. Since I have covered those benefits in other documentation and
 * I would like to avoid repeating myself, find some other documentation.
 * 
 * <p>
 * This class is inspired by
 * <a href="https://clojure.org/reference/sequences">Clojure</a>.
 * 
 * @author Jacob Malter
 *
 * @param <T>
 *            type of element
 */
public final class Sequence<T> {

	/** The first item in the sequence. */
	private final T first;
	/** A possibly empty sequence of the items after first. */
	private final Optional<Sequence<T>> rest;

	/**
	 * Stores rest as {@link java.util.Optional#empty()}.
	 * 
	 * @param first
	 *            The first item in the sequence.
	 */
	protected Sequence(T first) {
		this.first = first;
		this.rest = requireNonNull(empty());
	}

	/**
	 * 
	 * @param first
	 *            The first item in the sequence.
	 * @param rest
	 *            A possibly empty sequence of the items after first.
	 * @throws NullPointerException
	 *             if rest is null
	 */
	protected Sequence(T first, Sequence<T> rest) {
		this.first = first;
		this.rest = of(requireNonNull(rest));
	}

	/**
	 * @return The first item in the sequence.
	 */
	public T first() {
		return first;
	}

	/**
	 * @return A possibly empty sequence of the items after first.
	 */
	public Optional<Sequence<T>> rest() {
		return rest;
	}

	/**
	 * @return A non-empty sequence of the items after first.
	 * @throws NoSuchElementException
	 *             if there are no items after first
	 */
	public Sequence<T> next() {
		return rest.get();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 * @see java.util.Objects#hash(Object...)
	 */
	@Override
	public int hashCode() {
		return hash(first, rest);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see java.util.Objects#deepEquals(Object, Object)
	 * @see java.util.Objects#equals(Object, Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Sequence)) {
			return false;
		}

		Sequence<?> other = (Sequence<?>) obj;
		return deepEquals(first, other.first) && Objects.equals(rest, other.rest);
	}

}
