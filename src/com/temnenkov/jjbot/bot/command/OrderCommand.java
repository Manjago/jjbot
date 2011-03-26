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
		sb.append("!USD ������ ������� �� �������-������� ���\r\n");
		sb
				.append("!RUB ������ ������� �� �������-������� ���������� ������\r\n");
		sb.append("!EUR ������ ������� �� �������-������� ����\r\n");
		sb.append("!JPY ������ ������� �� �������-������� �������� ����\r\n");
		sb.append("!YAD ������ ������� �� �������-������� ������.�����\r\n");
		sb.append("!WMZ ������ ������� �� �������-������� WebMoney USD\r\n");
		sb.append("!WMR ������ ������� �� �������-������� WebMoney ������\r\n");
	}

	@Override
	public void process(String msg, CommandStatus commandStatus) {
		if (msg.startsWith("!") && (msg.length() > 1)
				&& Pair.isPair(msg.substring(1))) {
			logger.debug("��� ������ �������");
			InfoWithHint res = OrderInformer.info(msg.substring(1));
			if (res.getInfo() == null) {
				commandStatus.print(res.getHint());
			} else
				commandStatus.print(res.getInfo());
			commandStatus.setStopped(true);
		}
	}

}
