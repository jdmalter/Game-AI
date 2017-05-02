package decisiontree;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents a decision tree.
 * 
 * @author Jacob Malter
 *
 * @param <A>
 *            Any action.
 */
@FunctionalInterface
public interface Tree<A> {

	/**
	 * @return a decision
	 */
	A decide();

	/**
	 * Returns a composed tree that first evaluates {@code sentence} to decide
	 * which subtree to evaluated. If {@code sentence} returns true, then
	 * trueTree is evaluated; otherwise, falseTree is evaluated. If evaluation
	 * of either subtree throws an exception, it is relayed to the caller of the
	 * composed tree.
	 * 
	 * @param sentence
	 *            chooses which subtree to evaluated
	 * @param trueTree
	 *            the tree to call when {@code sentence} returns true
	 * @param falseTree
	 *            the tree to call when {@code sentence} returns false
	 * @return a composed tree that first evaluates {@code sentence} to decide
	 *         which subtree to evaluated
	 * @throws NullPointerException
	 *             if sentence, trueTree, or falseTree is null
	 */
	static <A> Tree<A> composeBinary(Supplier<Boolean> sentence, Tree<? extends A> trueTree,
			Tree<? extends A> falseTree) {
		requireNonNull(sentence);
		requireNonNull(trueTree);
		requireNonNull(falseTree);

		return () -> (sentence.get() ? trueTree : falseTree).decide();
	}

	/**
	 * Returns a composed tree that first evaluates {@code assignment} to decide
	 * which subtree from {@code subtrees} to evaluate. If evaluation of any
	 * subtree throws an exception, it is relayed to the caller of the composed
	 * tree.
	 * 
	 * @param assignment
	 *            chooses which subtree to be evaluated
	 * @param subtrees
	 *            defines the tree to call based on {@code assignment}
	 * @param defaultSubtree
	 *            the tree to call when {@code subtrees} does not contain an
	 *            assigned value
	 * @return a composed tree that first evaluates {@code assignment} to decide
	 *         which subtree from {@code subtrees} to evaluate
	 * @throws NullPointerException
	 *             if assignment, subtrees, any value in subtrees, or
	 *             defaultSubtree is null
	 */
	static <A> Tree<A> compose(Supplier<?> assignment, Map<?, ? extends Tree<? extends A>> subtrees,
			Tree<? extends A> defaultSubtree) {
		requireNonNull(assignment);
		requireNonNull(subtrees);
		requireNonNull(defaultSubtree);

		// create defensive copy
		Map<?, Tree<? extends A>> copy = subtrees.entrySet().stream().map((entry) -> {
			// check for null value
			requireNonNull(entry.getValue());
			return entry;
		}).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		return () -> copy.getOrDefault(assignment.get(), defaultSubtree).decide();
	}

	/**
	 * Returns a simple tree with one node that evaluates to the given action.
	 * 
	 * @param action
	 *            a decision
	 * @return a simple tree with one node that evaluates to the given action
	 */
	static <A> Tree<A> leaf(A action) {
		return () -> action;
	}

}
