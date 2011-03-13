package com.temnenkov.jjbot.bot;

import org.jivesoftware.smack.packet.Packet;

public class ThreadProcessPkt extends Thread {

	private final Packet packet;
	private final Bot bot;
	
	public ThreadProcessPkt(String threadName, Packet packet, Bot bot) {
		super(threadName);
		this.packet = packet;
		this.bot = bot;
	}

	@Override
	public void run() {
		bot.safeProcessPacket(packet);		
	}
	
	
  
	
}
