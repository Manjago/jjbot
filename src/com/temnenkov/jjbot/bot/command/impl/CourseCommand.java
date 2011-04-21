package com.temnenkov.jjbot.bot.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.command.CommonCommand;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;
import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.web.TickerInformer;

public class CourseCommand extends CommonCommand {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void getHelp(RequestSource source, StringBuilder sb) {
		sb.append("USD курс долларов —Ўј\r\n");
		sb.append("RUB курс российских рублей\r\n");
		sb.append("EUR курс евро\r\n");
		sb.append("JPY курс €понской иены\r\n");
		sb.append("YAD курс яндекс.ƒенег\r\n");
		sb.append("WMZ курс WebMoney USD\r\n");
		sb.append("WMR курс WebMoney рублей\r\n");
		sb.append("ALL курс всех вышеперечисленных валют\r\n");
	}

	@Override
	public void process(Request req, Responce resp) {
		
		String token = Aliases.translate(req.getBody());
		
		if (Pair.isPair(token) || "ALL".equals(token)) {
			logger.debug("это курс валюты");
			InfoWithHint res = TickerInformer.info(token);
			if (res.getInfo() == null){
				resp.print("»звините, € - глупый бот. я не понимаю команду \"");
				resp.print(req.getBody());
				resp.printLn("\".");
				resp.printLn("Ќо €, например, понимаю команды");
				resp.print(res.getHint());
				resp.printLn(",");
				resp.printLn("ALL (курсы всех валют на бирже)");
				resp.printLn("ƒл€ вывода списка команд введите команду HELP");
			}
			else
				resp.print(res.getInfo());
			resp.setStopped(true);
		}
		
	}

}
