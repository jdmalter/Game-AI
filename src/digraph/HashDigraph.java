package digraph;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A graph implementation using a hash map of vertices to a hash map of adjacent
 * vertices to weights.
 * 
 * <p>
 * I want to to do more work with the same hours of the day. With streams, my
 * digraph data structure offers understandable methods, captures new
 * functionality, and trivially achieves parallelism.
 * 
 * <p>
 * My digraph implementation API is minimal. In the interface, there are only
 * overloaded add, contains, get, and remove methods. Even better, these methods
 * do exactly what callers would expect from their names and parameters. For
 * example, add with one parameter adds some vertex, and add with two parameters
 * add some edge between two vertexes. Minimizing the number of methods on an
 * interface reduces the work to implement said interface, so Digraph reduced my
 * work on HashDigraph. Additionally, HashDigraph requires fewer changes to
 * accommodate changes in Digraph.
 * 
 * <p>
 * Java library classes vastly expand the number and scope of tasks that data
 * structures can accomplish. In get, streams seamlessly support numerous
 * requirements which would be odious otherwise. To find all vertexes not on the
 * open list, call get and filter, and to find the average position of vectors,
 * call get, map, and then reduce. When code focuses on higher level tasks,
 * unnecessary code like for loops, finding sums, flattening collections, and
 * filtering edges occupy minimal space. Throughout various classes, streams
 * simplify complicated tasks. Also, optional offers cleaner abstractions and
 * intentions. Without optional classes, imagine how to deal with getting edges
 * from a digraph. If the digraph does not contain the vertex, should an empty
 * stream or a null stream be returned? Are an empty stream for an absent vertex
 * the same as an empty stream for a vertex with no outgoing edges? Optionals
 * eliminate those ambiguities and implement methods to answer those questions.
 * When finding edge weights, optional values clarify boundary cases by
 * requiring exceptions to be considered. In DigraphProblem, optional forced my
 * client code to consider the cost of action between two states where there
 * should be no action to access the cost inside the returned optional. When no
 * edge is present, I chose to return positive infinity. Without optional, I
 * would have failed to consider a class of bugs and boundary cases which create
 * additional debugging during later in development; therefore, I spent more
 * time writing new functionality rather than debugging old mistakes.
 * 
 * <p>
 * Streams thrive in multi-threaded environments. To achieve parallelism on a
 * stream, call parallel. With one function call, execution speed dramatically
 * drops (at the cost of using additional computation power). Compounded with
 * immutable objects, an iteration over a digraph's vertexes costs near constant
 * time. Since there are few steps to add parallelism, there is far more work
 * completed in faster computation times versus the cost of adding a few couple
 * letters.
 * 
 * @see Optional
 * @see Stream
 * 
 * @author Jacob Malter
 *
 * @param <V>
 *            any vertex
 * @param <E>
 *            any edge
 */
public class HashDigraph<V, E> implements Digraph<V, E> {

	/** A mapping of vertices to a mapping of adjacent vertices to weights. */
	private final Map<V, Map<V, E>> vertices = new LinkedHashMap<V, Map<V, E>>();

	@Override
	public boolean add(V v) {
		Supplier<Boolean> addExpression = () -> {
			vertices.put(v, new LinkedHashMap<V, E>());
			return true;
		};

		return !vertices.containsKey(v) && addExpression.get();
	}

	@Override
	public boolean add(V v, V u, E e) {
		requireNonNull(e, "e must not be null");
		Supplier<Boolean> addExpression = () -> {
			vertices.get(v).put(u, e);
			return true;
		};

		return vertices.containsKey(v) && vertices.containsKey(u) && !vertices.get(v).containsKey(u)
				&& addExpression.get();
	}

	@Override
	public boolean contains(V v) {
		return vertices.containsKey(v);
	}

	@Override
	public boolean contains(V v, V u) {
		return vertices.containsKey(v) && vertices.get(v).containsKey(u);
	}

	@Override
	public Stream<V> get() {
		return vertices.keySet().stream();
	}

	@Override
	public Optional<Stream<V>> get(V v) {
		return vertices.containsKey(v) ? of(vertices.get(v).keySet().stream()) : empty();
	}

	@Override
	public Optional<E> get(V v, V u) {
		return vertices.containsKey(v) && vertices.get(v).containsKey(u) ? of(vertices.get(v).get(u)) : empty();
	}

	@Override
	public boolean remove(V v) {
		Supplier<Boolean> removeExpression = () -> {
			vertices.keySet().forEach((u) -> remove(u, v));
			vertices.remove(v);
			return true;
		};

		return vertices.containsKey(v) && removeExpression.get();
	}

	@Override
	public boolean remove(V v, V u) {
		Supplier<Boolean> removeExpression = () -> {
			vertices.get(v).remove(u);
			return true;
		};

		return vertices.containsKey(v) && vertices.get(v).containsKey(u) && removeExpression.get();
	}

	@Override
	public String toString() {
		return vertices.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return vertices.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof HashDigraph)) {
			return false;
		}

		HashDigraph<?, ?> other = (HashDigraph<?, ?>) obj;
		return Objects.equals(vertices, other.vertices);
	}

}
