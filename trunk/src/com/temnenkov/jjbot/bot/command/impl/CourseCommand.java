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
	public void process(Request req, Responce resp) {
		
		String token = Aliases.translate(req.getBody());
		
		if (Pair.isPair(token) || "ALL".equals(token)) {
			logger.debug("��� ���� ������");
			InfoWithHint res = TickerInformer.info(token);
			if (res.getInfo() == null){
				resp.print("��������, � - ������ ���. � �� ������� ������� \"");
				resp.print(req.getBody());
				resp.printLn("\".");
				resp.printLn("�� �, ��������, ������� �������");
				resp.print(res.getHint());
				resp.printLn(",");
				resp.printLn("ALL (����� ���� ����� �� �����)");
				resp.printLn("��� ������ ������ ������ ������� ������� HELP");
			}
			else
				resp.print(res.getInfo());
			resp.setStopped(true);
		}
		
	}

}
