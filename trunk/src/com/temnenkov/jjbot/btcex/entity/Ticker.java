package com.temnenkov.jjbot.btcex.entity;

public class Ticker {
	private String currency;
	private String currency_name;
	private String bid;
	private String ask;
	private String lastTradedPrice;
	private String lastTradedQuantity;
	private String last24HrsTradedQuantity;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCurrency_name() {
		return currency_name;
	}

	public void setCurrency_name(String currencyName) {
		currency_name = currencyName;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getAsk() {
		return ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public String getLastTradedPrice() {
		return lastTradedPrice;
	}

	public void setLastTradedPrice(String lastTradedPrice) {
		this.lastTradedPrice = lastTradedPrice;
	}

	public String getLastTradedQuantity() {
		return lastTradedQuantity;
	}

	public void setLastTradedQuantity(String lastTradedQuantity) {
		this.lastTradedQuantity = lastTradedQuantity;
	}

	public String getLast24HrsTradedQuantity() {
		return last24HrsTradedQuantity;
	}

	public void setLast24HrsTradedQuantity(String last24HrsTradedQuantity) {
		this.last24HrsTradedQuantity = last24HrsTradedQuantity;
	}

	public String toInfoString() {
		return currency_name + ": bid=" + bid + ", ask=" + ask;

	}

	@Override
	public String toString() {
		return "Ticker [ask=" + ask + ", bid=" + bid + ", currency=" + currency
				+ ", currency_name=" + currency_name
				+ ", last24HrsTradedQuantity=" + last24HrsTradedQuantity
				+ ", lastTradedPrice=" + lastTradedPrice
				+ ", lastTradedQuantity=" + lastTradedQuantity + "]";
	}

}
