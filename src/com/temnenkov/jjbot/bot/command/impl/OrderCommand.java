package com.temnenkov.jjbot.bot.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.command.CommonCommand;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;
import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.web.OrderInformer;

public class OrderCommand extends CommonCommand {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void getHelp(RequestSource source, StringBuilder sb) {
		sb.append("!USD список ордеров на покупку-продажу —Ўј\r\n");
		sb
				.append("!RUB список ордеров на покупку-продажу российских рублей\r\n");
		sb.append("!EUR список ордеров на покупку-продажу евро\r\n");
		sb.append("!JPY список ордеров на покупку-продажу €понской иены\r\n");
		sb.append("!YAD список ордеров на покупку-продажу яндекс.ƒенег\r\n");
		sb.append("!WMZ список ордеров на покупку-продажу WebMoney USD\r\n");
		sb.append("!WMR список ордеров на покупку-продажу WebMoney рублей\r\n");
	}

	@Override
	public void process(Request req, Responce resp) {
		if (req.getBody().startsWith("!") && (req.getBody().length() > 1)
				&& Pair.isPair(req.getBody().substring(1))) {
			logger.debug("это список ордеров");
			InfoWithHint res = OrderInformer.info(req.getBody().substring(1));
			if (res.getInfo() == null) {
				resp.print(res.getHint());
			} else
				resp.print(res.getInfo());
			resp.setStopped(true);
		}
	}

}
