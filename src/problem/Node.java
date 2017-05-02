package problem;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Optional;

/**
 * An immutable structure of four components: state, action, parent, and path
 * cost.
 * 
 * <p>
 * For this implementation, empty optional values are allowed only
 * simultaneously for action and parent.
 * 
 * <p>
 * I want to avoid managing data after being created. Through immutability, my
 * data structures encourage understandable simplicity, prevent defensive
 * programming, and easily achieve parallelism.
 * 
 * <p>
 * Immutable data structures become values rather than objects. In my design,
 * classes and fields accomplish immutability with final modifiers and a clean
 * API. To access data from values, getter methods produce values. Since fields
 * returned from getter methods are immutable, those fields always remain the
 * same. Treating objects as values minimizes the amount code in each object to
 * constructors, getter methods, and overridden methods. With the amount of code
 * minimized, these immutable data structures become simple and short;
 * therefore, these classes tend to be easier to understand and use.
 * 
 * <p>
 * Value based data structures contain more invariants than object based data
 * structures. In my constructors, instances reject invalid parameters
 * (including null parameters). Any value returned from a getter method must be
 * valid. When there is a guarantee that returned values are valid, client code
 * skips validation checks and focuses on what needs to be done rather than how
 * work is done. Throughout examples of value based data structures, those
 * functions use fewer checks on parameters and less instructions.
 * 
 * <p>
 * Immutable objects safely live in multi-threaded environments. When data is
 * guaranteed to remain the same, data in one execution context never changes
 * from updates in another execution context. Between threads, the same objects
 * provide data and optimization opportunities. Using the Java stream package,
 * traversing a collection of data structures guarantees safe access to any
 * element. When every element exists safely inside a collection, parallel
 * traversals through Java streams provide significantly faster execution at the
 * cost of trivial amounts of code.
 * 
 * <p>
 * Here is some additional reading:
 * <ul>
 * <li><a href="https://facebook.github.io/immutable-js/">Immutable.js</a></li>
 * <li><a href=
 * "https://www.sitepoint.com/functional-programming-ruby-value-objects/">Value
 * objects</a></li>
 * </ul>
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
 * @param <S>
 *            any state
 * @param <A>
 *            any action
 */
public final class Node<S, A> {

	/** The state in the state space to which the node corresponds. */
	private final S state;
	/** The action that was applied to the parent to generate the node. */
	private final Optional<A> action;
	/** The node in the search tree that generated this node. */
	private final Optional<Node<S, A>> parent;
	/** The cost of the path from the initial state to the node. */
	private final float pathCost;

	/**
	 * Sets action and parent to empty() and pathCost to 0.
	 * 
	 * @param state
	 *            The state in the state space to which the node corresponds.
	 */
	public Node(S state) {
		this.state = state; // null is valid
		this.action = empty(); // null is never valid
		this.parent = empty(); // null is never valid
		this.pathCost = 0;
	}

	/**
	 * @param state
	 *            The state in the state space to which the node corresponds.
	 * @param action
	 *            The action that was applied to the parent to generate the
	 *            node.
	 * @param parent
	 *            The node in the search tree that generated this node.
	 * @param pathCost
	 *            The cost of the path from the initial state to the node.
	 * @throws NullPointerException
	 *             if action or parent is null
	 */
	public Node(S state, A action, Node<S, A> parent, float pathCost) {
		this.state = state;
		this.action = of(requireNonNull(action));
		this.parent = of(requireNonNull(parent));
		this.pathCost = pathCost;
	}

	/**
	 * @return The state in the state space to which the node corresponds.
	 */
	public S state() {
		return state;
	}

	/**
	 * @return If this node is not a root node, the action that was applied to
	 *         the parent to generate the node.
	 */
	public Optional<A> action() {
		return action;
	}

	/**
	 * @return If this node is not a root node, the node in the search tree that
	 *         generated this node.
	 */
	public Optional<Node<S, A>> parent() {
		return parent;
	}

	/**
	 * @return The cost of the path from the initial state to the node.
	 */
	public float pathCost() {
		return pathCost;
	}

}
