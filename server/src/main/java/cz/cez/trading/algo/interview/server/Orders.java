package cz.cez.trading.algo.interview.server;

import static cz.cez.trading.algo.interview.shared.Operation.DELETE;
import static cz.cez.trading.algo.interview.shared.Operation.SET;
import static cz.cez.trading.algo.interview.shared.Side.ASK;
import static cz.cez.trading.algo.interview.shared.Side.BID;
import static java.util.UUID.randomUUID;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import cz.cez.trading.algo.interview.shared.Order;
import cz.cez.trading.algo.interview.shared.Side;

public class Orders {
	private static final String[] PRODUCTS = new String[] {"ttf-jan-2023", "ttf-feb-2023", "ttf-mar-2023", "ttf-apr-2023"};
	private static final double[] BID_PRICES = new double[] {99.9, 99.925, 99.95, 99.975};
	private static final double[] ASK_PRICES = new double[] {100, 100.025, 100.05, 100.075, 100.1};
	private final Map<String, Order> orders;
	private final Random random;
	public Orders() {
		this.orders = new HashMap<>();
		this.random = new Random(123456);
	}

	public Order doRandomChange() {
		final int size = this.orders.size();
		if (size == 0) {
			return this.createRandom();
		} else {
			final int operation = size - this.random.nextInt(size);
			if (operation > 10) {
				return this.deleteRandom();
			} else {
				if (this.random.nextBoolean()) {
					if (this.random.nextBoolean()) {
						return this.modifyRandom();
					} else {
						return this.deleteRandom();
					}
				} else {
					return this.createRandom();
				}
			}
		}
	}

	private Order modifyRandom() {
		final Order order = this.orders.get(this.randomKey());
		final Order result = new Order(order);
		final double[] prices = this.getPrices(order.getSide());
		result.setPrice(prices[this.random.nextInt(prices.length)]);
		this.orders.put(order.getId(), result);
		return result;
	}

	private Order deleteRandom() {
		final Order order = this.orders.get(this.randomKey());
		final Order result = new Order(order);
		result.setOperation(DELETE);
		this.orders.remove(order.getId());
		return result;
	}

	private String randomKey() {
		final int key = this.random.nextInt(this.orders.size() + 1);
		final Iterator<String> iterator = this.orders.keySet().iterator();
		int i = 0;
		String next;
		do {
			next = iterator.next();
		} while (++i < key);
		return next;
	}

	private Order createRandom() {
		final Side side = this.random.nextBoolean() ? BID : ASK;
		final double[] prices = this.getPrices(side);
		final Order order = new Order(randomUUID().toString(),
				PRODUCTS[this.random.nextInt(PRODUCTS.length)],
				side, 10, prices[this.random.nextInt(prices.length)], SET);
		this.orders.put(order.getId(), order);
		return order;
	}

	private double[] getPrices(Side side) {
		switch (side) {
		case BID: return BID_PRICES;
		case ASK: return ASK_PRICES;
		default: throw new RuntimeException();
		}
	}
}
