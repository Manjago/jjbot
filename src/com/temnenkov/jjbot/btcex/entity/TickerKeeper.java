package com.temnenkov.jjbot.btcex.entity;

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

	public String listCurr() {
		StringBuilder sb = new StringBuilder();
		for (Ticker i : getData()){
			
			if (sb.length() != 0)
				sb.append(",\r\n");
			
			sb.append(i.getCurrency());
			sb.append(" (курсы валюты \"");
			sb.append(i.getCurrency_name());
			sb.append("\")");
		}
		
		return sb.toString();
	}

	public String allCurr() {
		StringBuilder sb = new StringBuilder();
		for (Ticker i : getData()){
			
			if (sb.length() != 0)
				sb.append("\r\n");
			
			sb.append(i.toInfoString());
		}
		
		return sb.toString();
	}
}
