package com.temnenkov.jjbot;

import org.jivesoftware.smack.packet.Message;

public class Helper {
	static String toString(Message msg) {
		if (msg == null)
			return "null";

		return safeStr(msg.getFrom()) + ":" + safeStr(msg.getBody());
	}

	static String safeStr(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

	static Message createChatMessage(String to, String body) {
		Message msg = new Message(to, Message.Type.chat);
		msg.setBody(body);
		return msg;
	}
}
