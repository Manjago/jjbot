package com.temnenkov.jjbot.btcex.entity;

public class Order {
	private String askBid;
	private String rate;
	private String volume;

	public Order() {
		askBid = "";
		rate = "";
		volume = "";
	}

	public Order(String raw) {
		this();
		String[] items = raw.split(",");
		if (items.length >= 1)
			askBid = items[0];
		if (items.length >= 2)
			rate = items[1];
		if (items.length >= 3)
			volume = items[2];
	}

	public String getAskBid() {
		return askBid;
	}

	public void setAskBid(String askBid) {
		this.askBid = askBid;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		return "Order [askBid=" + askBid + ", rate=" + rate + ", volume="
				+ volume + "]";
	}

	public String toInfoString() {
		return askBid + " " + rate + " " + volume;
	}

}
