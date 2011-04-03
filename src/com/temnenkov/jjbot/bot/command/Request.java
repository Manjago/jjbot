package com.temnenkov.jjbot.bot.command;

public class Request {
	private final String from;
	private final String body;
	private final RequestSource source;

	public Request(String from, String body, RequestSource source) {
		this.from = from;
		this.body = body;
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
		return "Request [body=" + body + ", from=" + from + ", source="
				+ source + "]";
	}

}
