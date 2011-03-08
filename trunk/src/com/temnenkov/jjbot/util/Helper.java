package com.temnenkov.jjbot.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.smack.packet.Message;

public class Helper {
	public static String toString(Message msg) {
		if (msg == null)
			return "null";

		return safeStr(msg.getFrom())
				+ ":"
				+ safeStr(msg.getBody()
						+ (isDelayedMessage(msg) ? " (delayed)" : ""));
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

	public static String extractUser(String str) {
		if (isEmpty(str))
			return "";
		int pos = str.indexOf("/");
		if (pos < 0)
			return "";
		else
			return str.substring(0, pos);
	}

	public static String extractRoomNick(String str) {
		if (isEmpty(str))
			return "";
		int pos = str.indexOf("/");
		if (pos < 0)
			return "";
		else
			return str.substring(pos + 1);
	}

	public static boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	public static void lameTrust() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}

	}

	public static boolean isDelayedMessage(Message msg) {
		return msg.getExtension("delay", "urn:xmpp:delay") != null;
	}
}
