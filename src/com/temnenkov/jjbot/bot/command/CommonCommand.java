package com.temnenkov.jjbot.bot.command;

import java.util.Set;

public abstract class CommonCommand implements Command {

	@Override
	public Set<RequestSource> getSupportedTypes() {
		return null;
	}

	@Override
	public boolean init() {
		return true;
	}

}
