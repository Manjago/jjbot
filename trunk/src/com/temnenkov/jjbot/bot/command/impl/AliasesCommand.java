package com.temnenkov.jjbot.bot.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.command.CommonCommand;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;

public class AliasesCommand extends CommonCommand {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void getHelp(RequestSource source, StringBuilder sb) {
		sb.append("ALIAS список алиасов (псевдонимов) для команд\r\n");
	}

	@Override
	public void process(Request req, Responce resp) {
		String token = Aliases.translate(req.getBody());
		
		if ("ALIAS".equals(token)){
			logger.debug("это ALIAS");
			resp.print(Aliases.allAliases());
			resp.print("Можно вводить первые несколько символов, будет выбрано первое подходящее соответствие. Большие, маленькие буквы - все равно.");
			resp.setStopped(true);
		}
		
	}

}
