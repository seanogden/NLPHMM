package edu.cornell.cs.nlp.util.log.thread;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread factory to generated {@link LoggingThread}. Code copied from
 * {@link Executors.DefaultThreadFactory}.
 *
 * @author Yoav Artzi
 */
public class LoggingThreadFactory implements ThreadFactory, Serializable {
	/**
	 *
	 */
	private static final long	serialVersionUID	= -1342951854451613041L;
	private final String		namePrefix;
	private final AtomicInteger	threadNumber		= new AtomicInteger(1);

	public LoggingThreadFactory() {
		this("P");
	}

	public LoggingThreadFactory(String threadNamePrefix) {
		namePrefix = threadNamePrefix + "-T";
	}

	@Override
	public Thread newThread(Runnable r) {
		final Thread t = new LoggingThread(null, r,
				namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}

}
