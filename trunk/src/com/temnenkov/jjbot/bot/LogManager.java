package com.temnenkov.jjbot.bot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.temnenkov.jjbot.util.Helper;

public class LogManager {

	private Connection connection;
	private PreparedStatement storeMsg;

	public void init() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager
				.getConnection("jdbc:sqlite:/opt/jjbot/ConfLog.sqlite");
		storeMsg = connection
				.prepareStatement("insert into Log ([Jid], [From], [Message], [Type]) values (?,?,?,?);");
	}

	public void storeMsg(String jid, String from, String payload, boolean isDelayed)
			throws SQLException {

		storeMsg.setString(1, jid);
		storeMsg.setString(2, Helper.extractUser(from));
		storeMsg.setString(3, Helper.safeStr(payload));
		storeMsg.setString(4, isDelayed ? "D" : "N");

		storeMsg.executeUpdate();
	}
}
