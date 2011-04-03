package com.temnenkov.jjbot.bot;

import java.util.Locale;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.Info.InfoType;
import com.temnenkov.jjbot.bot.command.Command;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;
import com.temnenkov.jjbot.util.Helper;

public class ThreadProcessPkt implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Packet packet;
	private final Bot bot;

	public ThreadProcessPkt(Packet packet, Bot bot) {
		this.packet = packet;
		this.bot = bot;
	}

	@Override
	public void run() {
		doPacket(packet);
	}

	private void processUsers(Message message) {

		String resp;
		if (message.getBody() == null) {

			resp = "��������, � - ������ ���. ������� ������� HELP, ����������.";

		} else {

			boolean fromMultiChat = bot.getRoomManager().isFromUs(message);
			Request request = new Request(message.getFrom(), message.getBody()
					.toUpperCase(new Locale("ru", "RU")),
					fromMultiChat ? RequestSource.MULTICHATPRIVATE
							: RequestSource.PRIVATE);
			Responce responce = new Responce();
			try {
				for (Command cmd : bot.getCommands()) {
					cmd.process(request, responce);
					if (responce.isStopped())
						break;
				}

			} catch (Exception e) {
				logger.error("fail process cmd", e);
				return;
			}
			resp = responce.getText();
		}

		logger.debug("�������� � ������� " + message.getFrom() + " " + resp);
		bot.getQueue().add(new Info(InfoType.COMMON, message.getFrom(), resp));
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
		if (!msg.startsWith(bot.getUser()) && !msg.startsWith(bot.getUser2()))
			processUsers(message);
		else
			prosessOpers(message);
	}

	private void prosessOpers(Message message) {
		String body = message.getBody();
		if (body.contains("#on")) {

			if (bot.getRoomManager().getUserMucListen() != null)
				bot.getRoomManager().getUserMucListen().setActive(true);
			bot.sendMessage(bot.getUser(), "listener on");
			return;
		}

		if (body.contains("#off")) {
			if (bot.getRoomManager().getUserMucListen() != null)
				bot.getRoomManager().getUserMucListen().setActive(false);
			bot.sendMessage(bot.getUser(), "listener off");
			return;
		}

		if (bot.getRoomManager().getUserMuc() != null) {
			Message msg = bot.getRoomManager().getUserMuc().createMessage();
			msg.setBody(message.getBody());
			try {
				bot.getRoomManager().getUserMuc().sendMessage(msg);
			} catch (XMPPException e) {
				logger.error("fail send message to listener "
						+ Helper.toString(msg), e);
			}

		}

	}

}
