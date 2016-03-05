package edu.cornell.cs.nlp.util.log;

/**
 * Logger interface.
 *
 * @author Yoav Artzi
 */
public interface ILogger {
	void clearCustomLevel();

	void debug(Object o);

	void debug(Runnable runnable);

	void debug(String msg);

	void debug(String msg, Object o1);

	void debug(String msg, Object o1, Object o2);

	void debug(String msg, Object o1, Object o2, Object o3);

	void debug(String msg, Object o1, Object o2, Object o3, Object o4);

	void debug(String msg, Object o1, Object o2, Object o3, Object o4,
			Object o5);

	void debug(String msg, Throwable t);

	void debug(Throwable t);

	void dev(Object o);

	void dev(Runnable runnable);

	void dev(String msg);

	void dev(String msg, Object o1);

	void dev(String msg, Object o1, Object o2);

	void dev(String msg, Object o1, Object o2, Object o3);

	void dev(String msg, Object o1, Object o2, Object o3, Object o4);

	void dev(String msg, Object o1, Object o2, Object o3, Object o4, Object o5);

	void dev(String msg, Throwable o);

	void dev(Throwable o);

	void error(Object o);

	void error(Runnable runnable);

	void error(String msg);

	void error(String msg, Object o1);

	void error(String msg, Object o1, Object o2);

	void error(String msg, Object o1, Object o2, Object o3);

	void error(String msg, Object o1, Object o2, Object o3, Object o4);

	void error(String msg, Object o1, Object o2, Object o3, Object o4,
			Object o5);

	void error(String msg, Throwable o);

	void error(Throwable o);

	LogLevel getLogLevel();

	void info(Object o);

	void info(Runnable runnable);

	void info(String msg);

	void info(String msg, Object o1);

	void info(String msg, Object o1, Object o2);

	void info(String msg, Object o1, Object o2, Object o3);

	void info(String msg, Object o1, Object o2, Object o3, Object o4);

	void info(String msg, Object o1, Object o2, Object o3, Object o4,
			Object o5);

	void info(String msg, Throwable o);

	void info(Throwable o);

	void setCustomLevel(LogLevel level);

	void warn(Object o);

	void warn(Runnable runnable);

	void warn(String msg);

	void warn(String msg, Object o1);

	void warn(String msg, Object o1, Object o2);

	void warn(String msg, Object o1, Object o2, Object o3);

	void warn(String msg, Object o1, Object o2, Object o3, Object o4);

	void warn(String msg, Object o1, Object o2, Object o3, Object o4,
			Object o5);

	void warn(String msg, Throwable o);

	void warn(Throwable o);
}
