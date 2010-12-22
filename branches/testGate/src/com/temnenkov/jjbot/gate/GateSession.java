package com.temnenkov.jjbot.gate;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateSession {
	private final String user;
	private boolean active;
	private final Chat chat;
	private MultiUserChat muc;
	public MultiUserChat getMuc() {
		return muc;
	}

	public void setMuc(MultiUserChat muc) {
		this.muc = muc;
	}

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final PacketListener mucMessageListener;
	
	public PacketListener getMucMessageListener() {
		return mucMessageListener;
	}

	public Chat getChat() {
		return chat;
	}

	public String getUser() {
		return user;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public GateSession(String user, Chat chat) {
		this.user = user;
		this.chat = chat;
		active = true;
		
		mucMessageListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				Message message = (Message) packet;

				System.out.println(message.getFrom() + ":" + message.getBody());

				if (isActive()){
					try {
						getChat().sendMessage(message.getFrom() + ":" + message.getBody());
					} catch (XMPPException e) {
						logger.error("fail send in muc ");
					}
				}
			}
		};
		
	}

	@Override
	public String toString() {
		return "GateSession [active=" + active + ", chat=" + chat + ", user="
				+ user + "]";
	}
	
	
}
