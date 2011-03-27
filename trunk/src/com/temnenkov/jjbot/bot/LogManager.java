package com.temnenkov.jjbot.bot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.temnenkov.jjbot.util.Helper.toSqliteDate;
import static com.temnenkov.jjbot.util.Helper.fromSqliteDate;

import com.temnenkov.jjbot.util.Helper;

public class LogManager {

	private Connection connection;
	private PreparedStatement storeMsg;
	private PreparedStatement getLog;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	DateTimeFormatter logDateFormat = DateTimeFormat
			.forPattern("dd.MM.yyyy HH:mm:ss");

	public void init() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager
				.getConnection("jdbc:sqlite:/opt/jjbot/ConfLog.sqlite");
		storeMsg = connection
				.prepareStatement("insert into Log ([Jid], [From], [Message], [Type]) values (?,?,?,?);");
		getLog = connection
				.prepareStatement("select [From], Date, Message from Log where [Jid] = ? and date between ? and ? and [Type] = ? order by id asc;");
	}

	public void storeMsg(String from, String payload, boolean isDelayed)
			throws SQLException {

		storeMsg.setString(1, Helper.extractUser(from));
		storeMsg.setString(2, Helper.extractRoomNick(from));
		storeMsg.setString(3, Helper.safeStr(payload));
		storeMsg.setString(4, isDelayed ? "D" : "N");

		storeMsg.executeUpdate();
	}

	public void getLog(FileWriterWithEncoding fw, String jid,
			DateTime fromDate, DateTime tillDate) throws SQLException,
			IOException {

		logger.debug("getLog jid = " + jid + ", fromDate =" + fromDate
				+ ", tillDate= " + tillDate + ", sql=" + toSqliteDate(fromDate)
				+ " " + toSqliteDate(tillDate));

		getLog.setString(1, jid);
		getLog.setString(2, toSqliteDate(fromDate));
		getLog.setString(3, toSqliteDate(tillDate));
		getLog.setString(4, "N");

		ResultSet rs = getLog.executeQuery();
		while (rs.next()) {
			StringBuilder sb = new StringBuilder();
			sb.append(logDateFormat.print(fromSqliteDate(rs.getString(2))));
			sb.append(" ");
			sb.append("[");
			sb.append(rs.getString(1));
			sb.append("] ");
			sb.append(rs.getString(3));
			sb.append("\r\n");
			fw.write(sb.toString());
		}
	}

}
