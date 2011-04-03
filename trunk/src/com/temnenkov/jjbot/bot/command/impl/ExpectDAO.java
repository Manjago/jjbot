package com.temnenkov.jjbot.bot.command.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.util.Helper;

public class ExpectDAO {

	private Connection connection;
	private PreparedStatement updateCmd;
	private PreparedStatement getExpectsCmd;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ExpectDAO() {
	}

	public void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager
				.getConnection("jdbc:sqlite:/opt/jjbot/Expect.sqlite");

		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30);
		statement.executeUpdate("CREATE TABLE if not exists [expect] ([author] VARCHAR2(30) NOT NULL,[deadTime] DATETIME NOT NULL,[content] VARCHAR2(200),CONSTRAINT [] PRIMARY KEY ([author] COLLATE NOCASE ASC));");
		
		updateCmd = connection.prepareStatement("replace into expect (author, deadtime, content) values(?, ?, ?);");
		updateCmd.setQueryTimeout(30);
		
		getExpectsCmd = connection.prepareStatement("select author, deadTime, content from expect where deadTime > ?;");
		getExpectsCmd.setQueryTimeout(30);
	}

	public void update(Expect item) throws SQLException {
		
		logger.debug("update {}", item);
		
		updateCmd.setString(1, item.getAuthor());
		updateCmd.setString(2, Helper.toSqliteDate(item.getDeadTime()));
		updateCmd.setString(3, item.getContent() );

		updateCmd.executeUpdate();
	}

	public List<Expect> getExpects() throws SQLException {
		
		List<Expect> result = new ArrayList<Expect>();
		
		getExpectsCmd.setString(1, Helper.sqliteNow());

		ResultSet rs = getExpectsCmd.executeQuery();
		
		while(rs.next()){
		  Expect e = new Expect();
		  e.setAuthor(rs.getString(1));
		  e.setDeadTime(Helper.fromSqliteDate(rs.getString(2)));
		  e.setContent(rs.getString(3));
		  result.add(e);
		}
		
		return result;
	}

}
