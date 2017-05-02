package learning;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static learning.Learning.build;
import static learning.Recording.PATH;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import decisiontree.Tree;
import screendecisionmaking.BehaviorTree.Action;

public final class Models {

	/**
	 * Cannot be instantiated by users.
	 */
	private Models() {

	}

	/**
	 * @param path
	 *            the name of the file to read from
	 * @return a new {@code List} of model's for a regression mapped from lines
	 *         read from the buffering character-input stream from a file reader
	 *         named by path
	 * @throws FileNotFoundException
	 *             if the named file does not exist, is a directory rather than
	 *             a regular file, or for some other reason cannot be opened for
	 *             reading.
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static List<Model> models(String path) throws FileNotFoundException, IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
			return reader.lines().skip(1).map(Model::new).collect(toList());
		}
	}

	/**
	 * 
	 * @param supplier
	 *            Provides models for the decision tree to evaluate.
	 * @return A decision tree that produces a list of labels based on the model
	 *         supplied by supplier during its decision.
	 * @throws FileNotFoundException
	 *             if the named file does not exist, is a directory rather than
	 *             a regular file, or for some other reason cannot be opened for
	 *             reading.
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public static Tree<List<Action>> train(Supplier<Model> supplier) throws FileNotFoundException, IOException {
		return build(models(PATH), Model::action,
				new HashSet<Function<? super Model, ?>>(asList(Model::kill, Model::pursue, Model::satisfied)),
				supplier);
	}

}
