package com.temnenkov.jjbot.btcex;

public enum Pair {
	USD("2"), RUB("1"), EUR("3"), JPY("5"), YAD("150"), WMZ("200"), WMR("400");

	private final String code;

	public String getCode() {
		return code;
	}

	Pair(String code) {
		this.code = code;
	}

	public static String list() {
		StringBuilder sb = new StringBuilder();
		for (Pair p : values()) {
			if (sb.length() != 0)
				sb.append(", ");
			sb.append(p);
		}

		return sb.toString();
	}

	public static  boolean isPair(String curr) {
		try {
			Pair.valueOf(curr);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
