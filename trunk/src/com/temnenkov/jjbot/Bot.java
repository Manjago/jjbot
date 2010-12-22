package com.temnenkov.jjbot;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.gate.GateKeeper;
import com.temnenkov.jjbot.gate.GateSession;

public class Bot {

	private ConnectionConfiguration connConfig;
	private XMPPConnection connection;
	private String username;
	private String password;
	private String tester;
	private GateKeeper gateKeeper;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public Bot(String username, String password, String tester) {
		this.username = username;
		this.password = password;
		this.tester = tester;
	}

	void start() throws XMPPException, InterruptedException {

		connConfig = new ConnectionConfiguration("talk.google.com", 5222,
				"gmail.com");

		connection = new XMPPConnection(connConfig);
		gateKeeper = new GateKeeper(connection);
		connection.connect();
		try {
			connection.login(username, password);
		} catch (XMPPException e) {
			logger.error("fail login with login=\"" + username + "\", pwd="
					+ password + "\"");
			throw e;
		}
		logger.info("login successfully");
		
		Roster roster = connection.getRoster();
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			logger.trace("found friend " + entry);
			//roster.removeEntry(entry);
			gateKeeper.addSession(entry.getUser());
		}	
		
		roster.addRosterListener(new RosterListener() {
			
			@Override
			public void presenceChanged(Presence arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void entriesUpdated(Collection<String> users) {
				for(String user: users)
					logger.trace("update friend " + user);
			}
			
			@Override
			public void entriesDeleted(Collection<String> users) {
				for(String user: users)
					logger.trace("delete friend " + user);
			}
			
			@Override
			public void entriesAdded(Collection<String> users) {
				for(String user: users){
					logger.trace("add friend " + user);
					String userName = Helper.extractUser(user);
					gateKeeper.addSession(user);
				}
			}
		});
		
		logger.info("started");

		while (true) {
			Thread.sleep(500);
		}

	}

}
