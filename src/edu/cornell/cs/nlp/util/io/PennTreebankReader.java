package edu.cornell.cs.nlp.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.cornell.cs.nlp.util.ling.ConcatenationIterator;
import edu.cornell.cs.nlp.util.ling.Tree;
import edu.cornell.cs.nlp.util.ling.Trees;

/**
 * @author Dan Klein, Berkeley
 */
public class PennTreebankReader {

	public static void main(String[] args) {
		final Collection<Tree<String>> trees = readTrees(args[0]);
		for (Tree<String> tree : trees) {
			tree = new Trees.StandardTreeNormalizer().transformTree(tree);
			System.out.println(Trees.PennTreeRenderer.render(tree));
		}
	}

	public static Collection<Tree<String>> readTrees(String path) {
		return readTrees(path, -1, Integer.MAX_VALUE);
	}

	public static Collection<Tree<String>> readTrees(String path,
			int lowFileNum, int highFileNumber) {
		return new TreeCollection(path, lowFileNum, highFileNumber);
	}

	static class TreeCollection extends AbstractCollection<Tree<String>> {

		List<File> files;

		public TreeCollection(String path, int lowFileNum, int highFileNum) {
			final FileFilter fileFilter = new NumberRangeFileFilter(".mrg",
					lowFileNum, highFileNum, true);
			this.files = getFilesUnder(path, fileFilter);
		}

		@Override
		public Iterator<Tree<String>> iterator() {
			return new ConcatenationIterator<Tree<String>>(
					new TreeIteratorIterator(files));
		}

		@Override
		public int size() {
			int size = 0;
			final Iterator<Tree<String>> i = iterator();
			while (i.hasNext()) {
				size++;
				i.next();
			}
			return size;
		}

		private void addFilesUnder(File root, List<File> filesToAdd,
				FileFilter fileFilter) {
			if (!fileFilter.accept(root)) {
				return;
			}
			if (root.isFile()) {
				filesToAdd.add(root);
				return;
			}
			if (root.isDirectory()) {
				final File[] children = root.listFiles();
				for (int i = 0; i < children.length; i++) {
					final File child = children[i];
					addFilesUnder(child, filesToAdd, fileFilter);
				}
			}
		}

		private List<File> getFilesUnder(String path, FileFilter fileFilter) {
			final File root = new File(path);
			final List<File> pathFiles = new ArrayList<File>();
			addFilesUnder(root, pathFiles, fileFilter);
			return pathFiles;
		}

		static class TreeIteratorIterator
				implements Iterator<Iterator<Tree<String>>> {
			Iterator<File>			fileIterator;
			Iterator<Tree<String>>	nextTreeIterator;

			TreeIteratorIterator(List<File> files) {
				this.fileIterator = files.iterator();
				advance();
			}

			@Override
			public boolean hasNext() {
				return nextTreeIterator != null;
			}

			@Override
			public Iterator<Tree<String>> next() {
				final Iterator<Tree<String>> currentTreeIterator = nextTreeIterator;
				advance();
				return currentTreeIterator;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private void advance() {
				nextTreeIterator = null;
				while (nextTreeIterator == null && fileIterator.hasNext()) {
					try {
						final File file = fileIterator.next();
						nextTreeIterator = new Trees.PennTreeReader(
								new BufferedReader(new FileReader(file)));
					} catch (final FileNotFoundException e) {
					}
				}
			}
		}
	}

}
