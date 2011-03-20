package com.temnenkov.jjbot.bot;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.Info.InfoType;
import com.temnenkov.jjbot.bot.Task.TaskType;
import com.temnenkov.jjbot.util.Helper;

public class Bot implements PacketListener {

	private final String password;
	private final String user;
	private final LogManager logManager;

	public String getUser() {
		return user;
	}

	private final String user2;

	ConnectionConfiguration connConfig;

	XMPPConnection connection;

	private final ConcurrentLinkedQueue<Info> queue;

	private final static Logger logger = LoggerFactory.getLogger(Bot.class);

	private final PriorityBlockingQueue<Task> taskQueue;

	private final LameRoomManager roomManager;
	private final Executor executor;

	public Bot(String username, String pwd, String listener, String operator,
			final String room, String roomnick) throws XMPPException,
			SQLException, ClassNotFoundException {

		executor = Executors.newSingleThreadExecutor();
		taskQueue = new PriorityBlockingQueue<Task>();

		logManager = new LogManager();
		logManager.init();
		logger.info("database ok");

		this.password = pwd;
		this.user = listener;
		this.user2 = operator;

		queue = new ConcurrentLinkedQueue<Info>();

		connConfig = new ConnectionConfiguration("talk.google.com", 5222,
				"gmail.com");

		connection = new XMPPConnection(connConfig);

		connection.connect();
		logger.info("connect ok");

		connection.login(username, password);
		logger.info("login ok");

		Roster roster = connection.getRoster();
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		// PacketFilter filter2 = new MessageTypeFilter(Message.Type.groupchat);

		connection.addPacketListener((PacketListener) this, filter);
		// connection.addPacketListener(new PacketListener() {
		//
		// @Override
		// public void processPacket(Packet packet) {
		// if (packet instanceof Presence) {
		// Presence p = (Presence) packet;
		// logger.debug("PRESENCE " + p.getFrom() + " " + p.toXML());
		// }
		//
		// }
		// }, null);

		roomManager = new LameRoomManager(connection, logManager, queue, user);
		roomManager.init(room, roomnick);
	}

	public void sendMessage(String to, String message) {

		Message msg = new Message(to, Message.Type.chat);

		msg.setBody(message);

		logger.debug("send pkt " + Helper.toString(msg));
		connection.sendPacket(msg);
	}

	public void disconnect() {

		connection.disconnect();

	}

	public static void start(String username, String pwd, String listener,
			String operator, String room, String roomnick)
			throws XMPPException, InterruptedException, SQLException,
			ClassNotFoundException {
		Bot bot = new Bot(username, pwd, listener, operator, room, roomnick);

		// messageSender.sendMessage(messageSender.getUser(),
		// "type #on or #off");
		bot.getQueue().add(
				new Info(InfoType.USER, bot.getUser(), "type #on or #off"));

		bot.getTaskQueue().add(new Task(TaskType.SENDMSG, 0));
		bot.getTaskQueue().add(new Task(TaskType.CHECKALIVE, 1000 * 60));

		while (true) {
			Task task = bot.getTaskQueue().peek();
			if (task == null || task.getExecDate().isAfterNow()) {
				Thread.sleep(200);
			} else {
				// ���� �� ����� - �� ����� � �������� task
				task = bot.getTaskQueue().poll();

				switch (task.getTaskType()) {
				case SENDMSG:
					Info i = bot.getQueue().poll();
					if (i != null) {

						switch (i.getType()) {
						case USER:
							String body = i.getFrom() + ":" + i.getData();
							bot.sendMessage(bot.getUser(), body);
							break;
						case COMMON:
							try {
								bot.sendMessage(i.getTargetAddr(), i.getData());
							} catch (Exception e) {
								logger.error("fail", e);
							}
							break;
						}
						bot.getTaskQueue().add(
								new Task(TaskType.SENDMSG, 15000));
					} else {
						bot.getTaskQueue().add(new Task(TaskType.SENDMSG, 200));
					}
					break;
				case CHECKALIVE:
					bot.check();
					bot.getTaskQueue().add(
							new Task(TaskType.CHECKALIVE, 60 * 1000));
					break;
				default:
					logger.error("Unknown task " + task);
				}

			}
		}

		// while (true) {
		// bot.check();
		// Info i = bot.getQueue().poll();
		// if (i != null) {
		//
		// switch (i.getType()) {
		// case USER:
		// String body = i.getFrom() + ":" + i.getData();
		// bot.sendMessage(bot.getUser(), body);
		// break;
		// case COMMON:
		// try {
		// bot.sendMessage(i.getTargetAddr(), i.getData());
		// } catch (Exception e) {
		// logger.error("fail", e);
		// }
		// break;
		// }
		// Thread.sleep(15000);
		// } else
		// Thread.sleep(200);
		// }
	}

	public void processPacket(Packet packet) {
		ThreadProcessPkt t = new ThreadProcessPkt(packet, this);
		executor.execute(t);
	}

	public ConcurrentLinkedQueue<Info> getQueue() {
		return queue;
	}

	public void check() {
		try {
			roomManager.check();
		} catch (XMPPException e) {
			logger.error("error during checking ", e);
		}
	}

	public String getUser2() {
		return user2;
	}

	public LameRoomManager getRoomManager() {
		return roomManager;
	}

	public PriorityBlockingQueue<Task> getTaskQueue() {
		return taskQueue;
	}

}
