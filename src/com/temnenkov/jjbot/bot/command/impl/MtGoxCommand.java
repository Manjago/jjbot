package com.temnenkov.jjbot.bot.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.command.CommonCommand;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.mtgox.MtgoxTickerInformer;

public class MtGoxCommand extends CommonCommand {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void getHelp(RequestSource source, StringBuilder sb) {
		sb.append("MTGOX курс с mtgox.com\r\n");
	}

	@Override
	public void process(Request req, Responce resp) {
		if ("MTGOX".equals(req.getBody())) {
			logger.debug("это mtgox");
			InfoWithHint res = MtgoxTickerInformer.info();
			if (res.getInfo() == null)
				resp.print(res.getHint());
			else
				resp.print(res.getInfo());
			resp.setStopped(true);

		}

	}

}
