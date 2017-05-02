package function;

import static java.util.Objects.requireNonNull;

/**
 * Represents an operation that accepts no arguments and returns no result.
 * Unlike most other functional interfaces, {@code Procedure} is expected to
 * operate via side-effects.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #call()}.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * <p>
 * Recently, moved some functional interfaces into new function package because
 * it is easier to search for functions in a package named function rather than
 * utility.
 * 
 * @author Jacob Malter
 */
@FunctionalInterface
public interface Procedure {

	/**
	 * Performs this operation.
	 */
	void call();

	/**
	 * Returns a composed {@code Procedure} that performs, in sequence, the
	 * {@code before} operation followed by this operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the
	 * composed operation. If performing the {@code before} operation throws an
	 * exception, this operation will not be performed.
	 *
	 * @param before
	 *            the operation to perform before this operation
	 * @return a composed {@code Procedure} that performs in sequence the
	 *         {@code before} operation followed by this operation
	 * @throws NullPointerException
	 *             if {@code before} is null
	 */
	default Procedure compose(Procedure before) {
		requireNonNull(before);

		return () -> {
			before.call();
			call();
		};
	}

	/**
	 * Returns a composed {@code Procedure} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the
	 * composed operation. If performing this operation throws an exception, the
	 * {@code after} operation will not be performed.
	 *
	 * @param after
	 *            the operation to perform after this operation
	 * @return a composed {@code Procedure} that performs in sequence this
	 *         operation followed by the {@code after} operation
	 * @throws NullPointerException
	 *             if {@code after} is null
	 */
	default Procedure andThen(Procedure after) {
		requireNonNull(after);

		return () -> {
			call();
			after.call();
		};
	}

}
