package com.temnenkov.jjbot.bot;

import java.io.IOException;

import org.enterprisepower.net.NetUtils;
import org.enterprisepower.net.portforward.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortForwarding implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void run() {
		Listener listener;
		try {
			listener = new Listener(NetUtils.parseInetSocketAddress("5190"),
					NetUtils.parseInetSocketAddress("deepbit.net:8332"));
		} catch (IOException e) {
			logger.error("fail start port forwarding", e);
			return;
		}
		listener.run();

	}

}
