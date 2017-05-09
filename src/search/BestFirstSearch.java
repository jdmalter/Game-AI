package search;

import static function.HigherOrder.compose;
import static java.util.Objects.requireNonNull;
import static utility.Comparators.ASCENDING;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Function;

import problem.Node;
import problem.Problem;
import queuesearch.PriorityQueueSearch;
import sequence.Sequence;

/**
 * An abstract informed priority queue search using an evaluation function.
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
 * <p>
 * Used {@link function.HigherOrder} to make comparator more compact.
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
public abstract class BestFirstSearch<P extends Problem<S, A>, S, A> implements Search<P, S, A> {

	/** A priority queue search that stores explored nodes. */
	private final PriorityQueueSearch<P, S, A> priorityQueueSearch;
	/**
	 * Returns the estimated cost of the cheapest solution through the given
	 * node.
	 */
	private final Function<? super Node<S, A>, Float> evaluation;

	/**
	 * @param priorityQueueSearch
	 *            A priority queue search that stores explored nodes.
	 * @param evaluation
	 *            Returns the estimated cost of the cheapest solution through
	 *            the given node.
	 * @throws NullPointerException
	 *             if priorityQueueSearch or evaluation is null
	 */
	public BestFirstSearch(PriorityQueueSearch<P, S, A> priorityQueueSearch,
			Function<? super Node<S, A>, Float> evaluation) {
		this.priorityQueueSearch = requireNonNull(priorityQueueSearch);
		this.evaluation = requireNonNull(evaluation);
	}

	@Override
	public Optional<Optional<Sequence<A>>> search(P problem) {
		Comparator<Node<S, A>> comparator = compose(ASCENDING, evaluation::apply)::apply;
		PriorityQueue<Node<S, A>> frontier = new PriorityQueue<Node<S, A>>(comparator);
		return priorityQueueSearch.search(frontier, problem);
	}

}
