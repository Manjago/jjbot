package com.temnenkov.jjbot;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.joda.time.DateTime;

import com.temnenkov.jjbot.bot.LogManager;
import com.temnenkov.jjbot.bot.PortForwarding;

public class Test {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException {

		final Executor exec = Executors.newSingleThreadExecutor();
		exec.execute(new PortForwarding());
		
//		
//		LogManager lm = new LogManager();
//		lm.init();
//		
//
//		DateTime now = new DateTime();
//		DateTime yesterday = new DateTime(2011,3,23,0,0,0,0);  //now.minusDays(1).minusYears(1);
//		DateTime fromDate = yesterday.withHourOfDay(0).withMinuteOfHour(0)
//				.withSecondOfMinute(0).withMillisOfSecond(0);
//		DateTime tillDate = yesterday.withHourOfDay(23).withMinuteOfHour(59)
//				.withSecondOfMinute(59).withMillisOfSecond(999);
//
//		File file = new File("/home/jjbot/test.txt");
//		FileWriterWithEncoding fw = new FileWriterWithEncoding(file, Charset
//				.forName("866"));
//		try {
//			lm.getLog(fw, "bitcoin@conference.jabber.ru", fromDate, tillDate);
//		} catch (IOException e) {
//
//		} finally {
//			fw.close();
//		}

		// String json = HTTPRequestPoster.sendGetRequest(
		// "http://mtgox.com/code/data/ticker.php", "");
		//
		// JSONObject jsonObject = JSONObject.fromObject(json);
		//		
		// MtgoxTickerKeeper infoBean =
		// (MtgoxTickerKeeper)JSONObject.toBean(jsonObject,
		// MtgoxTickerKeeper.class);
		//		
		// if (infoBean == null)
		// System.out.println("null");
		// else
		// System.out.println(infoBean);

		// System.out.println(Pair.valueOf("YAD").getCode());
		// try
		// {
		// System.out.println(Pair.valueOf("1"));
		// }
		// catch(IllegalArgumentException e)
		// {
		// System.out.println("!");
		// }

		// LogManager lm = new LogManager();
		// lm.init();
		// lm.storeMsg("ggg", "hhh", "testo", false);

		// Helper.lameTrust();
		// String res =
		// HTTPRequestPoster.sendGetRequest("https://btcex.com/ticker.json",
		// "");
		// System.out.println(res);
		//
		// String json = "{data:"+res+"}";
		// JSONObject jsonObject = JSONObject.fromObject( json );
		// TickerKeeper testBean = (TickerKeeper) JSONObject.toBean( jsonObject,
		// TickerKeeper.class );
		// System.out.println(testBean);
	}

}
