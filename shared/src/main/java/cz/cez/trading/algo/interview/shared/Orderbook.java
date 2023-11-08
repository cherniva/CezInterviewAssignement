package cz.cez.trading.algo.interview.shared;

import java.util.stream.Stream;

/**
 * Represents a trading orderbook (a list of currently available orders on the market).
 */
public interface Orderbook {

	/**
	 * Find orders of given side with best price for given product.
	 * Best price is meant from the point of view of the market,
	 * i.e. the price closest (or better) to the best price of the
	 * opposite side.
	 *
	 * @param product Product of the resulting orders.
	 * @param side Side of the resulting orders.
	 * @return All available orders with the best price.
	 * @see Order
	 */
	Stream<Order> getBestOrdersFor(String product, Side side);

//	void processOrder(Order order);
}
