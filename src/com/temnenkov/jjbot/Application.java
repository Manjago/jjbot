package com.temnenkov.jjbot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temnenkov.jjbot.bot.Bot;
import com.temnenkov.jjbot.util.Helper;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class Application {

	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws FileNotFoundException {

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = null;
		try {
			configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("/opt/jjbot/logback.xml");
		} catch (JoranException e) {
			logger.error("fail process /opt/jjbot/logback.xml", e);
			return;
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

		Properties prop = getPropertiesFile("bot.properties");
		if (prop == null)
			prop = getPropertiesFile("/opt/jjbot/bot.properties");

		if (prop == null) {
			logger.error("bot.properties not found");
			return;
		}

		Helper.lameTrust();
		try {
			Bot.start(prop.getProperty("login"), prop.getProperty("password"),
					prop.getProperty("tester"), prop.getProperty("operator"),
					prop.getProperty("room"), prop.getProperty("roomnick"));
		} catch (XMPPException e) {
			logger.error("XMPPException", e);
			return;
		} catch (InterruptedException e) {
			logger.error("InterruptedException", e);
			return;
		} catch (SQLException e) {
			logger.error("SQLException", e);
			return;
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException", e);
			return;
		}
	}

	private static Properties getPropertiesFile(String name) {
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(name));
		} catch (IOException e) {
			return null;
		}
		return prop;
	}

}
