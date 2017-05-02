package searchfunction;

import problem.Node;

/**
 * Represents a function that accepts one node and produces the estimated cost
 * of the cheapest solution through the given node.
 * 
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #apply(Node)}.
 * 
 * <p>
 * Recently, moved some informed search functional interfaces into new
 * searchfunction package because it is easier to search for search functions in
 * a package named searchfunction rather than utility.
 * 
 * @author Jacob Malter
 *
 * @param <S>
 *            any state
 * @param <A>
 *            any action
 */
@FunctionalInterface
public interface Evaluation<S, A> {

	/**
	 * Applies this evaluation to the argument node.
	 *
	 * @param state
	 *            the evaluted argument
	 * @return the estimated cost of the cheapest solution through the given
	 *         node
	 */
	float apply(Node<S, A> state);

}