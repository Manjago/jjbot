package com.temnenkov.jjbot.bot.command.impl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.command.CommonCommand;
import com.temnenkov.jjbot.bot.command.Request;
import com.temnenkov.jjbot.bot.command.RequestSource;
import com.temnenkov.jjbot.bot.command.Responce;
import com.temnenkov.jjbot.util.Helper;

public class ExpectCommand extends CommonCommand {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final static String CMD = "EXPECT";
	private final ExpectDAO dao;

	public ExpectCommand() {
		dao = new ExpectDAO();
	}

	@Override
	public boolean init() {
		super.init();
		try {
			dao.init();
		} catch (ClassNotFoundException e) {
			logger.error("fail init dao - class not found", e);
			return false;
		} catch (SQLException e) {
			logger.error("fail init dao - sqlerr", e);
			return false;
		}
		return true;
	}

	@Override
	public void getHelp(RequestSource source, StringBuilder sb) {

		switch (source) {
		case PRIVATE:
			sb
					.append("EXPECT <текст> <время в целых часах> (пример - expect курс будет buy 60.6 4). EXPECT без параметров выведет текущие прогнозы\r\n");
			break;
		case MULTICHATPRIVATE:
			sb.append("EXPECT - текущие прогнозы\r\n");
			break;
		}

	}

	@Override
	public void process(Request req, Responce resp) {

		switch (req.getSource()) {
		case MULTICHATPRIVATE:
			if (req.getBody().equals(CMD)) {
				try {
					expect(resp);
				} catch (SQLException e) {
					logger.error("fail list expect", e);
					resp.print(e.getMessage());
				}
				resp.setStopped(true);
			}
			break;
		case PRIVATE:
			String body = req.getBody();
			String originalBody = req.getOriginalBody();

			if (!body.startsWith(CMD))
				return;

			if (body.equals(CMD)) {
				try {
					expect(resp);
				} catch (SQLException e) {
					logger.error("fail list expect", e);
					resp.print(e.getMessage());
				}
			} else {
				String[] tokens = originalBody.split(" ");
				if (tokens.length < 1 || !Helper.upper(tokens[0]).equals(CMD))
					badCommand(req, resp);
				else {
					// последнее попытаемся в число
					int hours = 0;
					boolean all = false;
					try {
						hours = Integer.parseInt(tokens[tokens.length - 1]);
					} catch (NumberFormatException e) {
						hours = 1;
						all = true;
					}
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < (all ? tokens.length
							: tokens.length - 1); ++i) {
						if (i != 1)
							sb.append(" ");
						sb.append(tokens[i]);
					}
					Expect item = new Expect();
					item.setAuthor(Helper.extractUser(req.getFrom()));
					item.setContent(sb.toString());
					item.setDeadTime(new DateTime().plusHours(hours));
					try {
						dao.update(item);
					} catch (SQLException e) {
						logger.error("fail update excpect", e);
						resp.print(e.getMessage());
					}
					try {
						expect(resp);
					} catch (SQLException e) {
						logger.error("fail send excpect", e);
						resp.print(e.getMessage());
					}
				}
			}

			resp.setStopped(true);
			break;
		}

	}

	private void badCommand(Request req, Responce resp) {
		resp.printLn("Что-то не так с командой \"" + req.getOriginalBody() + "\"");
	}

	private void expect(Responce resp) throws SQLException {
		List<Expect> expects = dao.getExpects();
		if (expects == null || expects.size() == 0) {
			resp.printLn("Никто ничего не предсказал!");
		} else {
			for (Expect s : expects) {
				resp.printLn(s.toInfoString());
			}
		}
	}

	@Override
	public Set<RequestSource> getSupportedTypes() {
		Set<RequestSource> result = new HashSet<RequestSource>();
		result.add(RequestSource.PRIVATE);
		result.add(RequestSource.MULTICHATPRIVATE);
		return result;
	}

}
