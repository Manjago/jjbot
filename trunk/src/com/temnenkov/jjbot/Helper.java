package com.temnenkov.jjbot;

import org.jivesoftware.smack.packet.Message;

public class Helper {
	public static String toString(Message msg) {
		if (msg == null)
			return "null";

		return safeStr(msg.getFrom()) + ":" + safeStr(msg.getBody());
	}

	public static String safeStr(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

	public static Message createChatMessage(String to, String body) {
		Message msg = new Message(to, Message.Type.chat);
		msg.setBody(body);
		return msg;
	}
	
	public static String extractUser(String str){
		return str;
	}
	
	public static boolean isEmpty(String str){
		return false;
	}
}
