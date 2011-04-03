package com.temnenkov.jjbot.bot;

import java.util.HashMap;
import java.util.Map;

import com.temnenkov.jjbot.bot.command.Command;
import com.temnenkov.jjbot.bot.command.RequestSource;

public class HelpBuilder {

	private final Map<RequestSource, StringBuilder> sb;

	public HelpBuilder() {
		sb = new HashMap<RequestSource, StringBuilder>();

		for (RequestSource item : RequestSource.values()) {
			sb.put(item, new StringBuilder());
		}
	}

	public void append(String string) {
		for (RequestSource item : RequestSource.values()) {
			sb.get(item).append(string);
		}
	}

	public void addHelp(Command cmd) {
		for (RequestSource item : RequestSource.values()) {
			if (cmd.getSupportedTypes() == null
					|| cmd.getSupportedTypes().contains(item))
				cmd.getHelp(item, sb.get(item));
		}
	}

	public void toString(Map<RequestSource, String> helpTexts) {
		for (RequestSource item : RequestSource.values()) {
			helpTexts.put(item, sb.get(item).toString());
		}
	}

}
