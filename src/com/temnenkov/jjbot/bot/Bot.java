package com.temnenkov.jjbot.bot;

import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.web.OrderInformer;
import com.temnenkov.jjbot.btcex.web.TickerInformer;
import com.temnenkov.jjbot.mtgox.MtgoxTickerInformer;
import com.temnenkov.jjbot.util.Helper;

public class Bot implements PacketListener {

	private final String password;
	private final String user;
	private final LogManager logManager;
	private int tCounter = 0;

	public String getUser() {
		return user;
	}

	private final String user2;

	ConnectionConfiguration connConfig;

	XMPPConnection connection;

	private final ConcurrentLinkedQueue<Info> queue;

	private final static Logger logger = LoggerFactory.getLogger(Bot.class);

	private Object syncObject = new Object();

	private final LameRoomManager roomManager;

	public Bot(String username, String pwd, String listener, String operator,
			final String room, String roomnick) throws XMPPException,
			SQLException, ClassNotFoundException {

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

		// messageSender.disconnect();
		while (true) {
			bot.check();
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
				Thread.sleep(15000);
			} else
				Thread.sleep(200);
		}
	}

	public void processPacket(Packet packet) {
		String name = "bot " + ++tCounter;
		ThreadProcessPkt t = new ThreadProcessPkt(name, packet, this);
		t.start();
	}

	public void safeProcessPacket(Packet packet) {
		synchronized (syncObject) {
			doPacket(packet);
		}
	}

	private void doPacket(Packet packet) {
		Message message = (Message) packet;

		System.out.println("process message  (from: " + message.getFrom()
				+ "): " + message.getBody() + ":" + packet.toXML());

		// ���� ���� - ����, �� ������ �� ������ (����� ������ � ����
		// �����������)
		if (message.getBody() == null) {
			logger.debug("� �������� ����, � ����� � ��� "
					+ (packet != null ? packet.toXML() : "null"));
			return;
		}

		// ��������� ������ �� ���������
		if (message.getFrom() == null)
			return;
		String msg = message.getFrom().toLowerCase();
		if (!msg.startsWith(user) && !msg.startsWith(user2))
			processGuest(message);
		else
			prosessOpers(message);
	}

	private void processGuest(Message message) {
		logger.info("get cmd from guest " + Helper.toString(message));
		String resp;
		try {
			resp = processCmd(message);
		} catch (Exception e) {
			logger.error("fail process cmd", e);
			return;
		}

		logger.debug("�������� � �������" + message.getFrom() + " " + resp);
		// sendMessage(message.getFrom(), resp);
		queue.add(new Info(InfoType.COMMON, message.getFrom(), resp));

	}

	private String processCmd(Message message) {
		String resp = "������ - �������� ������������";
		if (message.getBody() == null) {

			resp = "��������, � - ������ ���. ������� ������� HELP, ����������.";

		} else {

			String msg = message.getBody().toUpperCase(new Locale("ru", "RU"));

			// ���� ������?
			if (Pair.isPair(msg) || "ALL".equals(msg)) {
				logger.debug("��� ���� ������");
				InfoWithHint res = TickerInformer.info(msg);
				if (res.getInfo() == null)
					resp = "��������, � - ������ ���. � �� ������� ������� \""
							+ message.getBody()
							+ "\".\r\n�� �, ��������, ������� ������� \r\n"
							+ res.getHint()
							+ ",\r\nALL (����� ���� ����� �� �����)\r\n��� ������ ������ ������ ������� ������� HELP";
				else
					resp = res.getInfo();
			} else {
				// ��, ����� ����, �� ����� ������ �������?
				if (msg.startsWith("!") && (msg.length() > 1)
						&& Pair.isPair(msg.substring(1))) {
					logger.debug("��� ������ �������");
					InfoWithHint res = OrderInformer.info(msg.substring(1));
					if (res.getInfo() == null) {
						resp = res.getHint();
					} else
						resp = res.getInfo();
				} else {

					// �� ����� ������?
					if ("HELP".equals(msg)) {
						logger.debug("��� ������ ������");
						resp = getHelp(null);
					} else {

						// ������?
						if ("MTGOX".equals(msg)) {
							logger.debug("��� mtgox");
							InfoWithHint res = MtgoxTickerInformer.info();
							if (res.getInfo() == null)
								resp = res.getHint();
							else
								resp = res.getInfo();

						} else {
							logger.debug("��� ����������� �������");
							resp = getHelp(msg);
						}

					}
				}

			}

		}
		return resp;
	}

	private String getHelp(String badCmd) {
		StringBuilder sb = new StringBuilder();
		if (!Helper.isEmpty(badCmd))
			sb.append("� �� ���� ������� \"" + badCmd + "\"\r\n");
		sb.append("������ ��������� ������:\r\n");
		sb.append("HELP ��� ���������\r\n");
		sb.append("USD ���� �������� ���\r\n");
		sb.append("RUB ���� ���������� ������\r\n");
		sb.append("EUR ���� ����\r\n");
		sb.append("JPY ���� �������� ����\r\n");
		sb.append("YAD ���� ������.�����\r\n");
		sb.append("WMZ ���� WebMoney USD\r\n");
		sb.append("WMR ���� WebMoney ������\r\n");
		sb.append("ALL ���� ���� ����������������� �����\r\n");
		sb.append("MTGOX ���� � mtgox.com\r\n");
		sb.append("!USD ������ ������� �� �������-������� ���\r\n");
		sb
				.append("!RUB ������ ������� �� �������-������� ���������� ������\r\n");
		sb.append("!EUR ������ ������� �� �������-������� ����\r\n");
		sb.append("!JPY ������ ������� �� �������-������� �������� ����\r\n");
		sb.append("!YAD ������ ������� �� �������-������� ������.�����\r\n");
		sb.append("!WMZ ������ ������� �� �������-������� WebMoney USD\r\n");
		sb.append("!WMR ������ ������� �� �������-������� WebMoney ������\r\n");
		sb
				.append("���� ��� ����� ���� - ������ �� https://www.bitcoin.org/smf/index.php?topic=4256.0");
		return sb.toString();
	}

	private void prosessOpers(Message message) {
		String body = message.getBody();
		if (body.contains("#on")) {

			if (roomManager.getUserMucListen() != null)
				roomManager.getUserMucListen().setActive(true);
			sendMessage(user, "listener on");
			return;
		}

		if (body.contains("#off")) {
			if (roomManager.getUserMucListen() != null)
				roomManager.getUserMucListen().setActive(false);
			sendMessage(user, "listener off");
			return;
		}

		if (roomManager.getUserMuc() != null) {
			Message msg = roomManager.getUserMuc().createMessage();
			msg.setBody(message.getBody());
			try {
				roomManager.getUserMuc().sendMessage(msg);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

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

}
