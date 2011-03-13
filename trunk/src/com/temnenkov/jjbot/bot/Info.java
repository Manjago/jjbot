package com.temnenkov.jjbot.bot;

import org.jivesoftware.smack.packet.Message;

public class Info {

	public enum InfoType {
		USER, COMMON
	}

	private final InfoType type;

	public InfoType getType() {
		return type;
	}

	private String targetAddr;
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

	public String getTargetAddr() {
		return targetAddr;
	}

	public void setTargetAddr(String targetAddr) {
		this.targetAddr = targetAddr;
	}

	public Info(InfoType type, String targetAddr, Message msg) {
		this.type = type;
		this.targetAddr = targetAddr;
		this.from = msg.getFrom();
		this.data = msg.getBody();
	}

	public Info(InfoType type, String targetAddr, String content) {
		this.type = type;
		this.targetAddr = targetAddr;
		this.data = content;
	}

	@Override
	public String toString() {
		return "Info [data=" + data + ", from=" + from + ", targetAddr="
				+ targetAddr + "]";
	}

}
