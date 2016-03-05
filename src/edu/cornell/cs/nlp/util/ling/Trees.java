package edu.cornell.cs.nlp.util.ling;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tools for displaying, reading, and modifying trees.
 *
 * @author Dan Klein, Berkeley
 */
public class Trees {

	public static void main(String[] args) {
		final PennTreeReader reader = new PennTreeReader(new StringReader(
				"((S (NP (DT the) (JJ quick) (JJ brown) (NN fox)) (VP (VBD jumped) (PP (IN over) (NP (DT the) (JJ lazy) (NN dog)))) (. .)))"));
		final Tree<String> tree = reader.next();
		System.out.println(PennTreeRenderer.render(tree));
		System.out.println(tree);
	}

	/**
	 * Prunes out all nodes which match the provided filter (and nodes which
	 * dominate only pruned nodes).
	 *
	 * @param tree
	 * @param filter
	 * @return
	 */
	public static <L> Tree<L> pruneNodes(Tree<L> tree, Filter<L> filter) {
		return pruneNodesHelper(tree, filter);
	}

	/**
	 * Splices out all nodes which match the provided filter.
	 *
	 * @param tree
	 * @param filter
	 * @return
	 */
	public static <L> Tree<L> spliceNodes(Tree<L> tree, Filter<L> filter) {
		final List<Tree<L>> rootList = spliceNodesHelper(tree, filter);
		if (rootList.size() > 1) {
			throw new IllegalArgumentException(
					"spliceNodes: no unique root after splicing");
		}
		if (rootList.size() < 1) {
			return null;
		}
		return rootList.get(0);
	}

	/**
	 * Recursively transforms each node of <code>tree</code> parameter according
	 * to the passed in
	 * <code>LabelFactory</code>. None of the structure of the <code>Tree</code>
	 * is altered only
	 * the information stored in the Label at each node.
	 *
	 * @param <S>
	 *            Original Tree label type
	 * @param <T>
	 *            Output tree label type
	 * @param tree
	 * @param labelTransformer
	 * @return Tree with transformed labels according to
	 *         <code>labelTransformer</code>
	 */
	public static <S, T> Tree<T> transformTreeLabels(Tree<S> tree,
			LabelFactory<S, T> labelFactory) {
		final T newLabel = labelFactory.newLabel(tree);
		final List<Tree<T>> newChildren = new ArrayList<Tree<T>>();
		for (final Tree<S> node : tree.getChildren()) {
			final Tree<T> newNode = transformTreeLabels(node, labelFactory);
			newChildren.add(newNode);
		}
		return new Tree<T>(newLabel, newChildren);
	}

	private static <L> Tree<L> pruneNodesHelper(Tree<L> tree,
			Filter<L> filter) {
		if (filter.accept(tree.getLabel())) {
			return null;
		}
		final List<Tree<L>> prunedChildren = new ArrayList<Tree<L>>();
		for (final Tree<L> child : tree.getChildren()) {
			final Tree<L> prunedChild = pruneNodesHelper(child, filter);
			if (prunedChild != null) {
				prunedChildren.add(prunedChild);
			}
		}
		if (prunedChildren.isEmpty() && !tree.isLeaf()) {
			return null;
		}
		return new Tree<L>(tree.getLabel(), prunedChildren);
	}

	private static <L> List<Tree<L>> spliceNodesHelper(Tree<L> tree,
			Filter<L> filter) {
		final List<Tree<L>> splicedChildren = new ArrayList<Tree<L>>();
		for (final Tree<L> child : tree.getChildren()) {
			final List<Tree<L>> splicedChildList = spliceNodesHelper(child,
					filter);
			splicedChildren.addAll(splicedChildList);
		}
		if (filter.accept(tree.getLabel())) {
			return splicedChildren;
		}
		return Collections
				.singletonList(new Tree<L>(tree.getLabel(), splicedChildren));
	}

	public static class EmptyNodeStripper implements TreeTransformer<String> {
		@Override
		public Tree<String> transformTree(Tree<String> tree) {
			final String label = tree.getLabel();
			if (label.equals("-NONE-")) {
				return null;
			}
			if (tree.isLeaf()) {
				return new Tree<String>(label);
			}
			final List<Tree<String>> children = tree.getChildren();
			final List<Tree<String>> transformedChildren = new ArrayList<Tree<String>>();
			for (final Tree<String> child : children) {
				final Tree<String> transformedChild = transformTree(child);
				if (transformedChild != null) {
					transformedChildren.add(transformedChild);
				}
			}
			if (transformedChildren.size() == 0) {
				return null;
			}
			return new Tree<String>(label, transformedChildren);
		}
	}

