package com.temnenkov.jjbot.bot.command.impl;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Expect {
	
	private String author;
	private DateTime deadTime;
	private String content;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public DateTime getDeadTime() {
		return deadTime;
	}

	public void setDeadTime(DateTime deadTime) {
		this.deadTime = deadTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Expect [author=" + author + ", content=" + content
				+ ", deadTime=" + deadTime + "]";
	}

	public String toInfoString() {
		
		final DateTimeFormatter dateFormat = DateTimeFormat
		.forPattern("yyyy-MM-dd HH:mm:ss");
		
		StringBuilder sb = new StringBuilder();
		sb.append(getAuthor());
		sb.append(" ");
		sb.append(dateFormat.print(getDeadTime()));
		sb.append(" ");
		sb.append(getContent());
		
		return sb.toString();
	}

}
