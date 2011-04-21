package com.temnenkov.jjbot.bot.command.impl;

import java.util.ArrayList;
import java.util.List;

import com.temnenkov.jjbot.util.Helper;

public class Aliases {

	private static List<AliasPair> aliases; 
	
	static {
		aliases = new ArrayList<AliasPair>();
		aliases.add(new AliasPair("помощь", "HELP"));
		aliases.add(new AliasPair("баксы", "USD"));
		aliases.add(new AliasPair("доллары", "USD"));
		aliases.add(new AliasPair("рубли", "RUB"));
		aliases.add(new AliasPair("евро", "EUR"));
		aliases.add(new AliasPair("иены", "JPY"));
		aliases.add(new AliasPair("яд", "YAD"));
		aliases.add(new AliasPair("вмз", "WMZ"));
		aliases.add(new AliasPair("вмр", "WMR"));
		aliases.add(new AliasPair("все", "ALL"));
		aliases.add(new AliasPair("мтгокс", "MTGOX"));
		aliases.add(new AliasPair("!баксы", "!USD"));
		aliases.add(new AliasPair("!доллары", "!USD"));
		aliases.add(new AliasPair("!рубли", "!RUB"));
		aliases.add(new AliasPair("!евро", "!EUR"));
		aliases.add(new AliasPair("!иены", "!JPY"));
		aliases.add(new AliasPair("!яд", "!YAD"));
		aliases.add(new AliasPair("!вмз", "!WMZ"));
		aliases.add(new AliasPair("!вмр", "!WMR"));
		aliases.add(new AliasPair("псевдонимы", "ALIAS"));
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
