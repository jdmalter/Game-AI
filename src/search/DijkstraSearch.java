package search;

import static searchfunction.Factory.zero;

import problem.Problem;
import queuesearch.PriorityQueueSearch;

/**
 * An informed priority queue search using an A* evaluation function whose
 * heurisitic always returns zero.
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
 * @param <P>
 *            any problem of state and action
 * @param <S>
 *            any state
 * @param <A>
 *            any action
 */
public class DijkstraSearch<P extends Problem<S, A>, S, A> extends AStarSearch<P, S, A> {

	/**
	 * If any parameter is null, a customized
	 * {@link java.lang.NullPointerException} is thrown.
	 * 
	 * @param priorityQueueSearch
	 *            A priority queue search that stores explored nodes.
	 */
	public DijkstraSearch(PriorityQueueSearch<P, S, A> priorityQueueSearch) {
		super(priorityQueueSearch, zero());
	}

}
