package com.temnenkov.jjbot.bot.command;

public class CommandStatus {
	private final static String CRLF = "\r\n";
	private boolean stopped;
	private StringBuilder sb;

	public CommandStatus() {
		sb = new StringBuilder();
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
	
	public String getResponce(){
		return sb.toString();
	}
	
	public void print(String s){
		sb.append(s);
	}
	
	public void printLn(String s){
		print(s);
		sb.append(CRLF);
	}
  
}
