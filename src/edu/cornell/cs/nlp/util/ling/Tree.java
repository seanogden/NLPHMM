package edu.cornell.cs.nlp.util.ling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represent linguistic trees, with each node consisting of a label and a list
 * of children.
 *
 * @author Dan Klein, Berkeley
 */
public class Tree<L> {
	List<Tree<L>>	children;
	L				label;

	public Tree(L label) {
		this.label = label;
		this.children = Collections.emptyList();
	}

	public Tree(L label, List<Tree<L>> children) {
		this.label = label;
		this.children = children;
	}

	private static <L> void appendPreTerminalYield(Tree<L> tree,
			List<L> yield) {
		if (tree.isPreTerminal()) {
			yield.add(tree.getLabel());
			return;
		}
		for (final Tree<L> child : tree.getChildren()) {
			appendPreTerminalYield(child, yield);
		}
	}

	private static <L> void appendYield(Tree<L> tree, List<L> yield) {
		if (tree.isLeaf()) {
			yield.add(tree.getLabel());
			return;
		}
		for (final Tree<L> child : tree.getChildren()) {
			appendYield(child, yield);
		}
	}

	private static <L> Tree<L> deepCopy(Tree<L> tree) {
		final List<Tree<L>> childrenCopies = new ArrayList<Tree<L>>();
		for (final Tree<L> child : tree.getChildren()) {
			childrenCopies.add(deepCopy(child));
		}
		return new Tree<L>(tree.getLabel(), childrenCopies);
	}

	private static <L> int toConstituentCollectionHelper(Tree<L> tree,
			int start, List<Constituent<L>> constituents) {
		if (tree.isLeaf() || tree.isPreTerminal()) {
			return 1;
		}
		int span = 0;
		for (final Tree<L> child : tree.getChildren()) {
			span += toConstituentCollectionHelper(child, start + span,
					constituents);
		}
		constituents
				.add(new Constituent<L>(tree.getLabel(), start, start + span));
		return span;
	}

	private static <L> void traversalHelper(Tree<L> tree,
			List<Tree<L>> traversal, boolean preOrder) {
		if (preOrder) {
			traversal.add(tree);
		}
		for (final Tree<L> child : tree.getChildren()) {
			traversalHelper(child, traversal, preOrder);
		}
		if (!preOrder) {
			traversal.add(tree);
		}
	}

	public Tree<L> deepCopy() {
		return deepCopy(this);
	}

	public List<Tree<L>> getChildren() {
		return children;
	}

	public L getLabel() {
		return label;
	}

	public List<Tree<L>> getPostOrderTraversal() {
		final ArrayList<Tree<L>> traversal = new ArrayList<Tree<L>>();
		traversalHelper(this, traversal, false);
		return traversal;
	}

	public List<Tree<L>> getPreOrderTraversal() {
		final ArrayList<Tree<L>> traversal = new ArrayList<Tree<L>>();
		traversalHelper(this, traversal, true);
		return traversal;
	}

	public List<L> getPreTerminalYield() {
		final List<L> yield = new ArrayList<L>();
		appendPreTerminalYield(this, yield);
		return yield;
	}

	public List<L> getYield() {
		final List<L> yield = new ArrayList<L>();
		appendYield(this, yield);
		return yield;
	}

	public boolean isLeaf() {
		return getChildren().isEmpty();
	}

	public boolean isPhrasal() {
		return !(isLeaf() || isPreTerminal());
	}

	public boolean isPreTerminal() {
		return getChildren().size() == 1 && getChildren().get(0).isLeaf();
	}

	public void setChildren(List<Tree<L>> children) {
		this.children = children;
	}

	public void setLabel(L label) {
		this.label = label;
	}

	public List<Constituent<L>> toConstituentList() {
		final List<Constituent<L>> constituentList = new ArrayList<Constituent<L>>();
		toConstituentCollectionHelper(this, 0, constituentList);
		return constituentList;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toStringBuilder(sb);
		return sb.toString();
	}

	public void toStringBuilder(StringBuilder sb) {
		if (!isLeaf()) {
			sb.append('(');
		}
		if (getLabel() != null) {
			sb.append(getLabel());
		}
		if (!isLeaf()) {
			for (final Tree<L> child : getChildren()) {
				sb.append(' ');
				child.toStringBuilder(sb);
			}
			sb.append(')');
		}
	}

	public List<Tree<L>> toSubTreeList() {
		return getPreOrderTraversal();
	}
}
