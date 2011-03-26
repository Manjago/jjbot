package com.temnenkov.jjbot.bot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.mtgox.MtgoxTickerInformer;

public class MtGoxCommand implements Command {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void getHelp(StringBuilder sb) {
		sb.append("MTGOX курс с mtgox.com\r\n");
	}

	@Override
	public void process(String msg, CommandStatus commandStatus) {
		if ("MTGOX".equals(msg)) {
			logger.debug("это mtgox");
			InfoWithHint res = MtgoxTickerInformer.info();
			if (res.getInfo() == null)
				commandStatus.print(res.getHint());
			else
				commandStatus.print(res.getInfo());
			commandStatus.setStopped(true);

		}

	}

}
