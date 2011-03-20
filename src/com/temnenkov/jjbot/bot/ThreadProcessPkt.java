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

		// если боди - нулл, то ничего не делаем (такое бывает с этой
		// библиотекой)
		if (message.getBody() == null) {
			logger.debug("у мессаджа нулл, а пакет у нас "
					+ (packet != null ? packet.toXML() : "null"));
			return;
		}

		// принимаем только от оператора
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

		logger.debug("Посылаем в очередь" + message.getFrom() + " " + resp);
		// sendMessage(message.getFrom(), resp);
		bot.getQueue().add(new Info(InfoType.COMMON, message.getFrom(), resp));

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


	
}
