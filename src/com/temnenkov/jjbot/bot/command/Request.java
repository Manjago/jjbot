package com.temnenkov.jjbot.bot.command;

import java.util.Locale;

import com.temnenkov.jjbot.util.Helper;

public class Request {
	private final String from;
	private final String body;
	private final String originalBody;
	private final RequestSource source;

	public Request(String from, String body, RequestSource source) {
		this.from = from;
		this.originalBody = body;
		this.body = Helper.upper(originalBody);
		this.source = source;
	}

	public String getBody() {
		return body;
	}

	public RequestSource getSource() {
		return source;
	}

	public String getFrom() {
		return from;
	}

	@Override
	public String toString() {
		return "Request [body=" + body + ", from=" + from + ", originalBody="
				+ originalBody + ", source=" + source + "]";
	}

	public String getOriginalBody() {
		return originalBody;
	}
	
}
