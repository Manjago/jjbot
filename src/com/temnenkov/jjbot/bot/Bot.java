package com.temnenkov.jjbot.bot;

import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.web.OrderInformer;
import com.temnenkov.jjbot.btcex.web.TickerInformer;
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

	private MultiUserChat muc2;
	private RoomPacketListener muc2Listen;

	private static ConcurrentLinkedQueue<Info> queue;

	private final Logger logger = LoggerFactory.getLogger(getClass());

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

		String[] rooms = room.split(";");
		String[] roomNicks = roomnick.split(";");
		for (int i = 0; i < rooms.length; ++i) {

			String roomName = rooms[i];
			String roomNick = roomNicks[i];

			MultiUserChat muc = new MultiUserChat(connection, roomName);

			RoomPacketListener lstnr = new RoomPacketListener(logManager,
					roomNick, i == 0, i == 0 ? queue : null);
			muc.addMessageListener(lstnr);

			DiscussionHistory history = new DiscussionHistory();
			history.setMaxStanzas(5);
			muc.join(roomNick, "", history, SmackConfiguration
					.getPacketReplyTimeout());
			logger.info("join room " + roomName + " as " + roomNick + " ok");

			if (i == 0) {
				muc2 = muc;
				muc2Listen = lstnr;
			}
		}

	}

	public void sendMessage(String to, String message) {

		Message msg = new Message(to, Message.Type.chat);

		msg.setBody(message);

		connection.sendPacket(msg);

	}

	public void disconnect() {

		connection.disconnect();

	}

	public static void start(String username, String pwd, String listener,
			String operator, String room, String roomnick)
			throws XMPPException, InterruptedException, SQLException,
			ClassNotFoundException {
		Bot messageSender = new Bot(username, pwd, listener, operator, room,
				roomnick);

		messageSender.sendMessage(messageSender.getUser(), "type #on or #off");

		// messageSender.disconnect();
		while (true) {
			Thread.sleep(1000);
			Info i = queue.poll();
			if (i != null) {
				String body = i.getFrom() + ":" + i.getData();
				messageSender.sendMessage(messageSender.getUser(), body);
			}
		}
	}

	public void processPacket(Packet packet) {
		Message message = (Message) packet;

		System.out.println("process message  (from: " + message.getFrom() + "): "
				+ message.getBody());

		// ���� ���� - ����, �� ������ �� ������ (����� ������ � ���� ������������)
		if (message.getBody() == null){
			logger.debug("� �������� ����, � ����� � ��� " + packet != null ? packet.toString() : "null");
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
		logger.info("get msg from guest " + Helper.toString(message));
		String resp;
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
					if ("HELP".equals(msg)){
						logger.debug("��� ������ ������");
						resp = getHelp(null);
					}
					else{
						logger.debug("��� ����������� �������");
						resp = getHelp(msg);
					}
				}

			}

		}

		logger.debug("�������� " + message.getFrom() + " " + resp);
		sendMessage(message.getFrom(), resp);

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
		sb.append("!USD ������ ������� �� �������-������� ���\r\n");
		sb
				.append("!RUB ������ ������� �� �������-������� ���������� ������\r\n");
		sb.append("!EUR ������ ������� �� �������-������� ����\r\n");
		sb.append("!JPY ������ ������� �� �������-������� �������� ����\r\n");
		sb.append("!YAD ������ ������� �� �������-������� ������.�����\r\n");
		sb.append("!WMZ ������ ������� �� �������-������� WebMoney USD\r\n");
		sb.append("!WMR ������ ������� �� �������-������� WebMoney ������\r\n");
		sb.append("���� ��� ����� ���� - ������ �� https://www.bitcoin.org/smf/index.php?topic=4256.0");
		return sb.toString();
	}

	private void prosessOpers(Message message) {
		String body = message.getBody();
		if (body.contains("#on")) {

			if (muc2Listen != null)
				muc2Listen.setActive(true);
			sendMessage(user, "listener on");
			return;
		}

		if (body.contains("#off")) {
			if (muc2Listen != null)
				muc2Listen.setActive(false);
			sendMessage(user, "listener off");
			return;
		}

		Message msg = muc2.createMessage();
		msg.setBody(message.getBody());
		try {
			muc2.sendMessage(msg);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
