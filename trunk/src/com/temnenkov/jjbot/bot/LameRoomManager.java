package com.temnenkov.jjbot.bot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.util.Helper;

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
			if (!chat.getMuc().isJoined()) {
				logger.debug("NOT JOINED");
				chat.getMuc().join(chat.getNickname(), "", null,
						SmackConfiguration.getPacketReplyTimeout());
			}
		}
	}

	public void exportYesterdayLog(LogManager lm) throws IOException,
			SQLException {
		DateTime now = new DateTime();
		DateTime yesterday = now.minusDays(1);
		DateTime fromDate = yesterday.withHourOfDay(0).withMinuteOfHour(0)
				.withSecondOfMinute(0).withMillisOfSecond(0);
		DateTime tillDate = yesterday.withHourOfDay(23).withMinuteOfHour(59)
				.withSecondOfMinute(59).withMillisOfSecond(999);

		for (RoomInfo info : chats) {
			String roomName = info.getMuc().getRoom();
			logger.debug("export log for " + roomName);

			File file = new File("/home/jjbot/" + roomName + ".log");
			FileWriterWithEncoding fw = new FileWriterWithEncoding(file,
					Charset.forName("866"));
			try {
				lm.getLog(fw, roomName, fromDate, tillDate);
			} finally {
				fw.close();
			}

		}
	}

	public boolean isFromUs(Message msg){
		if (msg == null || msg.getFrom() == null) return false;
		
		String from = Helper.extractUser(msg.getFrom());
		for(RoomInfo i: chats){
			if (i.getMuc().getRoom().equals(from))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "LameRoomManager [chats=" + chats + ", connection=" + connection
				+ ", logManager=" + logManager + ", logger=" + logger
				+ ", queue=" + queue + ", user=" + user + ", userMuc="
				+ userMuc + ", userMucListen=" + userMucListen + "]";
	}

}
