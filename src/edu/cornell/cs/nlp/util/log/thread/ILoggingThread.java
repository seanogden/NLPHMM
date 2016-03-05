package edu.cornell.cs.nlp.util.log.thread;

import edu.cornell.cs.nlp.util.log.Log;

/**
 *
 * @author Yoav Artzi
 *
 */
public interface ILoggingThread {
	Log getLog();

	void println(String string);

	void println(Throwable throwable);

	void setLog(Log log);
}
