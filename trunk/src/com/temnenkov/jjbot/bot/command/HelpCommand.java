package com.temnenkov.jjbot.bot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpCommand implements Command{

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String helpString = "";
	
	@Override
	public void getHelp(StringBuilder sb) {
		sb.append("HELP это сообщение\r\n");
	}

	@Override
	public void process(String msg, CommandStatus commandStatus) {
		if ("HELP".equals(msg)) {
			logger.debug("это запрос помощи");
			commandStatus.print(helpString);
			commandStatus.setStopped(true);
		} 
		
	}

	public String getHelpString() {
		return helpString;
	}

	public void setHelpString(String helpString) {
		this.helpString = helpString;
	}

}