	public static class FunctionNodeStripper
			implements TreeTransformer<String> {

		public static String transformLabel(Tree<String> tree) {
			String transformedLabel = tree.getLabel();
			int cutIndex = transformedLabel.indexOf('-');
			final int cutIndex2 = transformedLabel.indexOf('=');
			if (cutIndex2 > 0 && (cutIndex2 < cutIndex || cutIndex == -1)) {
				cutIndex = cutIndex2;
			}
			if (cutIndex > 0 && !tree.isLeaf()) {
				transformedLabel = new String(
						transformedLabel.substring(0, cutIndex));
			}
			return transformedLabel;
		}

		@Override
		public Tree<String> transformTree(Tree<String> tree) {
			final String transformedLabel = transformLabel(tree);
			if (tree.isLeaf()) {
				return new Tree<String>(transformedLabel);
			}
			final List<Tree<String>> transformedChildren = new ArrayList<Tree<String>>();
			for (final Tree<String> child : tree.getChildren()) {
				transformedChildren.add(transformTree(child));
			}
			return new Tree<String>(transformedLabel, transformedChildren);
		}
	}

	/**
	 * Generate a Label from a <code>Tree</code>.
	 *
	 * Interface for generating labels of a tree from a given node with a
	 * different label type.
	 * Note that the node with the old label is passed in so that the new label
	 * <code>T</code> can
	 * have features of the original tree. See <code>transformTreeLabels</code>
	 * for
	 * a function which recursively calls the label transformer on each node of
	 * a tree.
	 *
	 * @param <S>
	 *            Original label type
	 * @param <T>
	 *            Output label type
	 */
	public static interface LabelFactory<S, T> {
		T newLabel(Tree<S> node);
	}

	public static class PennTreeReader implements Iterator<Tree<String>> {
		public static String	ROOT_LABEL	= "ROOT";

		PushbackReader			in;
		Tree<String>			nextTree;

		public PennTreeReader(Reader in) {
			this.in = new PushbackReader(in);
			nextTree = readRootTree();
		}

		private static boolean isLeftParen(int ch) {
			return ch == '(';
		}

		private static boolean isRightParen(int ch) {
			return ch == ')';
		}

		private static boolean isWhiteSpace(int ch) {
			return ch == ' ' || ch == '\t' || ch == '\f' || ch == '\r'
					|| ch == '\n';
		}

		@Override
		public boolean hasNext() {
			return nextTree != null;
		}

		@Override
		public Tree<String> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			final Tree<String> tree = nextTree;
			nextTree = readRootTree();
			return tree;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private int peek() throws IOException {
			final int ch = in.read();
			in.unread(ch);
			return ch;
		}

		private List<Tree<String>> readChildList() throws IOException {
			final List<Tree<String>> children = new ArrayList<Tree<String>>();
			readWhiteSpace();
			while (!isRightParen(peek())) {
				children.add(readTree(false));
				readWhiteSpace();
			}
			return children;
		}

		private List<Tree<String>> readChildren() throws IOException {
			readWhiteSpace();
			if (!isLeftParen(peek())) {
				return Collections.singletonList(readLeaf());
			}
			return readChildList();
		}

		private String readLabel() throws IOException {
			readWhiteSpace();
			return readText();
		}

		private Tree<String> readLeaf() throws IOException {
			final String label = readText();
			return new Tree<String>(label);
		}

		private void readLeftParen() throws IOException {
			// System.out.println("Read left.");
			readWhiteSpace();
			final int ch = in.read();
			if (!isLeftParen(ch)) {
				throw new RuntimeException(
						"Format error reading tree with character: ("
								+ Character.valueOf((char) ch) + ")");
			}
		}

		private void readRightParen() throws IOException {
			// System.out.println("Read right.");
			readWhiteSpace();
			final int ch = in.read();
			if (!isRightParen(ch)) {
				throw new RuntimeException("Format error reading tree.");
			}
		}

		private Tree<String> readRootTree() {
			try {
				readWhiteSpace();
				if (!isLeftParen(peek())) {
					return null;
				}
				return readTree(true);
			} catch (final IOException e) {
				throw new RuntimeException("Error reading tree.");
			}
		}

		private String readText() throws IOException {
			final StringBuilder sb = new StringBuilder();
			int ch = in.read();
			while (!isWhiteSpace(ch) && !isLeftParen(ch) && !isRightParen(ch)) {
				sb.append((char) ch);
				ch = in.read();
			}
			in.unread(ch);
			// System.out.println("Read text: ["+sb+"]");
			return sb.toString().intern();
		}

		private Tree<String> readTree(boolean isRoot) throws IOException {
			readLeftParen();
			String label = readLabel();
			if (label.length() == 0 && isRoot) {
				label = ROOT_LABEL;
			}
			final List<Tree<String>> children = readChildren();
			readRightParen();
			return new Tree<String>(label, children);
		}

		private void readWhiteSpace() throws IOException {
			int ch = in.read();
			while (isWhiteSpace(ch)) {
				ch = in.read();
			}
			in.unread(ch);
		}
	}

	/**
	 * Renderer for pretty-printing trees according to the Penn Treebank
	 * indenting
	 * guidelines (mutliline). Adapted from code originally written by Dan Klein
	 * and modified by Chris Manning.
	 */
	public static class PennTreeRenderer {

