package com.temnenkov.jjbot.mtgox;

public class MtgoxTicker {
	private String high;
	private String low;
	private String vol;
	private String buy;
	private String sell;
	private String last;

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getVol() {
		return vol;
	}

	public void setVol(String vol) {
		this.vol = vol;
	}

	public String getBuy() {
		return buy;
	}

	public void setBuy(String buy) {
		this.buy = buy;
	}

	public String getSell() {
		return sell;
	}

	public void setSell(String sell) {
		this.sell = sell;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	@Override
	public String toString() {
		return "MtgoxTicker [buy=" + buy + ", high=" + high + ", last=" + last
				+ ", low=" + low + ", sell=" + sell + ", vol=" + vol + "]";
	}
	
	public String toInfoString(){
		return "MTGOX: buy=" + buy + ", sell=" + sell;		
	}

}
