package digraphproblem;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.empty;

import java.util.Objects;

import digraph.Digraph;
import problem.Problem;

/**
 * A graph based problem where the objective is to find a path from the initial
 * vertex to the goal vertex.
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
 *            any vertex/state
 */
public class DigraphProblem<V> extends Problem<V, V> {

	/**
	 * @param digraph
	 *            A mathematical structure that consists of two sets of
	 *            elements: a set of vertices and a set of edges. Edges are
	 *            weighted ordered pairs of vertices.
	 * @param initial
	 *            The initial state that the agent starts in.
	 * @param goal
	 *            The goal state that the agent ends in.
	 * @throws NullPointerException
	 *             if digprah is null
	 */
	public DigraphProblem(Digraph<V, Float> digraph, V initial, V goal) {
		super(initial, (state) -> {
			return digraph.get(state).orElse(empty()).collect(toSet());
		}, (state, action) -> {
			return action;
		}, (state) -> {
			return Objects.equals(state, goal);
		}, (state, action, result) -> {
			return digraph.get(state, result).orElse(Float.POSITIVE_INFINITY);
		});

		requireNonNull(digraph);
	}

}
