package com.temnenkov.jjbot.bot;

public class Info {

	private String from;
	private String data;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Info(String from, String data) {
		super();
		this.from = from;
		this.data = data;
	}

	@Override
	public String toString() {
		return "Info [data=" + data + ", from=" + from + "]";
	}

	
}
