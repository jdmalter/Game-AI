package behaviortree;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.generate;
import static utility.Random.nextFloat;

import java.util.stream.Stream;

/**
 * Defines finite and infinite selector and sequence behavior trees.
 * 
 * <p>
 * I want to avoid unexpected returns from function calls. Through pure
 * functions, my static functions only focus on what work needs to be done,
 * quickly compose into complex behaviors, and safely achieve parallelism.
 * 
 * <p>
 * Pure functions map input arguments to output values. In my design, helper
 * classes provide pure functions through static methods. Within each function,
 * input transforms into output. When each line of code works with input or
 * mapped input, code in each function avoid unnecessary work. Without
 * unnecessary lines of code, pure functions minimize the length of functions.
 * 
 * <p>
 * Helper functions remain predictable regardless of who calls functions. Inside
 * the lowest level pure functions, code guarantees consistent return values or
 * calls safe Java library static functions. When it is guaranteed that the
 * output only depends on the input, these functions avoid bugs caused by
 * mutable external state. Since pure functions eliminate an entire class of
 * bugs, there is a lower cost to regularly reuse and rely on those functions.
 * As the cost of developing with pure functions decreases while holding
 * constant development resources, the amount of code produced increases, and
 * therefore more working code enables more complex functions to be successfully
 * completed.
 * 
 * <p>
 * Pure functions easily become parallelized. Again, pure functions return the
 * same output of for the same input regardless of execution context. Since a
 * list of inputs can be split into threads, each input can be passed in
 * parallel to a pure function to return results. Using the Java stream package,
 * a collection of inputs can be mapped into outputs in parallel without bugs
 * arising from external state. Throughout my code, there are many successful
 * examples of streams using pure functions to quickly perform operations on
 * large data structures.
 * 
 * <p>
 * Here is some additional reading:
 * <ul>
 * <li><a href=
 * "https://medium.com/javascript-scene/master-the-javascript-interview-what-is-a-pure-function-d1c076bec976#.vsrql8u01">Javascript
 * pure functions</a></li>
 * <li><a href=
 * "https://www.sitepoint.com/functional-programming-pure-functions/">Pure
 * functions</a></li>
 * </ul>
 * 
 * @author Jacob Malter
 */
public final class Decorator {

	/**
	 * Cannot be instantiated by users.
	 */
	private Decorator() {

	}

	/**
	 * This function is a helper function to reduce duplicated code. Returns an
	 * infinite stream where each element is the provided tree.
	 * 
	 * @param tree
	 *            A tree to be infinitely streamed.
	 * @return an infinite stream where each element is the provided tree.
	 * @throws NullPointerException
	 *             if tree null
	 */
	private static Stream<Tree> stream(Tree tree) {
		requireNonNull(tree);

		return generate(() -> tree);
	}

	/**
	 * This function is a helper function to reduce duplicated code. A selected
	 * tree returns whether any tree in trees returns true.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#anyMatch(java.util.function.Predicate)}.
	 * 
	 * @param trees
	 *            A list of trees to be selected.
	 * @return a selected tree
	 */
	private static Tree select(Stream<Tree> trees) {
		return () -> trees.anyMatch(Tree::behave);
	}

	/**
	 * This function is a helper function to reduce duplicated code. A sequenced
	 * tree returns whether all trees in trees return true.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#allMatch(java.util.function.Predicate)}.
	 * 
	 * @param trees
	 *            A list of trees to be sequenced.
	 * @return a sequenced tree
	 */
	private static Tree sequence(Stream<Tree> trees) {
		return () -> trees.allMatch(Tree::behave);
	}

	/**
	 * Returns an finitely selected tree where each tree is the provided tree. A
	 * selected tree returns whether any tree returns true. It is possible for
	 * the tree to short-circuit its execution.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#anyMatch(java.util.function.Predicate)}.
	 * 
	 * @param tree
	 *            A tree to be finitely selected.
	 * @param maxSize
	 *            the number of trees to be limited to.
	 * @return an finitely selected tree where each tree is the provided tree.
	 * @throws NullPointerException
	 *             if tree null
	 */
	public static Tree finiteSelect(Tree tree, long maxSize) {
		requireNonNull(tree);

		return () -> select(stream(tree).limit(maxSize)).behave();
	}

	/**
	 * Returns an finitely sequenced tree where each tree is the provided tree.
	 * A selected tree returns whether all trees return true. It is possible for
	 * the tree to short-circuit its execution.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#allMatch(java.util.function.Predicate)}.
	 * 
	 * @param tree
	 *            A tree to be finitely sequenced.
	 * @param maxSize
	 *            the number of trees to be limited to.
	 * @return an finitely sequenced tree where each tree is the provided tree.
	 * @throws NullPointerException
	 *             if tree null
	 */
	public static Tree finiteSequence(Tree tree, long maxSize) {
		requireNonNull(tree);

		return () -> sequence(stream(tree).limit(maxSize)).behave();
	}

	/**
	 * Returns an infinitely selected tree where each tree is the provided tree.
	 * A selected tree returns whether any tree returns true. It is necessary
	 * for the infinite tree to short-circuit to end its execution.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#anyMatch(java.util.function.Predicate)}.
	 * 
	 * @param tree
	 *            A tree to be infinitely selected.
	 * @return an infinitely selected tree where each tree is the provided tree.
	 * @throws NullPointerException
	 *             if tree null
	 */
	public static Tree infiniteSelect(Tree tree) {
		requireNonNull(tree);

		return () -> select(stream(tree)).behave();
	}

	/**
	 * Returns an infinitely sequenced tree where each tree is the provided
	 * tree. A selected tree returns whether all trees return true. It is
	 * necessary for the infinite tree to short-circuit to end its execution.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#allMatch(java.util.function.Predicate)}.
	 * 
	 * @param tree
	 *            A tree to be infinitely sequenced.
	 * @return an infinitely sequenced tree where each tree is the provided
	 *         tree.
	 * @throws NullPointerException
	 *             if tree null
	 */
	public static Tree infiniteSequence(Tree tree) {
		requireNonNull(tree);

		return () -> sequence(stream(tree)).behave();
	}

	/**
	 * Returns a randomized tree where the provided tree is executed with the
	 * provided probability.
	 * 
	 * <p>
	 * This implementation uses {@link utility.Random#nextFloat()} and behaves
	 * as the code is equivalent to:
	 * 
	 * <p>
	 * {@code () -> nextFloat() < probability && tree.behave();}
	 * 
	 * @param tree
	 *            A tree to be randomized.
	 * @param probability
	 *            the likelihood that the provided tree is executed during each
	 *            call to the composed tree represented as a float between 0.0
	 *            and 1.0 representing never and always executed respectively
	 * @return a randomized tree where the provided tree is executed with the
	 *         provided probability.
	 * @throws NullPointerException
	 *             if tree is null
	 */
	public static Tree randomize(Tree tree, float probability) {
		requireNonNull(tree);

		return () -> nextFloat() < probability && tree.behave();
	}

	/**
	 * Returns a negated tree.
	 * 
	 * <p>
	 * This implementation behaves as the code is equivalent to:
	 * 
	 * <p>
	 * {@code () -> !tree.behave();}
	 * 
	 * @param tree
	 *            A tree to be negated.
	 * @return a negated tree.
	 * @throws NullPointerException
	 *             if tree is null
	 */
	public static Tree negate(Tree tree) {
		requireNonNull(tree);

		return () -> !tree.behave();
	}

}
