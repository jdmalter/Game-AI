package screen;

import static steering.FixedRadiusNearestNeighbor.buckets;
import static utility.Random.nextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import drawing.Breadcrumb;
import drawing.Character;
import function.QuadConsumer;
import steering.Steering;
import utility.Pair;
import static utility.Streams.naturals;
import vector.Vector;

/**
 * A base class for movement. Additionally, this class handles adding
 * characters, bucketing characters, and dropping breadcrumbs.
 * 
 * <p>
 * After creating decision making, I realized there is more abstractions to be
 * removed from Screen. To make each class simpler, I split Screen into Screen
 * and Movement. Screen handles frames, elapsed time, and background drawing.
 * Movement handles draws breadcrumbs and characters.
 * 
 * @author Jacob Malter
 */
public abstract class Movement extends Screen {

	/** The maximum alpha value of 255. */
	private static final float MAX_ALPHA = 255;
	/**
	 * The number of frames between a breadcrumb being added an the breadcrumb
	 * appear on screen.
	 */
	private static final int DELAY = 3;
	/** The diameter of the drawn ellipses. */
	private static final float ELLIPSE_DIAMETER = 20f;

	/** The diameter of the drawn ellipses. */
	private final float diameter;

	/** A new draw function that has its pa set to the provided pa. */
	private final BiConsumer<Vector, Float> drawBreadcrumb = Breadcrumb.bind(this);
	/** A new draw function that has its pa set to the provided pa. */
	private final QuadConsumer<Integer, Float, Vector, Float> drawCharacter = Character.bind(this);

	/** Every steering of a character */
	private final List<Steering> steerings = new ArrayList<Steering>();
	/** Every color of a character */
	private final List<Integer> colors = new ArrayList<Integer>();

	/**
	 * Every breadcrumb being drawn on screen mapped to their current lifespan.
	 */
	private final Map<Vector, Integer> breadcrumbs = new HashMap<Vector, Integer>();

	/**
	 * Sets width, height, and diameter to defaults.
	 */
	public Movement() {
		this.diameter = ELLIPSE_DIAMETER;
	}

	/**
	 * Sets width, height, and diameter.
	 * 
	 * @param w
	 *            The width of the display window in units of pixels.
	 * @param h
	 *            The height of the display window in units of pixels.
	 * @param d
	 *            The diameter of the drawn ellipses.
	 */
	public Movement(int w, int h, float d) {
		super(w, h);
		this.diameter = d;
	}

	/**
	 * Adds a new character with a random color that tends to look pretty.
	 * 
	 * @param s
	 *            A steering data structure.
	 * @return Replaces the current character with the result of the function
	 *         that accepts the current character and produces a new character.
	 */
	public Consumer<Function<Steering, Steering>> addCharacter(Steering s) {
		int index = steerings.size();
		steerings.add(index, s);
		colors.add(index, nextColor());

		return (mapper) -> {
			Steering steering = steerings.get(index);
			steerings.set(index, mapper.apply(steering));
		};
	}

	/**
	 * Places every steering data structure into some bucket.
	 * 
	 * @param r
	 *            The maximum distance between two characters below which two
	 *            characters are considered neighbors.
	 * @return A map of position vectors to a collection (called a bucket) of
	 *         steering data structures of other steering data structures who
	 *         reside inside the bucket defined on an r by r coordinate grid.
	 */
	public Map<Vector, Collection<Steering>> bucketCharacters(float r) {
		return buckets(steerings, r);
	}

	/**
	 * Drops a breadcrumb whose lifespan is 0 at the position.
	 * 
	 * @param p
	 *            A position vector.
	 */
	public void dropBreadcrumb(Vector p) {
		breadcrumbs.put(p, -DELAY);
	}

	@Override
	public void draw() {
		super.draw();

		breadcrumbs.forEach((position, lifespan) -> {
			if (lifespan >= 0) {
				drawBreadcrumb.accept(position, MAX_ALPHA - lifespan);
			}
			breadcrumbs.put(position, lifespan + 1);
		});
		breadcrumbs.entrySet().stream().filter((entry) -> {
			return entry.getValue() > MAX_ALPHA;
		});

		naturals().limit(steerings.size()).map((index) -> {
			return new Pair<Steering, Integer>(steerings.get(index), colors.get(index));
		}).forEach((pair) -> {
			drawCharacter.accept(pair.b(), diameter, pair.a().position(), pair.a().angle());
		});
	}

}
