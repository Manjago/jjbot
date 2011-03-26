package com.temnenkov.jjbot.bot.command;

public interface Command {

	void process(String msg, CommandStatus commandStatus);

	void getHelp(StringBuilder sb);
}
