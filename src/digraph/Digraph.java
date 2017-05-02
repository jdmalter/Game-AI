package digraph;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A mathematical structure that consists of two sets of elements: a set of
 * vertices and a set of edges. Edges are weighted ordered pairs of vertices.
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
public interface Digraph<V, E> {

	/**
	 * @param v
	 *            Some vertex being added.
	 * @return Whether the vertex was added.
	 */
	boolean add(V v);

	/**
	 * If e is null, a customized {@link java.lang.NullPointerException} is
	 * thrown.
	 * 
	 * @param v
	 *            The tail of the edge.
	 * @param u
	 *            The head of the edge.
	 * @param e
	 *            The non-null weight of the edge.
	 * @return Whether the edge was added.
	 */
	boolean add(V v, V u, E e);

	/**
	 * @param v
	 *            Some vertex being queried.
	 * @return Whether this graph contains the given vertex.
	 */
	boolean contains(V v);

	/**
	 * @param v
	 *            The tail of the edge.
	 * @param u
	 *            The head of the edge.
	 * @return Whether this graph contains the given edge.
	 */
	boolean contains(V v, V u);

	/**
	 * An empty stream is returned if this graph does not contain any vertices.
	 * 
	 * @return All vertices in this graph.
	 */
	Stream<V> get();

	/**
	 * An empty optional is returned if this graph does not contain the given
	 * vertex.
	 * 
	 * @param v
	 *            Some vertex.
	 * @return If present, all adjacent vertices.
	 */
	Optional<Stream<V>> get(V v);

	/**
	 * Any empty optional is returned if this graph does not contain the given
	 * edge.
	 * 
	 * @param v
	 *            The tail of the edge.
	 * @param u
	 *            The head of the edge.
	 * @return If present, the weight of the edge.
	 */
	Optional<E> get(V v, V u);

	/**
	 * Additionally, removes all edges containing the given vertex.
	 * 
	 * @param v
	 *            Some vertex being removed.
	 * @return Whether the vertex was removed.
	 */
	boolean remove(V v);

	/**
	 * @param v
	 *            The tail of the edge.
	 * @param u
	 *            The head of the edge.
	 * @return Whether the edge was removed.
	 */
	boolean remove(V v, V u);

}
