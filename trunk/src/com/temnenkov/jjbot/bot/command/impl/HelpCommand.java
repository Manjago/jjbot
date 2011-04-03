package com.temnenkov.jjbot.bot.command.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.command.CommonCommand;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;

public class HelpCommand extends CommonCommand{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<RequestSource, String> helpStrings;
	
	public HelpCommand() {
		helpStrings = new HashMap<RequestSource, String>();
	}

	@Override
	public void getHelp(RequestSource source, StringBuilder sb) {
		sb.append("HELP это сообщение\r\n");
	}

	@Override
	public void process(Request req, Responce resp) {
		if ("HELP".equals(req.getBody())) {
			logger.debug("это запрос помощи");
			resp.print(getHelpString(req.getSource()));
			resp.setStopped(true);
		} 
		
	}

	public String getHelpString(RequestSource source) {
		if (helpStrings.containsKey(source))
			return helpStrings.get(source);
		else
			return "";
	}

	public Map<RequestSource, String> getHelpStrings() {
		return helpStrings;
	}

}
