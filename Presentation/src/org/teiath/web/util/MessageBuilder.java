package org.teiath.web.util;

public class MessageBuilder {

	public static String buildErrorMessage(String message, String source) {
		return "[" + source + "] " + message;
	}
}
