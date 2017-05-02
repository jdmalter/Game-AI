package learning;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import screendecisionmaking.BehaviorTree.Action;

/**
 * A basic, immutable model for a regression.
 * 
 * @author Jacob Malter
 */
public final class Model {

	/** Whether kill condition is satisfied */
	private final boolean kill;
	/** Whether pursue condition is satisfied */
	private final boolean pursue;
	/** Whether sequence is completely satisfied */
	private final boolean satisfied;
	/** The previous action taken on the environment. */
	private final Action action;

	/** A map of the name of enum constants to the respective enum constant. */
	private static final Map<String, Action> actions = new HashMap<String, Action>();

	{
		for (Action value : Action.values()) {
			String key = value.toString();
			actions.put(key, value);
		}
	}

	/**
	 * @param field
	 *            A string representation of a field.
	 * @return Whether the field reads 1 or 0.
	 * @throws IllegalArgumentException
	 *             if field is not 1 or 0
	 */
	private static boolean match(String field) {
		if (Objects.equals(field, "1")) {
			return true;
		} else if (Objects.equals(field, "0")) {
			return false;
		} else {
			throw new IllegalArgumentException("field must be 1 or 0. Instead, field is " + field);
		}
	}

	/**
	 * 
	 * @param kill
	 *            Whether kill condition is satisfied
	 * @param pursue
	 *            Whether pursue condition is satisfied
	 * @param satisfied
	 *            Whether sequence is completely satisfied
	 * @param action
	 *            The previous action taken on the environment.
	 * @throws NullPointerException
	 *             if action is null
	 */
	public Model(boolean kill, boolean pursue, boolean satisfied, Action action) {
		this.kill = kill;
		this.pursue = pursue;
		this.satisfied = satisfied;
		this.action = requireNonNull(action);
	}

	/**
	 * 
	 * @param string
	 *            a string representation of the model
	 * @throws NullPointerException
	 *             if string is null
	 * @throws IllegalArgumentException
	 *             if string is not formatted correctly
	 */
	public Model(String string) {
		String[] fields = requireNonNull(string).split(",");
		kill = match(fields[0]);
		pursue = match(fields[1]);
		satisfied = match(fields[2]);
		action = actions.get(fields[3]);

		if (action == null) {
			throw new IllegalArgumentException("action must be string representation of Action");
		}
	}

	/**
	 * @return Whether kill condition is satisfied
	 */
	public boolean kill() {
		return kill;
	}

	/**
	 * @return Whether pursue condition is satisfied
	 */
	public boolean pursue() {
		return pursue;
	}

	/**
	 * @return Whether sequence is completely satisfied
	 */
	public boolean satisfied() {
		return satisfied;
	}

	/**
	 * @return The previous action taken on the environment.
	 */
	public Action action() {
		return action;
	}

	@Override
	public String toString() {
		return (kill ? 1 : 0) + "," + (pursue ? 1 : 0) + "," + (satisfied ? 1 : 0) + "," + action;
	}

}
