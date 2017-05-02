package steering;

import static java.util.Arrays.asList;
import static vector.Arithmetic.add;
import static vector.Arithmetic.floor;
import static vector.Arithmetic.subtract;
import static vector.Factory.I;
import static vector.Factory.J;
import static vector.Factory.K;
import static vector.Property.magnitude;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import vector.Vector;

/**
 * Provides bucketing and neighbor functions.
 * 
 * <p>
 * Major credit to
 * <a href= "http://efekarakus.github.io/fixed-radius-near-neighbor/">Fixed
 * Radius Near Neighbor</a> for guidance.
 * 
 * <p>
 * After learning about
 * {@link java.util.stream.Stream#collect(java.util.function.Supplier, java.util.function.BiConsumer, java.util.function.BiConsumer)},
 * I replaced reductions with collections for conciseness.
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
public final class FixedRadiusNearestNeighbor {

	/**
	 * Cannot be instantiated by users.
	 */
	private FixedRadiusNearestNeighbor() {

	}

	/**
	 * @param ls
	 *            A list of steering data structures.
	 * @param r
	 *            The radius of locality.
	 * @return A map of position vectors to a collection (called a bucket) of
	 *         steering data structures of other steering data structures who
	 *         reside inside the bucket defined on an r by r coordinate grid.
	 */
	public static Map<Vector, Collection<Steering>> buckets(List<Steering> ls, float r) {
		return ls.parallelStream().collect(ConcurrentHashMap<Vector, Collection<Steering>>::new, (map, character) -> {

			Vector bucket = floor(character.position(), r);
			if (!map.containsKey(bucket)) {
				map.put(bucket, new ConcurrentLinkedQueue<Steering>());
			}
			map.get(bucket).add(character);

		}, ConcurrentHashMap::putAll);
	}

	/**
	 * @param b
	 *            A map of position vectors to a collection (called a bucket) of
	 *            steering data structures of other steering data structures who
	 *            reside inside the bucket defined on an r by r coordinate grid.
	 * @param s
	 *            A steering data structure.
	 * @param r
	 *            The radius of locality.
	 * @return A collection of steering data structures within the radius of
	 *         locality of the steering data structure.
	 */
	public static Collection<Steering> neighbors(Map<Vector, Collection<Steering>> b, Steering s, float r) {
		Vector characterBucket = floor(s.position(), r);
		Collection<Vector> buckets = asList(characterBucket, add(characterBucket, I),
				add(characterBucket, J), add(characterBucket, K), subtract(characterBucket, I),
				subtract(characterBucket, J), subtract(characterBucket, K));

		return buckets.parallelStream().collect(ConcurrentLinkedQueue<Steering>::new, (collection, bucket) -> {
			if (b.containsKey(bucket)) {
				b.get(bucket).parallelStream().forEach((target) -> {
					if (s != target && magnitude(subtract(target.position(), s.position())) < r) {
						collection.add(target);
					}
				});
			}
		}, Collection::addAll);
	}

}
