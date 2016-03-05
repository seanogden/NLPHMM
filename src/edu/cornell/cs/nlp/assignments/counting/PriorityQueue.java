package edu.cornell.cs.nlp.assignments.counting;

import java.util.Iterator;

/**
 * Priority queue interface: higher priorities are at the head of the queue.
 * GeneralPriorityQueue implements all of the
 * methods, while FastPriorityQueue does not support removal or promotion in the
 * normal manner.
 *
 * @author Dan Klein, Berkeley
 */
public interface PriorityQueue<E> extends Iterator<E> {
	boolean containsKey(E element);

	E getFirst();

	double getPriority();

	double getPriority(E element);

	boolean isEmpty();

	E removeFirst();

	double removeKey(E element);

	void setPriority(E element, double priority);

	int size();

	String toString(int maxKeysToPrint);
}
