package domain;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

import digraph.HashDigraph;

/**
 * A {@code HashDigraph} that calculates edge values by a weight function.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * @author Jacob Malter
 *
 * @param <V>
 *            any vertex
 * @param <E>
 *            any edge
 */
public final class FloorGraph<V, E> extends HashDigraph<V, E> {

	/** Returns a non-null weight of the edge between two vertices. */
	private final BiFunction<V, V, E> weight;

	/**
	 * This implementation has issues in {@link #add(Object, Object, Object)}
	 * when weight returns null. Additonally, it is assumed that weight is
	 * associative; alternatively, {@code weight.apply(v, u)} equals
	 * {@code weight.apply(u, v)}.
	 * 
	 * @param weight
	 *            Returns a non-null weight of the edge between two vertices.
	 * @throws NullPointerException
	 *             if weight is null
	 */
	public FloorGraph(BiFunction<V, V, E> weight) {
		this.weight = requireNonNull(weight);
	}

	/**
	 * Adds every source point and edges between every point.
	 * 
	 * @param d
	 *            A set of source points where all points have edges between
	 *            each other.
	 * @return Whether this graph was modified.
	 */
	public boolean add(Domain<V> d) {
		// add every source point
		boolean added = d.sources().map(this::add).reduce(false, Boolean::logicalOr);

		// connect every source point to each other
		return d.sources().map((v) -> {

			return d.sources().filter((u) -> {
				return v != u; // do not add self loops
			}).map((u) -> {
				return add(v, u, null);
			}).reduce(false, Boolean::logicalOr);

		}).reduce(added, Boolean::logicalOr);
	}

	/**
	 * Adds edges between every pair of points from each domain.
	 * 
	 * @param dV
	 *            One set of source points where all points have edges between
	 *            each other.
	 * @param dU
	 *            Another set of source points where all points have edges
	 *            between each other.
	 * @return Whether this graph was modified.
	 */
	public boolean add(Domain<V> dV, Domain<V> dU) {
		// for every source point in v
		return dV.sources().map((v) -> {

			// for every source point in u
			return dU.sources().map((u) -> {
				return add(v, u, null); // add an edge with overriden add
			}).reduce(false, Boolean::logicalOr);

		}).reduce(false, Boolean::logicalOr);
	}

	/**
	 * Replaces e with the function result of weight applied to v and u.
	 */
	@Override
	public boolean add(V v, V u, E e) {
		// assume weight.apply(v, u) equals weight.apply(u, v);
		E result = weight.apply(v, u);

		return super.add(v, u, result) && super.add(u, v, result);
	}

}
