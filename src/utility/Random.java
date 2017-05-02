package utility;

/**
 * Provides random float, random binomial, and random color functions.
 * 
 * @author Jacob Malter
 */
public final class Random {

	/** How many bits are in one byte. */
	private static final int BITS_PER_BYTE = 8;
	/** The mean value for a rgb component of random color. */
	private static final byte MEAN = 64;
	/**
	 * The farthest value from the mean for a rgb component of random color.
	 */
	private static final byte RANGE = 64;

	/**
	 * Cannot be instantiated by users.
	 */
	private Random() {

	}

	/**
	 * This implementation uses {@link java.lang.Math#random()}.
	 * 
	 * @return a pseudorandom float greater than or equal to 0.0 and less than
	 *         1.0.
	 */
	public static float nextFloat() {
		return (float) Math.random();
	}

	/**
	 * This implementation uses {@link java.lang.Math#random()}.
	 * 
	 * @return A random sample from a continuous binomial distribution between
	 *         -1 and 1.
	 */
	public static float nextBinomial() {
		return nextFloat() - nextFloat();
	}

	/**
	 * This implementation uses {@link Random#nextBinomial()}.
	 * 
	 * @param mean
	 *            The mean value of a random binomial.
	 * @param range
	 *            The farthest value from the mean for a random binomial.
	 * @return A random sample from a continuous binomial distribution between
	 *         mean - range and mean + range.
	 */
	public static long nextBinomial(long mean, long range) {
		return (long) (mean + nextBinomial() * range);
	}

	/**
	 * This implementation uses {@link java.lang.Math#random()}. Complete credit
	 * to <a href=
	 * "http://stackoverflow.com/questions/43044/algorithm-to-randomly-generate-an-aesthetically-pleasing-color-palette"
	 * >David Crow</a> for the inspiration.
	 * 
	 * @return A random hexadecimal alpha, red, blue, green color that tends to
	 *         look pretty.
	 */
	public static int nextColor() {
		int bits = -1;
		bits <<= BITS_PER_BYTE;
		bits |= nextBinomial(3 * MEAN / 2, 3 * RANGE / 2);
		bits <<= BITS_PER_BYTE;
		bits |= nextBinomial(MEAN, RANGE);
		bits <<= BITS_PER_BYTE;
		bits |= nextBinomial(MEAN / 4, RANGE / 4);

		int offset = (int) (3 * nextFloat()) * BITS_PER_BYTE;
		bits = (int) (bits | nextBinomial(MEAN, (RANGE * 2)) << offset);
		return bits;
	}

}
