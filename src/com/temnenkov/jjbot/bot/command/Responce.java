package com.temnenkov.jjbot.bot.command;

public class Responce {
	private final static String CRLF = "\r\n";
	private boolean stopped;
	private StringBuilder sb;

	public Responce() {
		sb = new StringBuilder();
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public String getText() {
		return sb.toString();
	}

	public void print(String s) {
		sb.append(s);
	}

	public void printLn(String s) {
		print(s);
		sb.append(CRLF);
	}

	@Override
	public String toString() {
		return "Responce [sb=" + sb + ", stopped=" + stopped + "]";
	}

}
