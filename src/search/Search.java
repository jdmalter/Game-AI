package search;

import java.util.Optional;

import problem.Problem;
import sequence.Sequence;

/**
 * A functional interface for search.
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
@FunctionalInterface
public interface Search<P extends Problem<S, A>, S, A> {

	/**
	 * @param problem
	 *            A problem.
	 * @return An optional possibly empty list of actions that reaches the goal.
	 */
	Optional<Optional<Sequence<A>>> search(P problem);

}
