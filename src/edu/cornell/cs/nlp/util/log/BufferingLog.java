package edu.cornell.cs.nlp.util.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Log that buffers the output for future access. Allows to clean the buffer
 * upon request.
 *
 * @author Yoav Artzi
 */
public class BufferingLog extends Log {
	private final StringBuilder buffer = new StringBuilder();

	public BufferingLog() {
		super();
	}

	public BufferingLog(File errFile) throws FileNotFoundException {
		super(errFile);
	}

	public BufferingLog(PrintStream stream) {
		super(stream);
	}

	public void clear() {
		buffer.delete(0, buffer.length());
	}

	public String getBuffer() {
		return buffer.toString();
	}

	@Override
	public void println(String string) {
		super.println(string);
		buffer.append(string).append('\n');
	}

}
