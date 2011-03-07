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
		if (isEmpty(str)) return "";
		int pos = str.indexOf("/");
		if (pos < 0) return "";
		else return str.substring(0, pos);
	}
	
	public static boolean isEmpty(String str){
		return (str == null) || (str.length() == 0) ;
	}
}
