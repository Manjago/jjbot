package com.temnenkov.jjbot.btcex.web;

import com.temnenkov.jjbot.btcex.Pair;
import com.temnenkov.jjbot.btcex.entity.InfoWithHint;
import com.temnenkov.jjbot.btcex.entity.Order;
import com.temnenkov.jjbot.btcex.entity.OrderKeeper;
import com.temnenkov.jjbot.util.HTTPRequestPoster;
import com.temnenkov.jjbot.util.Helper;

public class OrderInformer {

	public static InfoWithHint info(String curr){

		Pair pair;
		try
		{
			pair = Pair.valueOf(curr);
		}
		catch(IllegalArgumentException e){
			return new InfoWithHint(null, "Я не знаю о валюте \"" + 
					curr + "\", я знаю о валютах " + Pair.list());
		}
		
		String res = HTTPRequestPoster.sendGetRequest(
				"https://btcex.com/site/orders/" + pair.getCode(),"");
		
		if (Helper.isEmpty(res))
			return new InfoWithHint(null, "На btcex.com какие-то временные неполадки");
		
		// ламерская обработка
		String lameRes = res.replace("ask", " ask").replace("bid", " bid");
		if (lameRes.startsWith(" "))
			lameRes = lameRes.substring(1);
		
		String[] ordersRaw = lameRes.split(" ");
		OrderKeeper orders = new OrderKeeper();
		for(String raw : ordersRaw){
			orders.add(new Order(raw));
		}
		
		
		return new InfoWithHint(orders.toInfoString(), "");
		
	}
}
