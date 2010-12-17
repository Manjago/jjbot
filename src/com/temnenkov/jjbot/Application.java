package com.temnenkov.jjbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.jivesoftware.smack.XMPPException;

public class Application {

	public static void main(String[] args) {

		Properties prop = getPropertiesFile("bot.properties");
		if (prop == null)
			prop = getPropertiesFile("/opt/jjbot/bot.properties");
		
		if (prop == null){
			System.out.println("bot.properties not found");
			return;
		}

		Bot bot = new Bot(prop.getProperty("login"), prop
		.getProperty("password"), prop.getProperty("tester"));
		try {
			bot.start();
		} catch (XMPPException e) {
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
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
