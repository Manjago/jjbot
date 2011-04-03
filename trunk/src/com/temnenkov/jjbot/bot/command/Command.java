package com.temnenkov.jjbot.bot.command;

import java.util.Set;

public interface Command {

	void process(Request req, Responce resp);

	void getHelp(RequestSource source, StringBuilder sb);
	
	Set<RequestSource> getSupportedTypes();
	
	boolean init();
}
