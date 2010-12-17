package com.temnenkov.jjbot;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bot {

	private ConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private String username;
	private String password;
	private String tester;

	private Logger logger = LoggerFactory
	.getLogger(getClass());	
	
	public Bot(String username, String password, String tester) {
		this.username = username;
		this.password = password;
		this.tester = tester;
	}

	void start() throws XMPPException, InterruptedException {

		connConfig = new ConnectionConfiguration("talk.google.com", 5222,
				"gmail.com");

		connection = new XMPPConnection(connConfig);
		connection.connect();
		connection.login(username, password);
		
		Chat chat = connection.getChatManager().createChat(tester, new MessageListener() {

			@Override
		    public void processMessage(Chat chat, Message message) {
				
				String logMessage = Helper.toString(message);
				
				logger.trace("Received message: " + logMessage);
				String logNewMessage = "";
				try {
					
				    Message newMsg = Helper.createChatMessage(chat.getParticipant(), "Ага, " + Helper.safeStr(message.getBody()));
				    logNewMessage = newMsg.getTo() + ":" + newMsg.getBody();
					
					chat.sendMessage(newMsg);
					logger.trace("Send message: " + logNewMessage);
				} catch (XMPPException e) {
					logger.error("fail send message \"" + logNewMessage + "\"", e);
				}
		    }

		});
		chat.sendMessage("Тест");
		
		
		while(true){
		  Thread.sleep(500);	
		}
		
	}

}
