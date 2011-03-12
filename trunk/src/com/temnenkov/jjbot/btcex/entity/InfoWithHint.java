package com.temnenkov.jjbot.btcex.entity;

public class InfoWithHint {
	private String info;
	private String hint;

	public InfoWithHint() {
	}

	public InfoWithHint(String info, String hint) {
		this();
		this.info = info;
		this.hint = hint;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	@Override
	public String toString() {
		return "InfoWithHint [hint=" + hint + ", info=" + info + "]";
	}

}
