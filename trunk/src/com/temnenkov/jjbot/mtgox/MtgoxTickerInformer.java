package com.temnenkov.jjbot.mtgox;

import net.sf.json.JSONObject;

import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.util.HTTPRequestPoster;

public class MtgoxTickerInformer {

	public static InfoWithHint info(){
		
		String json = HTTPRequestPoster.sendGetRequest(
				"http://mtgox.com/code/data/ticker.php", "");

		JSONObject jsonObject = JSONObject.fromObject(json);
		
		MtgoxTickerKeeper infoBean = (MtgoxTickerKeeper)JSONObject.toBean(jsonObject,
				MtgoxTickerKeeper.class);
		
		if (infoBean == null)
			return new InfoWithHint(null, "На mtgox.com временные неполадки");
		else
			return new InfoWithHint(infoBean.toInfoString(), "");
		
	}
	
}
