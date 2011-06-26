package com.temnenkov.jjbot.bot;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.jivesoftware.smack.packet.Presence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.Info.InfoType;
import com.temnenkov.jjbot.bot.Task.TaskType;
import com.temnenkov.jjbot.bot.command.Command;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.impl.AliasesCommand;
import com.temnenkov.jjbot.bot.command.impl.CourseCommand;
import com.temnenkov.jjbot.bot.command.impl.ExpectCommand;
import com.temnenkov.jjbot.bot.command.impl.HelpCommand;
import com.temnenkov.jjbot.bot.command.impl.MtGoxCommand;
import com.temnenkov.jjbot.bot.command.impl.OrderCommand;
import com.temnenkov.jjbot.bot.command.impl.UnknownCommand;
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

	private final CommandManager commands;
	private final Map<RequestSource, String> helpTexts;

	public Bot(String username, String pwd, String listener, String operator,
			final String room, String roomnick) throws XMPPException,
			SQLException, ClassNotFoundException {

		commands = new CommandManager();

		HelpCommand helpCommand = new HelpCommand();
		UnknownCommand unknownCommand = new UnknownCommand();
		commands.add(helpCommand);
		//commands.add(new CourseCommand());
		//commands.add(new MtGoxCommand());
		//commands.add(new OrderCommand());
		//commands.add(new AliasesCommand());
		commands.add(new ExpectCommand());
		commands.add(unknownCommand);

		HelpBuilder hb = new HelpBuilder();
		hb.append("������ ��������� ������:\r\n");
		for (Command cmd : commands.getCommands()) {
			hb.addHelp(cmd);
		}
		hb
				.append("���� ��� ����� ���� - ������ �� https://www.bitcoin.org/smf/index.php?topic=4256.0");
		helpTexts = new HashMap<RequestSource, String>();
		hb.toString(helpTexts);

		hb.toString(helpCommand.getHelpStrings());
		hb.toString(unknownCommand.getHelpStrings());

		logger.info("commands ok");

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
		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				if (packet instanceof Presence) {
					Presence p = (Presence) packet;
					// logger.debug("PRESENCE " + p.getFrom() + " " +
					// p.toXML());
					try {
						Helper.writeToFile(Helper.extractUser(p.getFrom())+".flg", p.toXML());
					} catch (IOException e) {
						logger.error("fail write PRESENCE " + p.getFrom() + " "
								+ p.toXML(), e);
					}
				}

			}
		}, null);

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
		bot.getTaskQueue().add(new Task(TaskType.EXPORTLOG, 20 * 1000));

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
				case EXPORTLOG:
					try {
						bot.getRoomManager().exportYesterdayLog(
								bot.getLogManager());
					} catch (IOException e) {
						logger.error("fail export log", e);
					} catch (SQLException e) {
						logger.error("fail export log", e);
					}
					bot.getTaskQueue().add(
							new Task(TaskType.EXPORTLOG, Helper.tomorrow()
									.plusMinutes(1)));
					break;
				default:
					logger.error("Unknown task " + task);
				}

			}
		}

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

	public List<Command> getCommands() {
		return commands.getCommands();
	}

	public LogManager getLogManager() {
		return logManager;
	}

}
