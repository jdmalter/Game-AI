package learning;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import processing.core.PApplet;
import screendecisionmaking.BehaviorTree;

/**
 * Records the behavior tree algorithm.
 * 
 * @author Jacob Malter
 */
public class Recording extends BehaviorTree {

	/**
	 * The name of the file to use as the destination of this writer. If the
	 * file exists then it will be truncated to zero size; otherwise, a new file
	 * will be created. The output will be written to the file and is buffered.
	 */
	public static final String PATH = System.getProperty("user.dir") + "/output.csv";
	/** The name of a supported charset */
	public static final String CSN = "UTF-8";

	/** Writes regression output to a file. */
	private final PrintWriter writer;

	/**
	 * Creates a new printwriter with path and csn. Listens to regression output
	 * with writer.
	 * 
	 * @throws FileNotFoundException
	 *             If {@value #PATH} does not denote an existing, writable
	 *             regular file and a new regular file of that name cannot be
	 *             created, or if some other error occurs while opening or
	 *             creating the file
	 * @throws UnsupportedEncodingException
	 *             If {@value #CSN} is not supported
	 */
	public Recording() throws FileNotFoundException, UnsupportedEncodingException {
		// construct a writer
		writer = new PrintWriter(PATH, CSN);

		// use the writer
		writer.println("kill,pursue,satisfied,action");
		listen(writer::println);
	}

	@Override
	public void exit() {
		super.exit();

		// free up that file for other uses
		writer.close();

		// close the program
		System.exit(0);
	}

	/**
	 * Runs PApplet main on this class.
	 * 
	 * @param args
	 *            Command line arguments are not used in this program.
	 */
	public static void main(String[] args) {
		PApplet.main(Recording.class);
	}

}