		/**
		 * Print the tree as done in Penn Treebank merged files. The formatting
		 * should be exactly the same, but we don't print the trailing
		 * whitespace
		 * found in Penn Treebank trees. The basic deviation from a bracketed
		 * indented tree is to in general collapse the printing of adjacent
		 * preterminals onto one line of tags and words. Additional complexities
		 * are that conjunctions (tag CC) are not collapsed in this way, and
		 * that
		 * the unlabeled outer brackets are collapsed onto the same line as the
		 * next
		 * bracket down.
		 */
		public static <L> String render(Tree<L> tree) {
			final StringBuilder sb = new StringBuilder();
			renderTree(tree, 0, false, false, false, true, sb);
			sb.append('\n');
			return sb.toString();
		}

		private static <L> void renderChildren(List<Tree<L>> children,
				int indent, boolean parentLabelNull, StringBuilder sb) {
			boolean firstSibling = true;
			boolean leftSibIsPreTerm = true; // counts as true at beginning
			for (final Tree<L> child : children) {
				renderTree(child, indent, parentLabelNull, firstSibling,
						leftSibIsPreTerm, false, sb);
				leftSibIsPreTerm = child.isPreTerminal();
				// CC is a special case
				if (child.getLabel() != null
						&& child.getLabel().toString().startsWith("CC")) {
					leftSibIsPreTerm = false;
				}
				firstSibling = false;
			}
		}

		private static <L> void renderFlat(Tree<L> tree, StringBuilder sb) {
			if (tree.isLeaf()) {
				sb.append(tree.getLabel().toString());
				return;
			}
			sb.append('(');
			sb.append(tree.getLabel().toString());
			sb.append(' ');
			sb.append(tree.getChildren().get(0).getLabel().toString());
			sb.append(')');
		}

		/**
		 * Display a node, implementing Penn Treebank style layout
		 */
		private static <L> void renderTree(Tree<L> tree, int indent,
				boolean parentLabelNull, boolean firstSibling,
				boolean leftSiblingPreTerminal, boolean topLevel,
				StringBuilder sb) {
			// the condition for staying on the same line in Penn Treebank
			final boolean suppressIndent = parentLabelNull
					|| firstSibling && tree.isPreTerminal()
					|| leftSiblingPreTerminal && tree.isPreTerminal()
							&& (tree.getLabel() == null || !tree.getLabel()
									.toString().startsWith("CC"));
			if (suppressIndent) {
				sb.append(' ');
			} else {
				if (!topLevel) {
					sb.append('\n');
				}
				for (int i = 0; i < indent; i++) {
					sb.append("  ");
				}
			}
			if (tree.isLeaf() || tree.isPreTerminal()) {
				renderFlat(tree, sb);
				return;
			}
			sb.append('(');
			sb.append(tree.getLabel());
			renderChildren(tree.getChildren(), indent + 1,
					tree.getLabel() == null
							|| tree.getLabel().toString() == null,
					sb);
			sb.append(')');
		}
	}

	public static class PunctuationNodeStripper
			implements TreeTransformer<String> {
		private final static Pattern punctuationPattern = Pattern
				.compile("\\W+");

		@Override
		public Tree<String> transformTree(Tree<String> tree) {
			final String label = tree.getLabel();
			final Matcher matcher = punctuationPattern.matcher(label);
			if (matcher.matches()) {
				return null;
			}
			if (tree.isLeaf()) {
				return new Tree<String>(label);
			}
			final List<Tree<String>> children = tree.getChildren();
			final List<Tree<String>> transformedChildren = new ArrayList<Tree<String>>();
			for (final Tree<String> child : children) {
				final Tree<String> transformedChild = transformTree(child);
				if (transformedChild != null) {
					transformedChildren.add(transformedChild);
				}
			}
			if (transformedChildren.size() == 0) {
				return null;
			}
			return new Tree<String>(label, transformedChildren);
		}
	}

	public static class StandardTreeNormalizer
			implements TreeTransformer<String> {
		EmptyNodeStripper		emptyNodeStripper		= new EmptyNodeStripper();
		FunctionNodeStripper	functionNodeStripper	= new FunctionNodeStripper();
		XOverXRemover<String>	xOverXRemover			= new XOverXRemover<String>();

		@Override
		public Tree<String> transformTree(Tree<String> tree) {
			tree = functionNodeStripper.transformTree(tree);
			tree = emptyNodeStripper.transformTree(tree);
			tree = xOverXRemover.transformTree(tree);
			return tree;
		}
	}

	public static interface TreeTransformer<E> {
		Tree<E> transformTree(Tree<E> tree);
	}

	public static class XOverXRemover<E> implements TreeTransformer<E> {
		@Override
		public Tree<E> transformTree(Tree<E> tree) {
			final E label = tree.getLabel();
			List<Tree<E>> children = tree.getChildren();
			while (children.size() == 1 && !children.get(0).isLeaf()
					&& label.equals(children.get(0).getLabel())) {
				children = children.get(0).getChildren();
			}
			final List<Tree<E>> transformedChildren = new ArrayList<Tree<E>>();
			for (final Tree<E> child : children) {
				transformedChildren.add(transformTree(child));
			}
			return new Tree<E>(label, transformedChildren);
		}
	}

}
