package edu.cornell.cs.nlp.assignments.counting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Dan Klein, Berkeley
 */
public class Counters {
	private static final Random random = new Random();

	public static <K, V> CounterMap<K, V> conditionalNormalize(
			CounterMap<K, V> counterMap) {
		final CounterMap<K, V> normalizedCounterMap = new CounterMap<K, V>();
		for (final K key : counterMap.keySet()) {
			final Counter<V> normalizedSubCounter = normalize(
					counterMap.getCounter(key));
			for (final V value : normalizedSubCounter.keySet()) {
				final double count = normalizedSubCounter.getCount(value);
				normalizedCounterMap.setCount(key, value, count);
			}
		}
		return normalizedCounterMap;
	}

	/**
	 * Simple sparse dot product method. Try to put the sparser
	 * <code>Counter</code> as the <code>x</code>
	 * parameter since we iterate over those keys and search for them in the
	 * <code>y</code> parameter.
	 *
	 * @param x
	 * @param y
	 * @return dotProduct
	 */
	public static <E> double dotProduct(Counter<E> x, Counter<E> y) {
		double total = 0.0;
		for (final E keyX : x.keySet()) {
			total += x.getCount(keyX) * y.getCount(keyX);
		}
		return total;
	}

	/**
	 *
	 * @param <E>
	 * @param x
	 * @param y
	 * @return
	 */
	public static <E> double jensenShannonDivergence(Counter<E> x,
			Counter<E> y) {
		double sum = 0.0;
		final double xTotal = x.totalCount();
		final double yTotal = y.totalCount();
		for (final E key : x.keySet()) {
			// x -> x+y/2
			final double xVal = x.getCount(key) / xTotal;
			final double yVal = y.getCount(key) / yTotal;
			final double avg = 0.5 * (xVal + yVal);
			sum += xVal * Math.log(xVal / avg);
		}
		for (final E key : y.keySet()) {
			// y -> x+y/2
			final double xVal = x.getCount(key) / xTotal;
			final double yVal = y.getCount(key) / yTotal;
			final double avg = 0.5 * (xVal + yVal);
			sum += yVal * Math.log(yVal / avg);
		}
		return sum / 0.5;
	}

	public static <E> Counter<E> normalize(Counter<E> counter) {
		final Counter<E> normalizedCounter = new Counter<E>();
		final double total = counter.totalCount();
		for (final E key : counter.keySet()) {
			normalizedCounter.setCount(key, counter.getCount(key) / total);
		}
		return normalizedCounter;
	}

	public static <E> E sample(Counter<E> counter) {
		final double total = counter.totalCount();
		final double rand = random.nextDouble();
		double sum = 0.0;
		if (total <= 0.0) {
			throw new RuntimeException("Non-positive counter total: " + total);
		}
		for (final E key : counter.keySet()) {
			final double count = counter.getCount(key);
			if (count < 0.0) {
				throw new RuntimeException(
						"Negative count in counter: " + key + " => " + count);
			}
			final double prob = count / total;
			sum += prob;
			if (rand < sum) {
				return key;
			}
		}
		throw new RuntimeException("Shouldn't Reach Here");
	}

	public static <E> List<E> sortedKeys(Counter<E> counter) {
		final List<E> sortedKeyList = new ArrayList<E>();
		final PriorityQueue<E> pq = counter.asPriorityQueue();
		while (pq.hasNext()) {
			sortedKeyList.add(pq.next());
		}
		return sortedKeyList;
	}

	public static <E> String toBiggestValuesFirstString(Counter<E> c) {
		return c.asPriorityQueue().toString();
	}

	public static <E> String toBiggestValuesFirstString(Counter<E> c, int k) {
		final PriorityQueue<E> pq = c.asPriorityQueue();
		final PriorityQueue<E> largestK = new FastPriorityQueue<E>();
		while (largestK.size() < k && pq.hasNext()) {
			final double firstScore = pq.getPriority();
			final E first = pq.next();
			largestK.setPriority(first, firstScore);
		}
		return largestK.toString();
	}
}