package edu.cornell.cs.nlp.util.ling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Concatenates an iterator over iterators into one long iterator.
 *
 * @author Dan Klein, Berkeley
 */
public class ConcatenationIterator<E> implements Iterator<E> {

	Iterator<E>				currentIterator;
	Iterator<E>				lastIteratorToReturn;
	Iterator<Iterator<E>>	sourceIterators;

	public ConcatenationIterator(Collection<Iterator<E>> iteratorCollection) {
		this(iteratorCollection.iterator());
	}

	public ConcatenationIterator(Iterator<Iterator<E>> sourceIterators) {
		this.sourceIterators = sourceIterators;
		this.currentIterator = new ArrayList<E>().iterator();
		this.lastIteratorToReturn = null;
		advance();
	}

	public static void main(String[] args) {
		final List<String> list0 = Collections.emptyList();
		final List<String> list1 = Arrays.asList("a b c d".split(" "));
		final List<String> list2 = Arrays.asList("e f".split(" "));
		final List<Iterator<String>> iterators = new ArrayList<Iterator<String>>();
		iterators.add(list1.iterator());
		iterators.add(list0.iterator());
		iterators.add(list2.iterator());
		iterators.add(list0.iterator());
		final Iterator<String> iterator = new ConcatenationIterator<String>(
				iterators);
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}

	@Override
	public boolean hasNext() {
		if (currentIterator.hasNext()) {
			return true;
		}
		return false;
	}

	@Override
	public E next() {
		if (currentIterator.hasNext()) {
			final E e = currentIterator.next();
			lastIteratorToReturn = currentIterator;
			advance();
			return e;
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		if (lastIteratorToReturn == null) {
			throw new IllegalStateException();
		}
		currentIterator.remove();
	}

	private void advance() {
		while (!currentIterator.hasNext() && sourceIterators.hasNext()) {
			currentIterator = sourceIterators.next();
		}
	}
}
