package queuesearch;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static problem.Expander.expand;
import static problem.Solver.seq;

import java.util.Optional;
import java.util.Queue;

import problem.Node;
import problem.Problem;
import sequence.Sequence;

/**
 * An abstract search using a queue of leaf nodes available for expansion. This
 * class is a base class for {@link queuesearch.GraphSearch} and
 * {@link queuesearch.PriorityQueueSearch}.
 * 
 * <p>
 * I want client code to easily extend new search behaviors. Applying search and
 * stub methods, my queue searches promote straightforward extensions within and
 * without queue search.
 * 
 * <p>
 * My queue search API allows extensions. Firstly, search contains a frontier,
 * or open list, within its implementation so that subclasses can focus on other
 * tasks. Since search accepts a queue of any type of node, the behavior of
 * queue search completely changes depending on the given queue. If the given
 * queue is first-in-first-out, or a stack, queue search becomes a depth-first
 * search, but if the given queue is first-in-last-out, or a queue, queue search
 * becomes a breadth-first search. It is important to emphasize that callers
 * gain breadth or depth first behaviors by passing a different queue
 * implementation without any additional modifications; therefore, the cost of
 * change for queue search is minimal. If additional fields need to be accessed,
 * subclasses do work before calling the supper class search. Since add,
 * isEmpty, and remove are stubs, the behavior of queue search accepts new
 * subclasses. If a subclass wants to implement an explored set, or closed list,
 * with a field, a subclass becomes a graph search, and if the subclass wants to
 * implement efficient membership testing of states to nodes with a field, a
 * subclass can support decrease state operations for priority queues.
 * 
 * <p>
 * Client code easily composes queue searches. To implement search on a problem,
 * make a class with a queue search subclass field. Inside the search method,
 * create a new queue implementation and pass that queue and a given problem to
 * a queue search subclass field. With this implementation, search implementains
 * minimize code. Besides instantiation of a queue search implementation, search
 * classes need only to make arguments for queues and queue search
 * implementations such as evaluation and heursitic functions. Given small
 * search classes, client code becomes easy to understand.
 * 
 * <p>
 * Based on this <a href=
 * "https://github.com/jdmalter/fall2016/tree/master/Artificial%20Intelligence/Artificial%20Intelligence">repository</a>
 * which I <a href="https://github.com/jdmalter">(note the email)</a> wrote. The
 * arificial intelligence project was modeled after the
 * <a href="https://github.com/aimacode/aima-java">official AIMA repository in
 * java</a>.
 * </p>
 * 
 * @author Jacob Malter
 *
 * @param <Q>
 *            any queue of node of state and action
 * @param <P>
 *            any problem of state
 * @param <S>
 *            any state
 * @param <A>
 *            any action
 */
public abstract class QueueSearch<Q extends Queue<Node<S, A>>, P extends Problem<S, A>, S, A> {

	/**
	 * @param frontier
	 *            A queue of leaf nodes available for expansion.
	 * @param problem
	 *            A problem.
	 * @return An optional possibly empty list of actions that reaches a goal
	 *         state.
	 */
	public Optional<Optional<Sequence<A>>> search(Q frontier, P problem) {
		// make frontier whose only node is a root node
		frontier.clear();
		frontier.add(new Node<S, A>(problem.initial()));

		while (!isEmpty(frontier)) {
			// select some node for expansion
			Node<S, A> current = remove(frontier);

			// if the selected node is a goal state
			if (problem.goalTest(current.state())) {
				// then return its path
				return of(seq(current));
			}

			// otherwise expand the selected node
			expand(problem, current).forEach((child) -> add(frontier, child));
		}

		// there is no sequence of actions that reaches the goal
		return empty();
	}

	/**
	 * Adds a new leaf node available for expansion to the frontier.
	 * 
	 * @param frontier
	 *            A queue of leaf nodes available for expansion
	 * @param node
	 *            A new leaf node available for expansion.
	 * @return Whether a ne wleaf was added.
	 */
	public abstract boolean add(Q frontier, Node<S, A> node);

	/**
	 * Determines whether the frontier contains any nodes available for
	 * expansion.
	 * 
	 * @param frontier
	 *            A queue of leaf nodes available for expansion
	 * @return Whether the frontier contains any nodes available for expansion.
	 */
	public abstract boolean isEmpty(Q frontier);

	/**
	 * Removes and returns a node for expansion.
	 * 
	 * @param frontier
	 *            A queue of leaf nodes available for expansion
	 * @return A node for expansion.
	 */
	public abstract Node<S, A> remove(Q frontier);

}
