package com.temnenkov.jjbot.bot;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.Info.InfoType;
import com.temnenkov.jjbot.util.Helper;

public class RoomPacketListener implements PacketListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final LogManager logManager;
	private final String room;
	private final String targetAddr;
	private boolean isActive;

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	private final ConcurrentLinkedQueue<Info> queue;

	public RoomPacketListener(LogManager logManager, String room,
			boolean isActive, ConcurrentLinkedQueue<Info> queue,
			String targetAddr) {
		this.logManager = logManager;
		this.room = room;
		this.isActive = isActive;
		this.queue = queue;
		this.targetAddr = targetAddr;
	}

	@Override
	public void processPacket(Packet packet) {
		Message message = (Message) packet;

		System.out.println(message.getFrom() + ":" + message.getBody());
		try {
			logManager.storeMsg(room, message.getFrom(), message.getBody(),
					Helper.isDelayedMessage(message));

			logger.debug("store msg " + Helper.toString(message));
		} catch (SQLException e) {
			logger.error("can not store message", e);
			return;
		}

		if (isActive && (queue != null) && (targetAddr != null))
			queue.add(new Info(InfoType.USER, targetAddr, message));

	}

}
