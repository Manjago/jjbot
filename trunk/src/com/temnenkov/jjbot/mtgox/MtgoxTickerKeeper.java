package com.temnenkov.jjbot.mtgox;

public class MtgoxTickerKeeper {
	private MtgoxTicker ticker;

	public MtgoxTicker getTicker() {
		return ticker;
	}

	public void setTicker(MtgoxTicker ticker) {
		this.ticker = ticker;
	}

	@Override
	public String toString() {
		return "MtgoxTickerKeeper [ticker=" + ticker + "]";
	}
	
	public String toInfoString(){
	   if (ticker != null)
		   return ticker.toInfoString();
	   else
		   return "На mtgox.com временные (надеюсь) неполадки";
	}

}
