package search;

import static java.util.Objects.requireNonNull;

import problem.Problem;
import queuesearch.PriorityQueueSearch;
import searchfunction.Heuristic;

/**
 * An informed priority queue search using an A* evaluation function.
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
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * @author Jacob Malter
 *
 * @param <P>
 *            any problem of state and action
 * @param <S>
 *            any state
 * @param <A>
 *            any action
 */
public class AStarSearch<P extends Problem<S, A>, S, A> extends BestFirstSearch<P, S, A> {

	/**
	 * If any parameter is null, a customized
	 * {@link java.lang.NullPointerException} is thrown.
	 * 
	 * @param priorityQueueSearch
	 *            A priority queue search that stores explored nodes.
	 * @param heuristic
	 *            Returns the estimated cost of the cheapest solution through
	 *            the given node.
	 */
	public AStarSearch(PriorityQueueSearch<P, S, A> priorityQueueSearch, Heuristic<S> heuristic) {
		super(priorityQueueSearch, (node) -> node.pathCost() + heuristic.apply(node.state()));

		requireNonNull(heuristic);
	}

}
