package edu.cornell.cs.nlp.util.log.thread;

/**
 * RuntimeException wrapper for {@link InterruptedException}.
 *
 * @author Yoav Artzi
 */
public class InterruptedRuntimeException extends RuntimeException {

	private static final long			serialVersionUID	= 5213812645419869094L;
	private final InterruptedException	baseException;

	public InterruptedRuntimeException(InterruptedException e) {
		this.baseException = e;
	}

	@Override
	public String toString() {
		return baseException.toString();
	}

}
