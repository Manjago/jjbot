package com.temnenkov.jjbot.bot.command.impl;

import com.temnenkov.jjbot.util.Helper;

public class AliasPair {
	private final String alias;
	private final String aliasValue;

	AliasPair(String alias, String aliasValue) {
		super();
		this.alias = Helper.upper(alias);
		this.aliasValue = Helper.upper(aliasValue);
	}

	public String getAlias() {
		return alias;
	}

	public String getAliasValue() {
		return aliasValue;
	}

	boolean itIsMyItem(String src) {
		return (src != null) && getAlias().startsWith(src);
	}
	
	String toInfoStr(){
		StringBuilder sb = new StringBuilder();
		sb.append(getAlias());
		sb.append(" ");
		sb.append(getAliasValue());
		return sb.toString();
	}

}
