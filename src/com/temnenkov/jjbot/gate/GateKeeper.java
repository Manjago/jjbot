package com.temnenkov.jjbot.gate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.Helper;

public class GateKeeper {
	private ConcurrentMap<String, GateSession> gateSessions;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private MessageListener defaultMessageListener;
	private final XMPPConnection conn;
	
	private int usernum = 24;

	public GateKeeper(XMPPConnection conn) {
		super();		
		this.conn = conn;
		gateSessions = new ConcurrentHashMap<String, GateSession>();
		
		defaultMessageListener = new MessageListener() {
			
			@Override
			public void processMessage(Chat ch, Message message) {
				logger.trace("Received message " + Helper.safeStr(message.getBody()) + " from " + message.getFrom());
				
				if (isFriend(message.getFrom())){
					String newMsg = "";
					try {

						newMsg = "Рур, "
								+ Helper.safeStr(message.getBody());

						ch.sendMessage(newMsg);
						logger.trace("Send message: " + newMsg);
						
						GateSession session = getSession(message.getFrom());
						MultiUserChat muc = session != null ? session.getMuc() : null;
						
						if (muc != null && !Helper.isEmpty(message.getBody())){
							muc.sendMessage(message.getBody());
							logger.trace("Send multimessage: " + message.getBody());
						}
						
							
					} catch (XMPPException e) {
						logger.error("fail send message \"" + newMsg
								+ "\"", e);
					}					
				} else
					logger.warn(message.getFrom() + " is enemy");
			}
		};
		
	}
	
	public GateSession getSession(String user){
		String username = Helper.extractUser(user);
		return gateSessions.get(username);		
	}
	
	public void addSession(String user){
		String username = Helper.extractUser(user);
		
		Chat chat = conn.getChatManager().createChat(username, defaultMessageListener);
		
		GateSession session = new GateSession(username, chat); 
		
		MultiUserChat muc2 = new MultiUserChat(conn, "tihotest@conference.jabber.ru");
		muc2.addMessageListener(session.getMucMessageListener());
		
		session.setMuc(muc2);


		DiscussionHistory history = new DiscussionHistory();
		history.setMaxStanzas(5);
		
		String nick = "user" + Integer.toString(++usernum); 
		
		
		try {
			muc2.join(nick, "", history, SmackConfiguration.getPacketReplyTimeout());
		} catch (XMPPException e) {
			logger.error("fail join multichat nick " + nick + " user " + user , e);
		}		
		
		gateSessions.put(username, session);
		logger.trace("add session " + user + " " + username);
	}	
	
	public boolean isFriend(String user){
		String username = Helper.extractUser(user);
		return gateSessions.containsKey(username);
	}

	@Override
	public String toString() {
		return "GateKeeper [conn=" + conn + ", defaultMessageListener="
				+ defaultMessageListener + ", gateSessions=" + gateSessions
				+ ", logger=" + logger + "]";
	}

	
}
