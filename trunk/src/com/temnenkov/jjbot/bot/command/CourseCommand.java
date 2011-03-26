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
		sb.append("USD ���� �������� ���\r\n");
		sb.append("RUB ���� ���������� ������\r\n");
		sb.append("EUR ���� ����\r\n");
		sb.append("JPY ���� �������� ����\r\n");
		sb.append("YAD ���� ������.�����\r\n");
		sb.append("WMZ ���� WebMoney USD\r\n");
		sb.append("WMR ���� WebMoney ������\r\n");
		sb.append("ALL ���� ���� ����������������� �����\r\n");
	}

	@Override
	public void process(String msg, CommandStatus commandStatus) {
		if (Pair.isPair(msg) || "ALL".equals(msg)) {
			logger.debug("��� ���� ������");
			InfoWithHint res = TickerInformer.info(msg);
			if (res.getInfo() == null){
				commandStatus.print("��������, � - ������ ���. � �� ������� ������� \"");
				commandStatus.print(msg);
				commandStatus.printLn("\".");
				commandStatus.printLn("�� �, ��������, ������� �������");
				commandStatus.print(res.getHint());
				commandStatus.printLn(",");
				commandStatus.printLn("ALL (����� ���� ����� �� �����)");
				commandStatus.printLn("��� ������ ������ ������ ������� ������� HELP");
			}
			else
				commandStatus.print(res.getInfo());
			commandStatus.setStopped(true);
		}
		
	}

}
