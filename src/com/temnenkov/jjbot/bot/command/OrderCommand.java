package com.temnenkov.jjbot.bot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.web.OrderInformer;

public class OrderCommand implements Command {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void getHelp(StringBuilder sb) {
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
	public void process(String msg, CommandStatus commandStatus) {
		if (msg.startsWith("!") && (msg.length() > 1)
				&& Pair.isPair(msg.substring(1))) {
			logger.debug("это список ордеров");
			InfoWithHint res = OrderInformer.info(msg.substring(1));
			if (res.getInfo() == null) {
				commandStatus.print(res.getHint());
			} else
				commandStatus.print(res.getInfo());
			commandStatus.setStopped(true);
		}
	}

}
