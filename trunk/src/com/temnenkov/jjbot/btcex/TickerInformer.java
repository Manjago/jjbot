package com.temnenkov.jjbot.btcex;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.util.HTTPRequestPoster;

public class TickerInformer {
	private static Logger logger = LoggerFactory
			.getLogger(TickerInformer.class);

	public static String info(String curr) {
		logger.debug("get ifo about curr " + curr);
		String res = HTTPRequestPoster.sendGetRequest(
				"https://btcex.com/ticker.json", "");
		logger.debug("req ok = " + res);

		String json = "{data:" + res + "}";
		JSONObject jsonObject = JSONObject.fromObject(json);
		logger.debug("jsonObject ok");

		Map<String, Class<Ticker>> classMap = new HashMap<String, Class<Ticker>>();
		classMap.put("data", Ticker.class);

		TickerKeeper testBean = (TickerKeeper) JSONObject.toBean(jsonObject,
				TickerKeeper.class, classMap);
		if (testBean != null) {
			logger.debug("testBean ok");

		} else {
			logger.debug("testBean null");
			return null;
		}

		Ticker inf = testBean.find(curr);
		if (inf != null) {
			logger.debug("inf ok = " + inf);
			return inf.toInfoString();
		} else {
			logger.debug("inf not found");
			return null;
		}
	}
}
