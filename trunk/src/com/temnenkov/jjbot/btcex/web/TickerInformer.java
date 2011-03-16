package com.temnenkov.jjbot.btcex.web;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.entity.Ticker;
import com.temnenkov.jjbot.btcex.entity.TickerKeeper;
import com.temnenkov.jjbot.util.HTTPRequestPoster;

public class TickerInformer {
	private static Logger logger = LoggerFactory
			.getLogger(TickerInformer.class);

	public static InfoWithHint info(String curr) {
		logger.debug("get info about curr " + curr);
		String res = HTTPRequestPoster.sendGetRequest(
				"https://btcex.com/ticker.json", "");
		logger.debug("req ok = " + res);

		String json = "{data:" + res + "}";
		JSONObject jsonObject = JSONObject.fromObject(json);
		logger.debug("jsonObject ok");

		Map<String, Class<Ticker>> classMap = new HashMap<String, Class<Ticker>>();
		classMap.put("data", Ticker.class);

		TickerKeeper infoBean = (TickerKeeper) JSONObject.toBean(jsonObject,
				TickerKeeper.class, classMap);
		if (infoBean != null) {
			logger.debug("testBean ok");

		} else {
			logger.debug("testBean null");
			return new InfoWithHint(null, "Временные проблемы на btcex.com");
		}

		if ("ALL".equals(curr)){
			return new InfoWithHint(infoBean.allCurr(), "");
		} else {
			Ticker inf = infoBean.find(curr);
			if (inf != null) {
				logger.debug("inf ok = " + inf);
				return new InfoWithHint(inf.toInfoString(), "");
			} else {
				logger.debug("inf not found");
				return new InfoWithHint(null, infoBean.listCurr());
			}			
		}
		
		
	}
}
