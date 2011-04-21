package com.temnenkov.jjbot.bot.command.impl;

import java.util.ArrayList;
import java.util.List;

import com.temnenkov.jjbot.util.Helper;

public class Aliases {

	private static List<AliasPair> aliases; 
	
	static {
		aliases = new ArrayList<AliasPair>();
		aliases.add(new AliasPair("������", "HELP "));
		aliases.add(new AliasPair("�����", "USD"));
		aliases.add(new AliasPair("�������", "USD"));
		aliases.add(new AliasPair("�����", "RUB"));
		aliases.add(new AliasPair("����", "EUR"));
		aliases.add(new AliasPair("����", "JPY"));
		aliases.add(new AliasPair("��", "YAD"));
		aliases.add(new AliasPair("���", "WMZ"));
		aliases.add(new AliasPair("���", "WMR"));
		aliases.add(new AliasPair("���", "ALL"));
		aliases.add(new AliasPair("������", "MTGOX"));
		aliases.add(new AliasPair("!�����", "!USD"));
		aliases.add(new AliasPair("!�������", "!USD"));
		aliases.add(new AliasPair("!�����", "!RUB"));
		aliases.add(new AliasPair("!����", "!EUR"));
		aliases.add(new AliasPair("!����", "!JPY"));
		aliases.add(new AliasPair("!��", "!YAD"));
		aliases.add(new AliasPair("!���", "!WMZ"));
		aliases.add(new AliasPair("!���", "!WMR"));
		aliases.add(new AliasPair("����������", "ALIAS"));
	}
	
	public static String translate(String src){
        
		String victim = Helper.upper(src);
		
		for(AliasPair item : aliases){
			if (item.itIsMyItem(victim))
				return item.getAliasValue();
		}
		
		return src;
	}
	
	public static String allAliases(){
		StringBuilder sb = new StringBuilder();
		for(AliasPair item : aliases){
			sb.append(item.toInfoStr());
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
}
