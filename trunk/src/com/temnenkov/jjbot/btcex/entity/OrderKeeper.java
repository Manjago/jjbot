package com.temnenkov.jjbot.btcex.entity;

import java.util.ArrayList;
import java.util.List;

public class OrderKeeper {
	private final List<Order> orders;

	public OrderKeeper() {
		orders = new ArrayList<Order>();
	}

	@Override
	public String toString() {
		return "OrderKeeper [orders=" + orders + "]";
	}

	public String toInfoString() {
		StringBuilder sb = new StringBuilder();
		for (Order order : orders) {
			if (sb.length() != 0)
				sb.append("\r\n");
			sb.append(order.toInfoString());
		}
		if (sb.length() == 0)
			sb.append("Нет данных");
		return sb.toString();
	}

	public void add(Order order) {
		if ("ask".equals(order.getAskBid()) || "bid".equals(order.getAskBid()))
			orders.add(order);
	}

}
