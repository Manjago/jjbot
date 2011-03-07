package com.temnenkov.jjbot.btcex;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.util.Helper;

public class TickerKeeper {
	private static Logger logger = LoggerFactory
	.getLogger(TickerKeeper.class);
	private List<Ticker> data;

	public List<Ticker> getData() {
		return data;
	}

	public void setData(List<Ticker> data) {
		this.data = data;
	}

	public Ticker find(String curr){
		if (Helper.isEmpty(curr)) return null;
		logger.debug("find " + curr + " " + this);
		
		try {
			for (Ticker i : getData()){
				logger.debug("test " + i);			
				if (curr.equals(i.getCurrency()))
					return i;
			}
			return null;
				
		}
		catch(Exception e){
			logger.debug("oops", e);
			return null;
		}
		
	}
	
	
	@Override
	public String toString() {
		return "TickerKeeper [data=" + data + "]";
	}

}
