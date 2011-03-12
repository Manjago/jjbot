package com.temnenkov.jjbot.bot;

import java.sql.SQLException;
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

import com.temnenkov.jjbot.btcex.TickerInformer;
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
			final String room, String roomnick) throws XMPPException, SQLException, ClassNotFoundException {

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
		for(int i=0; i < rooms.length; ++i){
			
			String roomName = rooms[i];
			
			MultiUserChat muc = new MultiUserChat(connection, roomName);

			RoomPacketListener lstnr = new RoomPacketListener(logManager, roomnick, i == 0, i ==0 ? queue : null); 
			muc.addMessageListener(lstnr);

			DiscussionHistory history = new DiscussionHistory();
			history.setMaxStanzas(5);
			muc.join(roomnick, "", history, SmackConfiguration
					.getPacketReplyTimeout());
			logger.info("join room " + roomName + " as " + roomnick + " ok");
			
			if (i == 0){
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
			throws XMPPException, InterruptedException, SQLException, ClassNotFoundException {
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

		System.out.println("Message  (from: " + message.getFrom() + "): "
				+ message.getBody());

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
		logger.info("get msg from guest " + Helper.toString(message));
		String resp;
		if (message.getBody() == null) {

			resp = "Извините, я - глупый бот. Я пока что понимаю только команды WMR, JPY, RUB, YAD, WMZ, EUR, USD";

		} else {
			resp = TickerInformer.info(message.getBody());
			if (resp == null)
				resp = "Извините, я - глупый бот. Я ничего не знаю про валюту \""
						+ message.getBody()
						+ "\". Я пока что понимаю только команды WMR, JPY, RUB, YAD, WMZ, EUR, USD";

		}

		sendMessage(message.getFrom(), resp);

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
