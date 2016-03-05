package edu.cornell.cs.nlp.util.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for getting files recursively, with a filter.
 *
 * @author Dan Klein, Berkeley
 */
public class IOUtils {
	public static List<File> getFilesUnder(String path, FileFilter fileFilter) {
		final File root = new File(path);
		final List<File> files = new ArrayList<File>();
		addFilesUnder(root, files, fileFilter);
		return files;
	}

	private static void addFilesUnder(File root, List<File> files,
			FileFilter fileFilter) {
		if (!fileFilter.accept(root)) {
			return;
		}
		if (root.isFile()) {
			files.add(root);
			return;
		}
		if (root.isDirectory()) {
			final File[] children = root.listFiles();
			for (int i = 0; i < children.length; i++) {
				final File child = children[i];
				addFilesUnder(child, files, fileFilter);
			}
		}
	}

}
