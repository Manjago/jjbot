package com.temnenkov.jjbot;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.sqlite.SQLiteJDBCLoader;

import net.sf.json.JSONObject;

import com.temnenkov.jjbot.bot.LogManager;
import com.temnenkov.jjbot.btcex.TickerKeeper;
import com.temnenkov.jjbot.util.HTTPRequestPoster;
import com.temnenkov.jjbot.util.Helper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		LogManager lm = new LogManager();
		lm.init();
		lm.storeMsg("ggg", "hhh", "testo", false);
		
//		Helper.lameTrust();
//		String res = HTTPRequestPoster.sendGetRequest("https://btcex.com/ticker.json", "");
//		System.out.println(res);
//
//		String json = "{data:"+res+"}";  
//		JSONObject jsonObject = JSONObject.fromObject( json );  
//		TickerKeeper testBean = (TickerKeeper) JSONObject.toBean( jsonObject, TickerKeeper.class );  
//		System.out.println(testBean);
	}

}
