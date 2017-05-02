package behaviortree;

import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Defines sequential, random, and parallel selector and sequence behavior
 * trees.
 * 
 * <p>
 * Added variable arity versions of functions so that client code avoids calling
 * asList in many examples.
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
public final class Composite {

	/**
	 * Cannot be instantiated by users.
	 */
	private Composite() {

	}

	/**
	 * @param trees
	 *            A list of trees to be copied.
	 * @return a new {@code List} on the non-null elements of the given list
	 * @throws NullPointerException
	 *             if trees or any tree in trees is null
	 */
	private static List<Tree> copy(List<Tree> trees) {
		return trees.stream().map(Objects::requireNonNull).collect(toList());
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
	 * Returns the shuffled list.
	 * 
	 * <p>
	 * This implementation uses {@link java.util.Collections#shuffle(List)} and
	 * modifies the provided list.
	 * 
	 * @param list
	 *            the list to be shuffled.
	 * @return the shuffled list.
	 */
	private static <T> List<T> random(List<T> list) {
		shuffle(list);
		return list;
	}

	/**
	 * Returns a sequentially selected tree. A sequential tree evaluates its
	 * trees in the provided order. A selected tree returns whether any tree in
	 * trees returns true. It is possible for the tree to short-circuit its
	 * execution. If trees is empty then false is returned.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#anyMatch(java.util.function.Predicate)}.
	 * 
	 * @param trees
	 *            A list of trees to be selected.
	 * @return a sequentially selected tree
	 * @throws NullPointerException
	 *             if trees or any tree in trees is null
	 */
	public static Tree select(List<Tree> trees) {
		List<Tree> copy = copy(trees);

		return () -> select(copy.stream()).behave();
	}

	/**
	 * This implementation wraps {@link #select(List)}.
	 * 
	 * @param trees
	 *            An array of trees to be selected.
	 * @return a sequentially selected tree
	 */
	public static Tree select(Tree... trees) {
		return select(asList(trees));
	}

	/**
	 * Returns a sequentially sequenced tree. A sequential tree evaluates its
	 * trees in the provided order. A sequenced tree returns whether all trees
	 * in trees return true. It is possible for the tree to short-circuit its
	 * execution. If trees is empty then true is returned.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#allMatch(java.util.function.Predicate)}.
	 * 
	 * @param trees
	 *            A list of trees to be sequenced.
	 * @return a sequentially sequenced tree
	 * @throws NullPointerException
	 *             if trees or any tree in trees is null
	 */
	public static Tree sequence(List<Tree> trees) {
		List<Tree> copy = copy(trees);

		return () -> sequence(copy.stream()).behave();
	}

	/**
	 * This implementation wraps {@link #sequence(List)}.
	 * 
	 * @param trees
	 *            An array of trees to be sequenced.
	 * @return a sequentially sequenced tree
	 */
	public static Tree sequence(Tree... trees) {
		return sequence(asList(trees));
	}

	/**
	 * Returns a randomly selected tree. A random tree evaluates its trees in
	 * the shuffled order for each test. A selected tree returns whether any
	 * tree in trees returns true. It is possible for the tree to short-circuit
	 * its execution. If trees is empty then false is returned. Each call to
	 * {@link behaviortree.Tree#behave()} shuffles the order of its trees.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#anyMatch(java.util.function.Predicate)}
	 * and {@link java.util.Collections#shuffle(List)}.
	 * 
	 * <p>
	 * It should be emphasized that the evaluation order of its trees is
	 * randomized, so there are side effects! It is beneficial to expect
	 * unpredictability and avoid parallelism.
	 * 
	 * @param trees
	 *            A list of trees to be selected.
	 * @return a randomly selected tree
	 * @throws NullPointerException
	 *             if trees or any tree in trees is null
	 */
	public static Tree randomSelect(List<Tree> trees) {
		List<Tree> copy = copy(trees);

		// randomize copy each time
		return () -> {
			return select(random(copy).stream()).behave();
		};
	}

	/**
	 * This implementation wraps {@link #randomSelect(List)}.
	 * 
	 * @param trees
	 *            An array of trees to be selected.
	 * @return a randomly selected tree
	 */
	public static Tree randomSelect(Tree... trees) {
		return randomSelect(asList(trees));
	}

	/**
	 * Returns a randomly sequenced tree. A random tree evaluates its trees in
	 * the shuffled order for each test. A sequenced tree returns whether all
	 * trees in trees return true. It is possible for the tree to short-circuit
	 * its execution. If trees is empty then true is returned. Each call to
	 * {@link behaviortree.Tree#behave()} shuffles the order of its trees.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#allMatch(java.util.function.Predicate)}
	 * and {@link java.util.Collections#shuffle(List)}.
	 * 
	 * <p>
	 * It should be emphasized that the evaluation order of its trees is
	 * randomized, so there are side effects! It is beneficial to expect
	 * unpredictability and avoid parallelism.
	 * 
	 * @param trees
	 *            A list of trees to be sequenced.
	 * @return a randomly sequenced tree
	 * @throws NullPointerException
	 *             if trees or any tree in trees is null
	 */
	public static Tree randomSequence(List<Tree> trees) {
		List<Tree> copy = copy(trees);

		// randomize copy each time
		return () -> sequence(random(copy).stream()).behave();
	}

	/**
	 * This implementation wraps {@link #randomSequence(List)}.
	 * 
	 * @param trees
	 *            An array of trees to be sequenced.
	 * @return a randomly sequenced tree
	 */
	public static Tree randomSequence(Tree... trees) {
		return randomSequence(asList(trees));
	}

	/**
	 * Returns a parallelized selected tree. A selected tree returns whether any
	 * tree in trees returns true. It is possible for the tree to short-circuit
	 * its execution. If trees is empty then false is returned.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#anyMatch(java.util.function.Predicate)}.
	 * 
	 * @param trees
	 *            A list of trees to be selected.
	 * @return a parallelized selected tree
	 * @throws NullPointerException
	 *             if trees or any tree in trees is null
	 */
	public static Tree parallelSelect(List<Tree> trees) {
		List<Tree> copy = copy(trees);

		return () -> select(copy.stream().parallel()).behave();
	}

	/**
	 * This implementation wraps {@link #parallelSelect(List)}.
	 * 
	 * @param trees
	 *            An array of trees to be selected.
	 * @return a parallelized selected tree
	 */
	public static Tree parallelSelect(Tree... trees) {
		return parallelSelect(asList(trees));
	}

	/**
	 * Returns a parallelized sequenced tree. A sequenced tree returns whether
	 * all trees in trees return true. It is possible for the tree to
	 * short-circuit its execution. If trees is empty then true is returned.
	 * 
	 * <p>
	 * This implementation uses
	 * {@link java.util.stream.Stream#allMatch(java.util.function.Predicate)}.
	 * 
	 * @param trees
	 *            A list of trees to be sequenced.
	 * @return a parallelized sequenced tree
	 * @throws NullPointerException
	 *             if trees or any tree in trees is null
	 */
	public static Tree parallelSequence(List<Tree> trees) {
		List<Tree> copy = copy(trees);

		return () -> sequence(copy.stream().parallel()).behave();
	}

	/**
	 * This implementation wraps {@link #parallelSequence(List)}.
	 * 
	 * @param trees
	 *            An array of trees to be sequenced.
	 * @return a parallelized sequenced tree
	 */
	public static Tree parallelSequence(Tree... trees) {
		return parallelSequence(asList(trees));
	}

}
