package edu.cornell.cs.nlp.assignments.counting;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * A priority queue based on a binary heap. Note that this implementation does
 * not efficiently support containsKey or
 * getPriority. Removal is not supported. If you set the priority of a key
 * multiple times, it will NOT be promoted or
 * demoted, but rather it will be inserted in the queue once multiple times,
 * with the various priorities.
 *
 * @author Dan Klein, Berkeley
 */
public class FastPriorityQueue<E> implements PriorityQueue<E>, Serializable {
	private static final long	serialVersionUID	= 5724671156522771658L;
	int							capacity;
	Object[]					elements;
	double[]					priorities;
	int							size;

	public FastPriorityQueue() {
		this(15);
	}

	public FastPriorityQueue(int capacity) {
		int legalCapacity = 0;
		while (legalCapacity < capacity) {
			legalCapacity = 2 * legalCapacity + 1;
		}
		grow(legalCapacity);
	}

	public static void main(String[] args) {
		final PriorityQueue<String> pq = new FastPriorityQueue<String>();
		System.out.println(pq);
		pq.setPriority("one", 1);
		System.out.println(pq);
		pq.setPriority("three", 3);
		System.out.println(pq);
		pq.setPriority("one", 1.1);
		System.out.println(pq);
		pq.setPriority("two", 2);
		System.out.println(pq);
		System.out.println(pq.toString(2));
		while (pq.hasNext()) {
			System.out.println(pq.next());
		}
	}

	/**
	 * Returns a counter whose keys are the elements in this priority queue, and
	 * whose counts are the priorities in this
	 * queue. In the event there are multiple instances of the same element in
	 * the queue, the counter's count will be the
	 * sum of the instances' priorities.
	 *
	 * @return
	 */
	public Counter<E> asCounter() {
		final PriorityQueue<E> pq = deepCopy();
		final Counter<E> counter = new Counter<E>();
		while (!pq.isEmpty()) {
			final double priority = pq.getPriority();
			final E element = pq.removeFirst();
			counter.incrementCount(element, priority);
		}
		return counter;
	}

	@Override
	public boolean containsKey(E e) {
		for (int i = 0; i < elements.length; i++) {
			final Object element = elements[i];
			if (e == null && element == null) {
				return true;
			}
			if (e != null && e.equals(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a clone of this priority queue. Modifications to one will not
	 * affect modifications to the other.
	 */
	public FastPriorityQueue<E> deepCopy() {
		final FastPriorityQueue<E> clonePQ = new FastPriorityQueue<E>();
		clonePQ.size = size;
		clonePQ.capacity = capacity;
		clonePQ.elements = new Object[capacity];
		clonePQ.priorities = new double[capacity];
		if (size() > 0) {
			System.arraycopy(elements, 0, clonePQ.elements, 0, size());
			System.arraycopy(priorities, 0, clonePQ.priorities, 0, size());
		}
		return clonePQ;
	}

	/**
	 * Returns the highest-priority element in the queue, but does not pop it.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E getFirst() {
		if (size < 1) {
			throw new NoSuchElementException();
		}
		return (E) elements[0];
	}

	/**
	 * Gets the priority of the highest-priority element of the queue.
	 */
	@Override
	public double getPriority() {
		if (size() > 0) {
			return priorities[0];
		}
		throw new NoSuchElementException();
	}

	@Override
	public double getPriority(E e) {
		double bestPriority = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < elements.length; i++) {
			final Object element = elements[i];
			if (e == null && element == null
					|| e != null && e.equals(element)) {
				if (priorities[i] > bestPriority) {
					bestPriority = priorities[i];
				}
			}
		}
		return bestPriority;
	}

	/**
	 * Returns true if the priority queue is non-empty
	 */
	@Override
	public boolean hasNext() {
		return !isEmpty();
	}

	/**
	 * True if the queue is empty (size == 0).
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the element in the queue with highest priority, and pops it from
	 * the queue.
	 */
	@Override
	public E next() {
		return removeFirst();
	}

	/**
	 * Not supported -- next() already removes the head of the queue.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E removeFirst() {
		if (size < 1) {
			throw new NoSuchElementException();
		}
		final Object element = elements[0];
		swap(0, size - 1);
		size--;
		elements[size] = null;
		heapifyDown(0);
		return (E) element;
	}

	@Override
	public double removeKey(E e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds a key to the queue with the given priority. If the key is already in
	 * the queue, it will be added an
	 * additional time, NOT promoted/demoted.
	 *
	 * @param key
	 * @param priority
	 */
	@Override
	public void setPriority(E key, double priority) {
		if (size == capacity) {
			grow(2 * capacity + 1);
		}
		elements[size] = key;
		priorities[size] = priority;
		heapifyUp(size);
		size++;
	}

	/**
	 * Number of elements in the queue.
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns a representation of the queue in decreasing priority order.
	 */
	@Override
	public String toString() {
		return toString(size());
	}

	/**
	 * Returns a representation of the queue in decreasing priority order,
	 * displaying at most maxKeysToPrint elements.
	 *
	 * @param maxKeysToPrint
	 */
	@Override
	public String toString(int maxKeysToPrint) {
		final PriorityQueue<E> pq = deepCopy();
		final StringBuilder sb = new StringBuilder("[");
		int numKeysPrinted = 0;
		while (numKeysPrinted < maxKeysToPrint && !pq.isEmpty()) {
			final double priority = pq.getPriority();
			final E element = pq.removeFirst();
			sb.append(element.toString());
			sb.append(" : ");
			sb.append(priority);
			if (numKeysPrinted < size() - 1) {
				// sb.append("\n");
				sb.append(", ");
			}
			numKeysPrinted++;
		}
		if (numKeysPrinted < size()) {
			sb.append("...");
		}
		sb.append("]");
		return sb.toString();
	}

	protected void grow(int newCapacity) {
		final Object[] newElements = new Object[newCapacity];
		final double[] newPriorities = new double[newCapacity];
		if (size > 0) {
			System.arraycopy(elements, 0, newElements, 0, elements.length);
			System.arraycopy(priorities, 0, newPriorities, 0,
					priorities.length);
		}
		elements = newElements;
		priorities = newPriorities;
		capacity = newCapacity;
	}

	protected void heapifyDown(int loc) {
		int max = loc;
		final int leftChild = leftChild(loc);
		if (leftChild < size()) {
			final double priority = priorities[loc];
			final double leftChildPriority = priorities[leftChild];
			if (leftChildPriority > priority) {
				max = leftChild;
			}
			final int rightChild = rightChild(loc);
			if (rightChild < size()) {
				final double rightChildPriority = priorities[rightChild(loc)];
				if (rightChildPriority > priority
						&& rightChildPriority > leftChildPriority) {
					max = rightChild;
				}
			}
		}
		if (max == loc) {
			return;
		}
		swap(loc, max);
		heapifyDown(max);
	}

	protected void heapifyUp(int loc) {
		if (loc == 0) {
			return;
		}
		final int parent = parent(loc);
		if (priorities[loc] > priorities[parent]) {
			swap(loc, parent);
			heapifyUp(parent);
		}
	}

	protected int leftChild(int loc) {
		return 2 * loc + 1;
	}

	protected int parent(int loc) {
		return (loc - 1) / 2;
	}

	protected int rightChild(int loc) {
		return 2 * loc + 2;
	}

	protected void swap(int loc1, int loc2) {
		final double tempPriority = priorities[loc1];
		final Object tempElement = elements[loc1];
		priorities[loc1] = priorities[loc2];
		elements[loc1] = elements[loc2];
		priorities[loc2] = tempPriority;
		elements[loc2] = tempElement;
	}
}
