package behaviortree;

/**
 * Represents a behavior tree.
 * 
 * @author Jacob Malter
 */
@FunctionalInterface
public interface Tree {

	/**
	 * @return true or false
	 */
	boolean behave();

}
