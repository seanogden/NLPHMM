package edu.cornell.cs.nlp.util.ling;

/**
 * Filters are boolean functions which accept or reject items.
 *
 * @author Dan Klein, Berkeley
 */
public interface Filter<T> {
	boolean accept(T t);
}
