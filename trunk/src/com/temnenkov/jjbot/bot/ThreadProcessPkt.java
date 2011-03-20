package com.temnenkov.jjbot.bot;

import java.util.Locale;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.Info.InfoType;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.web.OrderInformer;
import com.temnenkov.jjbot.btcex.web.TickerInformer;
import com.temnenkov.jjbot.mtgox.MtgoxTickerInformer;
import com.temnenkov.jjbot.util.Helper;
import com.temnenkov.jjbot.btcex.Pair;

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
		bot.getQueue().add(new Info(InfoType.COMMON, message.getFrom(), resp));

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
				logger.error("fail send message to listener " + Helper.toString(msg), e);
			}

		}

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


	
}
