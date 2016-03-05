package edu.cornell.cs.nlp.assignments.counting;

/**
 * Convenience Extension of Counter to use an IdentityHashMap.
 *
 * @author Dan Klein, Berkeley
 */
public class IdentityCounter<E> extends Counter<E> {
	private static final long serialVersionUID = -8807552227053770788L;

	public IdentityCounter() {
		super(new MapFactory.IdentityHashMapFactory<E, Double>());
	}
}
