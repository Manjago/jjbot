package com.temnenkov.jjbot.bot;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomInfo {
	private final MultiUserChat muc;
	private final String nickname;
	private final Logger logger = LoggerFactory.getLogger(Bot.class);

	public RoomInfo(MultiUserChat muc, String nickname) {
		super();
		this.muc = muc;
		this.nickname = nickname;
	}

	public MultiUserChat getMuc() {
		return muc;
	}

	public String getNickname() {
		return nickname;
	}

	public void join() throws XMPPException {
		DiscussionHistory history = new DiscussionHistory();
		history.setMaxStanzas(5);
		muc.join(nickname, "", history, SmackConfiguration
				.getPacketReplyTimeout());
		logger.info("join room " + muc.getRoom() + " as " + nickname + " ok");
	}

	@Override
	public String toString() {
		return "RoomInfo [muc=" + muc + ", nickname=" + nickname + "]";
	}

}
