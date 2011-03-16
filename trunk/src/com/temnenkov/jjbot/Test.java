package com.temnenkov.jjbot;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.sqlite.SQLiteJDBCLoader;

import net.sf.json.JSONObject;

import com.temnenkov.jjbot.bot.LogManager;
import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.TickerKeeper;
import com.temnenkov.jjbot.mtgox.MtgoxTicker;
import com.temnenkov.jjbot.mtgox.MtgoxTickerKeeper;
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
		
		String json = HTTPRequestPoster.sendGetRequest(
				"http://mtgox.com/code/data/ticker.php", "");

		JSONObject jsonObject = JSONObject.fromObject(json);
		
		MtgoxTickerKeeper infoBean = (MtgoxTickerKeeper)JSONObject.toBean(jsonObject,
				MtgoxTickerKeeper.class);
		
		if (infoBean == null)
			System.out.println("null");
		else
			System.out.println(infoBean);
		
//		System.out.println(Pair.valueOf("YAD").getCode());
//		try
//		{
//			System.out.println(Pair.valueOf("1"));			
//		}
//		catch(IllegalArgumentException e)
//		{
//			System.out.println("!");
//		}
		
//		LogManager lm = new LogManager();
//		lm.init();
//		lm.storeMsg("ggg", "hhh", "testo", false);
		
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
