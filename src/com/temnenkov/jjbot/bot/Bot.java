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

	private MultiUserChat muc2;
	private RoomPacketListener muc2Listen;

	private static ConcurrentLinkedQueue<Info> queue;

	private final static Logger logger = LoggerFactory.getLogger(Bot.class);

	private Object syncObject = new Object();

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
					roomNick, i == 0, i == 0 ? queue : null, i == 0 ? user
							: null);
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
		Bot messageSender = new Bot(username, pwd, listener, operator, room,
				roomnick);

		// messageSender.sendMessage(messageSender.getUser(),
		// "type #on or #off");
		queue.add(new Info(InfoType.USER, messageSender.getUser(),
				"type #on or #off"));

		// messageSender.disconnect();
		while (true) {
			Info i = queue.poll();
			if (i != null) {

				switch (i.getType()) {
				case USER:
					String body = i.getFrom() + ":" + i.getData();
					messageSender.sendMessage(messageSender.getUser(), body);
					break;
				case COMMON:
					try {
						messageSender.sendMessage(i.getTargetAddr(), i
								.getData());
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

		// если боди - нулл, то ничего не делаем (такое бывает с этой
		// библилотекой)
		if (message.getBody() == null) {
			logger.debug("у мессаджа нулл, а пакет у нас "
					+ (packet != null ? packet.toXML() : "null"));
			return;
		}

		// принимаем только от оператора
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

		logger.debug("Посылаем в очередь" + message.getFrom() + " " + resp);
		// sendMessage(message.getFrom(), resp);
		queue.add(new Info(InfoType.COMMON, message.getFrom(), resp));

	}

	private String processCmd(Message message) {
		String resp = "Ошибка - сообщите разработчЕГУ";
		if (message.getBody() == null) {

			resp = "Извините, я - глупый бот. Введите команду HELP, пожалуйста.";

		} else {

			String msg = message.getBody().toUpperCase(new Locale("ru", "RU"));

			// курс валюты?
			if (Pair.isPair(msg) || "ALL".equals(msg)) {
				logger.debug("это курс валюты");
				InfoWithHint res = TickerInformer.info(msg);
				if (res.getInfo() == null)
					resp = "Извините, я - глупый бот. Я не понимаю команду \""
							+ message.getBody()
							+ "\".\r\nНо я, например, понимаю команды \r\n"
							+ res.getHint()
							+ ",\r\nALL (курсы всех валют на бирже)\r\nДля вывода списка команд введите команду HELP";
				else
					resp = res.getInfo();
			} else {
				// ну, может быть, мы хотим список ордеров?
				if (msg.startsWith("!") && (msg.length() > 1)
						&& Pair.isPair(msg.substring(1))) {
					logger.debug("это список ордеров");
					InfoWithHint res = OrderInformer.info(msg.substring(1));
					if (res.getInfo() == null) {
						resp = res.getHint();
					} else
						resp = res.getInfo();
				} else {

					// ну может помощь?
					if ("HELP".equals(msg)) {
						logger.debug("это запрос помощи");
						resp = getHelp(null);
					} else {

						// мтгокс?
						if ("MTGOX".equals(msg)) {
							logger.debug("это mtgox");
							InfoWithHint res = MtgoxTickerInformer.info();
							if (res.getInfo() == null)
								resp = res.getHint();
							else
								resp = res.getInfo();

						} else {
							logger.debug("это неизвестная команда");
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
			sb.append("Я не знаю команду \"" + badCmd + "\"\r\n");
		sb.append("Список доступных команд:\r\n");
		sb.append("HELP это сообщение\r\n");
		sb.append("USD курс долларов США\r\n");
		sb.append("RUB курс российских рублей\r\n");
		sb.append("EUR курс евро\r\n");
		sb.append("JPY курс японской иены\r\n");
		sb.append("YAD курс Яндекс.Денег\r\n");
		sb.append("WMZ курс WebMoney USD\r\n");
		sb.append("WMR курс WebMoney рублей\r\n");
		sb.append("ALL курс всех вышеперечисленных валют\r\n");
		sb.append("MTGOX курс с mtgox.com\r\n");
		sb.append("!USD список ордеров на покупку-продажу США\r\n");
		sb
				.append("!RUB список ордеров на покупку-продажу российских рублей\r\n");
		sb.append("!EUR список ордеров на покупку-продажу евро\r\n");
		sb.append("!JPY список ордеров на покупку-продажу японской иены\r\n");
		sb.append("!YAD список ордеров на покупку-продажу Яндекс.Денег\r\n");
		sb.append("!WMZ список ордеров на покупку-продажу WebMoney USD\r\n");
		sb.append("!WMR список ордеров на покупку-продажу WebMoney рублей\r\n");
		sb
				.append("Если вам этого мало - пишите на https://www.bitcoin.org/smf/index.php?topic=4256.0");
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
