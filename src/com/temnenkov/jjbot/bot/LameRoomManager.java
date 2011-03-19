package com.temnenkov.jjbot.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LameRoomManager {

	private final XMPPConnection connection;
	private final LogManager logManager;
	private final ConcurrentLinkedQueue<Info> queue;
	private final String user;

	private final List<RoomInfo> chats;

	private MultiUserChat userMuc;
	private RoomPacketListener userMucListen;

	private final Logger logger = LoggerFactory.getLogger(Bot.class);

	public LameRoomManager(XMPPConnection connection, LogManager logManager,
			ConcurrentLinkedQueue<Info> queue, String user) {
		this.connection = connection;
		this.logManager = logManager;
		this.queue = queue;
		this.user = user;
		chats = new ArrayList<RoomInfo>();
	}

	public void init(String room, String roomnick) throws XMPPException {
		String[] rooms = room.split(";");
		String[] roomNicks = roomnick.split(";");
		for (int i = 0; i < rooms.length; ++i) {

			String roomName = rooms[i];
			String roomNick = roomNicks[i];

			createListener(i == 0, roomName, roomNick);
		}

	}

	private void createListener(boolean special, String roomName,
			String roomNick) throws XMPPException {
		MultiUserChat muc = new MultiUserChat(connection, roomName);
		RoomInfo chat = new RoomInfo(muc, roomNick); 
		chats.add(chat);

		RoomPacketListener lstnr = new RoomPacketListener(logManager, roomNick,
				special, special ? queue : null, special ? user : null);
		muc.addMessageListener(lstnr);

		chat.join();

		if (special) {
			userMuc = muc;
			userMucListen = lstnr;
		}
	}

	public MultiUserChat getUserMuc() {
		return userMuc;
	}

	public RoomPacketListener getUserMucListen() {
		return userMucListen;
	}

	public void check() throws XMPPException {
		for (RoomInfo chat : chats) {
			if (chat.getMuc().isJoined())
				logger.debug("joined");
			else {
				logger.debug("NOT JOINED");
				chat.getMuc().join(chat.getNickname(), "", null, SmackConfiguration
						.getPacketReplyTimeout());
			}
		}
	}

	@Override
	public String toString() {
		return "LameRoomManager [chats=" + chats + ", connection=" + connection
				+ ", logManager=" + logManager + ", logger=" + logger
				+ ", queue=" + queue + ", user=" + user + ", userMuc="
				+ userMuc + ", userMucListen=" + userMucListen + "]";
	}

}
