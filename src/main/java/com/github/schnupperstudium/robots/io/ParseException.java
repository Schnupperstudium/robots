package com.github.schnupperstudium.robots.io;

/**
 * Created by sigmar on 28.05.17.
 */
public class ParseException extends Exception {
	private static final long serialVersionUID = 1466051580825756456L;

	public ParseException(int line, String message) {
		super(formatMessage(line, message));
	}

	public ParseException(int line, String message, Throwable cause) {
		super(formatMessage(line, message), cause);
	}

	private static String formatMessage(int line, String message) {
		return "[" + line + "] " + message;
	}
}
