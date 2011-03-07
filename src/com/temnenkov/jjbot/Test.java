package com.temnenkov.jjbot;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import com.temnenkov.jjbot.btcex.TickerKeeper;
import com.temnenkov.jjbot.util.HTTPRequestPoster;
import com.temnenkov.jjbot.util.Helper;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Helper.lameTrust();
		String res = HTTPRequestPoster.sendGetRequest("https://btcex.com/ticker.json", "");
		System.out.println(res);

		String json = "{data:"+res+"}";  
		JSONObject jsonObject = JSONObject.fromObject( json );  
		TickerKeeper testBean = (TickerKeeper) JSONObject.toBean( jsonObject, TickerKeeper.class );  
		System.out.println(testBean);
	}

}
