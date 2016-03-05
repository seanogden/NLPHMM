package edu.cornell.cs.nlp.util.log.thread;

import edu.cornell.cs.nlp.util.log.Log;
import edu.cornell.cs.nlp.util.log.Logger;

/**
 * A thread that carries a logging stream that can be modified.
 *
 * @author Yoav Artzi
 */
public class LoggingThread extends Thread implements ILoggingThread {
	private Log				log	= Logger.DEFAULT_LOG;
	private final String	prefix;

	public LoggingThread(ThreadGroup group, Runnable target, String name,
			long stackSize) {
		super(group, target, name, stackSize);
		this.prefix = String.format("[%s] ", getName());
	}

	@Override
	public Log getLog() {
		return log;
	}

	@Override
	public void println(String string) {
		synchronized (log) {
			log.println(prefix + string);
		}
	}

	@Override
	public void println(Throwable throwable) {
		synchronized (log) {
			log.println(throwable);
		}
	}

	@Override
	public void setLog(Log log) {
		this.log = log;
	}

}
