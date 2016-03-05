package edu.cornell.cs.nlp.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maintains a two-way map between a set of objects and contiguous integers from
 * 0 to the number of objects. Use get(i) to look up object i, and
 * indexOf(object) to look up the index of an object.
 *
 * @author Dan Klein, Berkeley
 */
public class Indexer<E> extends AbstractList<E> implements Serializable {
	private static final long	serialVersionUID	= -8769544079136550516L;
	Map<E, Integer>				indexes;
	List<E>						objects;

	public Indexer() {
		objects = new ArrayList<E>();
		indexes = new HashMap<E, Integer>();
	}

	public Indexer(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	/**
	 * Add an element to the indexer. If the element is already in the indexer,
	 * the indexer is unchanged (and false is returned).
	 *
	 * @param e
	 * @return
	 */
	@Override
	public boolean add(E e) {
		if (contains(e)) {
			return false;
		}
		objects.add(e);
		indexes.put(e, size() - 1);
		return true;
	}

	/**
	 * Add an element to the indexer if not already present. In either case,
	 * returns the index of the given object.
	 *
	 * @param e
	 * @return
	 */
	public int addAndGetIndex(E e) {
		final Integer index = indexes.get(e);
		if (index != null) {
			return index;
		}
		// Else, add
		final int newIndex = size();
		objects.add(e);
		indexes.put(e, newIndex);
		return newIndex;
	}

	/**
	 * Constant time override for contains.
	 */
	@Override
	public boolean contains(Object o) {
		return indexes.keySet().contains(o);
	}

	/**
	 * Return the object with the given index
	 *
	 * @param index
	 */
	@Override
	public E get(int index) {
		return objects.get(index);
	}

	/**
	 * Returns the index of the given object, or -1 if the object is not present
	 * in the indexer.
	 *
	 * @param o
	 * @return
	 */
	@Override
	public int indexOf(Object o) {
		final Integer index = indexes.get(o);
		if (index == null) {
			return -1;
		}
		return index;
	}

	/**
	 * Returns the number of objects indexed.
	 */
	@Override
	public int size() {
		return objects.size();
	}
}
