package screenpathfinding;

import static drawing.Path.bind;
import static java.util.function.Function.identity;
import static vector.Factory.create;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import screen.IndoorEnvironment;
import sequence.Sequence;
import target.Target;

/**
 * Runs the A* pathfinding algorithm and a pathfollowing algorithm.
 * 
 * <p>
 * After some consideration, null objects are treatly differently. Changed the
 * documentation from a small message in the description to a throws annotiation
 * to grab more attention. Removed the string message from the actual null
 * pointer message because it was redundant.
 * 
 * <p>
 * After {@code DecisionMaking} added {@link #sense()}, {@link #think()},
 * {@link #act(Target)}, and {@link #quantize()} path finding behaviors in this
 * class were appropriately split among those methods.
 * 
 * @author Jacob Malter
 */
public class PathFinding extends IndoorEnvironment<Target> {

	/** A new draw function that has its pa set to the provided pa. */
	private final Consumer<Sequence<Target>> drawPath = bind(this);

	/**
	 * replaces the current path with the result of the function that accepts
	 * the current path and produces and returns a vew of a new path with
	 * respect to some replacer
	 */
	private Function<Function<Sequence<Target>, Sequence<Target>>, Sequence<Target>> path;
	/**
	 * A bifunction which takes the initial state that the character starts in
	 * and the goal state that the character ends in, produces an optional
	 * possibly empty sequence of targets that reaches the goal target from the
	 * initial target, and update pathReplacer and targetReplacer with
	 * respective new values.
	 */
	private BiFunction<Target, Target, Optional<Sequence<Target>>> pathFind;

	@Override
	public void settings() {
		super.settings();
		path = paths(replacer());
		pathFind = pathFind(path);
	}

	@Override
	public void mousePressed() {
		cycle();
	}

	@Override
	public void draw() {
		super.draw();
		drawPath.accept(path.apply(identity()));
	}

	@Override
	public void sense() {
		// parent PApplet senses mouseX and mouseY for us!
	}

	@Override
	public Target think() {
		return quantize(create(mouseX, mouseY));
	}

	@Override
	public void act(Target action) {
		pathFind.apply(quantize(), action);
	}

}
