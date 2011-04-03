package com.temnenkov.jjbot.bot.command.impl;

import java.util.HashMap;
import java.util.Map;

import com.temnenkov.jjbot.bot.command.CommonCommand;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;

public class UnknownCommand extends CommonCommand {
	private final Map<RequestSource, String> helpStrings;

	public UnknownCommand() {
		helpStrings = new HashMap<RequestSource, String>();
	}

	@Override
	public void getHelp(RequestSource source, StringBuilder sb) {
	}

	@Override
	public void process(Request req, Responce resp) {
		StringBuilder sb = new StringBuilder();
		sb.append("Я не знаю команду \"" + req.getBody() + "\"\r\n");
		sb.append(getHelpString(req.getSource()));
		resp.print(sb.toString());
		resp.setStopped(true);
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
