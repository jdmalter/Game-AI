package screenpathfinding;

import static vector.Factory.create;

import java.util.Optional;
import java.util.function.BiFunction;

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

	private BiFunction<Target, Target, Optional<Sequence<Target>>> pathFind;

	@Override
	public void settings() {
		super.settings();
		pathFind = pathFind(paths(replacer()));
	}

	@Override
	public void mousePressed() {
		cycle();
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
