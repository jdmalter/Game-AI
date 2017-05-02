package domain;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static java.util.Objects.*;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A set of source points where all points have visibility between each other.
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
 *            source point type
 */
public final class Domain<S> {

	/** A set of source points. */
	private final Set<S> sources = new HashSet<>();

	/**
	 * If sources or any source is null, a customized
	 * {@link java.lang.NullPointerException} is thrown.
	 * 
	 * @param sources
	 *            An array of source points.
	 */
	public Domain(S[] sources) {
		this(asList(sources));
	}

	/**
	 * If sources or any source is null, a customized
	 * {@link java.lang.NullPointerException} is thrown.
	 * 
	 * @param sources
	 *            A collection of source points.
	 */
	public Domain(Collection<? extends S> sources) {
		requireNonNull(sources).forEach(Objects::requireNonNull);

		this.sources.addAll(sources);
	}

	/**
	 * @return A sequential Stream with a set of source points as its source.
	 */
	public Stream<S> sources() {
		return sources.stream();
	}

}
