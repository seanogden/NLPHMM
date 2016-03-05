package edu.cornell.cs.nlp.util.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 *
 * @author Yoav Artzi
 *
 */
public class Log {

	private final PrintStream	err;
	private final boolean		openedPrintStream;

	public Log() {
		this.err = System.err;
		this.openedPrintStream = false;
	}

	public Log(File errFile) throws FileNotFoundException {
		this.err = new PrintStream(errFile);
		this.openedPrintStream = true;
	}

	public Log(PrintStream stream) {
		this.err = stream;
		this.openedPrintStream = false;
	}

	public void close() {
		if (openedPrintStream) {
			err.close();
		}
	}

	public void println(String string) {
		err.print("[");
		err.print(Thread.currentThread().getName());
		err.print("] ");
		err.println(string);
	}

	public void println(Throwable throwable) {
		err.print("[");
		err.print(Thread.currentThread().getName());
		err.print("] ");
		throwable.printStackTrace(err);
	}
}
