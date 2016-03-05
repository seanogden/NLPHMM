package edu.cornell.cs.nlp.util.log;

import java.io.PrintStream;

import edu.cornell.cs.nlp.util.log.thread.ILoggingThread;

/**
 *
 * @author Yoav Artzi
 *
 */
public class Logger implements ILogger {
	public static Log		DEFAULT_LOG	= new Log();

	private static boolean	SKIP_PREFIX	= false;

	private LogLevel		customLogLevel;

	private final String	prefix;

	public Logger(String prefix) {
		this(prefix, null);
	}

	public Logger(String prefix, LogLevel customLogLevel) {
		this.customLogLevel = customLogLevel;
		this.prefix = prefix + " :: ";
	}

	public static void setSkipPrefix(boolean skipPrefix) {
		SKIP_PREFIX = skipPrefix;
	}

	private static void println(Throwable throwable) {
		if (Thread.currentThread() instanceof ILoggingThread) {
			((ILoggingThread) Thread.currentThread()).println(throwable);
		} else {
			DEFAULT_LOG.println(throwable);
		}
	}

	@Override
	public void clearCustomLevel() {
		setCustomLevel(null);
	}

	@Override
	public final void debug(Object o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format("%s", o));
		}
	}

	@Override
	public void debug(Runnable runnable) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			runnable.run();
		}
	}

	@Override
	public final void debug(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format(msg));
		}
	}

	@Override
	public final void debug(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format(msg, o1));
		}
	}

	@Override
	public final void debug(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format(msg, o1, o2));
		}
	}

	@Override
	public final void debug(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format(msg, o1, o2, o3));
		}
	}

	@Override
	public final void debug(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format(msg, o1, o2, o3, o4));
		}
	}

	@Override
	public final void debug(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}

	@Override
	public void debug(String msg, Throwable t) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(String.format(msg, t));
			println(t);
		}
	}

	@Override
	public void debug(Throwable t) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEBUG.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEBUG.level) {
			println(t);
		}
	}

	@Override
	public final void dev(Object o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format("%s", o));
		}
	}

	@Override
	public void dev(Runnable runnable) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			runnable.run();
		}
	}

	@Override
	public final void dev(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format(msg));
		}
	}

	@Override
	public final void dev(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format(msg, o1));
		}
	}

	@Override
	public final void dev(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format(msg, o1, o2));
		}
	}

	@Override
	public final void dev(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format(msg, o1, o2, o3));
		}
	}

	@Override
	public final void dev(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format(msg, o1, o2, o3, o4));
		}
	}

	@Override
	public final void dev(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}

	@Override
	public void dev(String msg, Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(String.format(msg, o));
			println(o);
		}
	}

	@Override
	public void dev(Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.DEV.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.DEV.level) {
			println(o);
		}
	}

	@Override
	public final void error(Object o) {

		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format("%s", o));
		}
	}

	@Override
	public void error(Runnable runnable) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			runnable.run();
		}
	}

	@Override
	public final void error(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format(msg));
		}
	}

	@Override
	public final void error(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format(msg, o1));
		}
	}

	@Override
	public final void error(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format(msg, o1, o2));
		}
	}

	@Override
	public final void error(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format(msg, o1, o2, o3));
		}
	}

	@Override
	public final void error(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format(msg, o1, o2, o3, o4));
		}
	}

	@Override
	public final void error(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}

	@Override
	public void error(String msg, Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(String.format(msg, o));
			println(o);
		}
	}

	@Override
	public void error(Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.ERROR.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.ERROR.level) {
			println(o);
		}
	}

	@Override
	public LogLevel getLogLevel() {
		if (customLogLevel == null) {
			return LogLevel.CURRENT_LOG_LEVEL;
		} else {
			return customLogLevel;
		}
	}

	@Override
	public final void info(Object o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format("%s", o));
		}
	}

	@Override
	public void info(Runnable runnable) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			runnable.run();
		}
	}

	@Override
	public final void info(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format(msg));
		}
	}

	@Override
	public final void info(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format(msg, o1));
		}
	}

	@Override
	public final void info(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format(msg, o1, o2));
		}
	}

	@Override
	public final void info(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format(msg, o1, o2, o3));
		}
	}

	@Override
	public final void info(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format(msg, o1, o2, o3, o4));
		}
	}

	@Override
	public final void info(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}

	@Override
	public void info(String msg, Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(String.format(msg, o));
			println(o);
		}
	}

	@Override
	public void info(Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.INFO.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.INFO.level) {
			println(o);
		}
	}

	@Override
	public void setCustomLevel(LogLevel level) {
		this.customLogLevel = level;
	}

	@Override
	public final void warn(Object o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format("%s", o));
		}
	}

	@Override
	public void warn(Runnable runnable) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			runnable.run();
		}
	}

	@Override
	public final void warn(String msg) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format(msg));
		}
	}

	@Override
	public final void warn(String msg, Object o1) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format(msg, o1));
		}
	}

	@Override
	public final void warn(String msg, Object o1, Object o2) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format(msg, o1, o2));
		}
	}

	@Override
	public final void warn(String msg, Object o1, Object o2, Object o3) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format(msg, o1, o2, o3));
		}
	}

	@Override
	public final void warn(String msg, Object o1, Object o2, Object o3,
			Object o4) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format(msg, o1, o2, o3, o4));
		}
	}

	@Override
	public final void warn(String msg, Object o1, Object o2, Object o3,
			Object o4, Object o5) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format(msg, o1, o2, o3, o4, o5));
		}
	}

	@Override
	public void warn(String msg, Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(String.format(msg, o));
			println(o);
		}
	}

	@Override
	public void warn(Throwable o) {
		if (LogLevel.CURRENT_LOG_LEVEL.level >= LogLevel.WARN.level
				|| customLogLevel != null
						&& customLogLevel.level >= LogLevel.WARN.level) {
			println(o);
		}
	}

	private void println(String str) {
		if (Thread.currentThread() instanceof ILoggingThread) {
			((ILoggingThread) Thread.currentThread())
					.println(SKIP_PREFIX ? str : prefix + str);
		} else {
			DEFAULT_LOG.println(SKIP_PREFIX ? str : prefix + str);
		}
	}

	public interface IOutputStreamGetter {
		PrintStream get();
	}

}
