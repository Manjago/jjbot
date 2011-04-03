package com.temnenkov.jjbot.bot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.command.Command;

public class CommandManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final List<Command> commands;
	
	public CommandManager() {
		commands = new ArrayList<Command>();
	}

	public void add(Command command) {
		if (command.init())
			commands.add(command);
		else
			logger.error("Command {} not inited", command);			
	}

	public List<Command> getCommands() {
		return commands;
	}
	
	

}
