package com.temnenkov.jjbot.bot.command;

public class UnknownCommand implements Command {
	private String helpString = "";

	@Override
	public void getHelp(StringBuilder sb) {
	}

	@Override
	public void process(String msg, CommandStatus commandStatus) {
		StringBuilder sb = new StringBuilder();
		sb.append("Я не знаю команду \"" + msg + "\"\r\n");
		sb.append(helpString);
		commandStatus.print(sb.toString());
		commandStatus.setStopped(true);
	}

	public String getHelpString() {
		return helpString;
	}

	public void setHelpString(String helpString) {
		this.helpString = helpString;
	}

}
