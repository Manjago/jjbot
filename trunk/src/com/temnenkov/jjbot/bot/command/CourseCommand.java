package com.temnenkov.jjbot.bot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.web.TickerInformer;

public class CourseCommand implements Command {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void getHelp(StringBuilder sb) {
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
	public void process(String msg, CommandStatus commandStatus) {
		if (Pair.isPair(msg) || "ALL".equals(msg)) {
			logger.debug("это курс валюты");
			InfoWithHint res = TickerInformer.info(msg);
			if (res.getInfo() == null){
				commandStatus.print("»звините, € - глупый бот. я не понимаю команду \"");
				commandStatus.print(msg);
				commandStatus.printLn("\".");
				commandStatus.printLn("Ќо €, например, понимаю команды");
				commandStatus.print(res.getHint());
				commandStatus.printLn(",");
				commandStatus.printLn("ALL (курсы всех валют на бирже)");
				commandStatus.printLn("ƒл€ вывода списка команд введите команду HELP");
			}
			else
				commandStatus.print(res.getInfo());
			commandStatus.setStopped(true);
		}
		
	}

}
