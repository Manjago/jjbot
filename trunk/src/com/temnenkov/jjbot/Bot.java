package com.temnenkov.jjbot;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Bot {

	private ConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private String username;
	private String password;
	private String tester;

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
		        System.out.println("Received message: " + message);
		    }

		});
		chat.sendMessage("Тест");
		
		
		while(true){
		  Thread.sleep(500);	
		}
		
	}

}
