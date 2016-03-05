package edu.cornell.cs.nlp.util.log.thread;

import java.io.File;
import java.io.FileNotFoundException;

import edu.cornell.cs.nlp.util.log.Log;
import edu.cornell.cs.nlp.util.log.Logger;

public abstract class LoggingRunnable implements Runnable {

	private final Log	log;
	private final File	loggingFile;

	public LoggingRunnable() {
		if (Thread.currentThread() instanceof ILoggingThread) {
			this.log = ((ILoggingThread) Thread.currentThread()).getLog();
		} else {
			this.log = Logger.DEFAULT_LOG;
		}
		this.loggingFile = null;
	}

	public LoggingRunnable(File loggingFile) {
		this.log = null;
		this.loggingFile = loggingFile;
	}

	public LoggingRunnable(Log log) {
		this.log = log;
		this.loggingFile = null;
	}

	public abstract void loggedRun();

	@Override
	public final void run() {

		final Log logToUse;

		if (loggingFile != null) {
			try {
				logToUse = new Log(loggingFile);
			} catch (final FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			logToUse = log;
		}
		try {
			final Log originalLog;
			if (Thread.currentThread() instanceof ILoggingThread) {
				originalLog = ((ILoggingThread) Thread.currentThread())
						.getLog();
				((ILoggingThread) Thread.currentThread()).setLog(logToUse);
			} else {
				originalLog = null;
			}

			loggedRun();

			if (Thread.currentThread() instanceof ILoggingThread) {
				((ILoggingThread) Thread.currentThread()).setLog(originalLog);
			}

		} finally {
			if (loggingFile != null) {
				logToUse.close();
			}
		}

	}

}
