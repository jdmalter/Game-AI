package learning;

import static decisiontree.Tree.compose;
import static decisiontree.Tree.leaf;
import static function.HigherOrder.andThen;
import static function.HigherOrder.compose;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static utility.Comparators.ASCENDING;
import static utility.Mathf.log2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import decisiontree.Tree;
import utility.Pair;

/**
 * Provides decision tree learning.
 * 
 * @author Jacob Malter
 */
public final class Learning {

	/**
	 * Cannot be instantiated by users.
	 */
	private Learning() {

	}

	/**
	 * 
	 * @param list
	 *            A list of X
	 * @param mapper
	 *            a function that accepts one X and produces one Y.
	 * @return A new list of Y
	 */
	private static <X, Y> List<Y> map(List<? extends X> list, Function<? super X, ? extends Y> mapper) {
		return list.stream().map(mapper).collect(toList());
	}

	/**
	 * 
	 * @param xs
	 *            A distribution of some variable X.
	 * @return The expected value of the information contained in {@code xs}
	 */
	public static <X> float entropy(List<? extends X> xs) {
		// compute the number of xs
		float n = xs.size();

		// group samples by identity, compute entropy, and return sum
		return (float) xs.stream().collect(groupingBy(identity())).values().stream().mapToDouble((group) -> {
			// compute entropy with proportion of samples in group
			float probability = group.size() / n;
			return -probability * log2(probability);
		}).sum();
	}

	/**
	 * Pair should be interpreted as {@code X = x | Y = y}.
	 * 
	 * @param xs
	 *            A distribution of some variable X given some feature.
	 * @return The expected value of the information contained in {@code xs}
	 *         given some feature
	 */
	public static <X> float conditionalEntropy(List<Pair<? extends X, ?>> xs) {
		// compute the number of xs
		float n = xs.size();

		// group samples by features, compute conditional entropy given feature,
		// and return sum
		return (float) xs.stream().collect(groupingBy(Pair::b)).values().stream().mapToDouble((group) -> {
			// compute probability with proportion of samples in group
			float probability = group.size() / n;
			return probability * entropy(map(group, Pair::a));
		}).sum();
	}

	/**
	 * Pair should be interpreted as {@code X = x | Y = y}.
	 * 
	 * @param xs
	 *            A distribution of some variable X given some feature.
	 * @return The change in information entropy from some variable X to some
	 *         variable X given some feature
	 */
	public static <M, X> float informationGain(List<Pair<? extends X, ?>> xs) {
		// compute information entropy from some variable X
		float hLabel = entropy(map(xs, Pair::a));

		// compute information entropy from some variable X given some feature
		float hLabelFeature = conditionalEntropy(xs);

		// Compute chance in information entropy
		return hLabel - hLabelFeature;
	}

	/**
	 * Pair should be interpreted as {@code X = x | Y = y}.
	 * 
	 * @param label
	 *            Accepts one model and produces some variable X.
	 * @param feature
	 *            Accepts one model and produces some feature.
	 * @return A function that accepts one model and produces a pair of some
	 *         variable X and some feature.
	 */
	public static <M, X> Function<? super M, Pair<? extends X, ?>> map(Function<? super M, ? extends X> label,
			Function<? super M, ?> feature) {
		return (model) -> new Pair<X, Object>(label.apply(model), feature.apply(model));
	}

	/**
	 * 
	 * @param features
	 *            A set of functions which accepts one model and produces some
	 *            feature.
	 * @param samples
	 *            A list of models.
	 * @param label
	 *            Accepts one model and produces some variable X.
	 * @return A feature from features that maximizes information gain on the
	 *         provided samples.
	 */
	private static <M, X> Optional<Function<? super M, ?>> bestFeature(Set<Function<? super M, ?>> features,
			List<? extends M> samples, Function<? super M, ? extends X> label) {
		// bind samples and label
		Function<Function<? super M, ?>, Float> informationGain = (feature) -> {
			return informationGain(map(samples, map(label, feature)));
		};

		// maximize information gain
		return features.stream().max(compose(ASCENDING, informationGain)::apply);
	}

	/**
	 * 
	 * @param samples
	 *            A list of models.
	 * @param label
	 *            Accepts one model and produces some variable X.
	 * @param features
	 *            A set of functions which accepts one model and produces some
	 *            feature.
	 * @param supplier
	 *            Provides models for the decision tree to evaluate.
	 * @return A decision tree that produces a list of labels based on the model
	 *         supplied by {@code supplier} during its decision.
	 */
	public static <M, X> Tree<List<X>> build(List<? extends M> samples, Function<? super M, ? extends X> label,
			Set<Function<? super M, ?>> features, Supplier<? extends M> supplier) {
		// find the best feature
		return bestFeature(features, samples, label).filter((best) -> {
			// if there is more than one distinct label
			return map(samples, label).stream().distinct().limit(2).count() > 1;
		}).map((best) -> {
			// remove best feature from features to prevent overflow
			features.remove(best);

			// map values of feature to subtrees
			Map<?, Tree<List<X>>> subtrees = samples.stream().collect(groupingBy(best)).entrySet().stream()
					.collect(toMap(Map.Entry::getKey, (entry) -> {
						return build(entry.getValue(), label, features, supplier);
					}));

			// create tree where value of feature determines subtree
			return compose(andThen(supplier, best::apply), subtrees, ArrayList<X>::new);
		}).orElse(leaf(map(samples, label)));
	}

}
