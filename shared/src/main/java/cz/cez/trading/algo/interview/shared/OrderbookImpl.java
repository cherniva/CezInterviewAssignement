package cz.cez.trading.algo.interview.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Component
public class OrderbookImpl implements Orderbook {

    //TODO: tests

    //TODO: add querying for middle price, best price, volume, spread

    private static final Logger LOG = LoggerFactory.getLogger(OrderbookImpl.class);

    private HashMap<String, HashMap<Side, PriceOrderMap>> orderBook;
    private HashMap<String, Order> ordersMap;

    public OrderbookImpl() {
        this.orderBook = new HashMap<>();
        List.of("ttf-jan-2023", "ttf-feb-2023", "ttf-mar-2023", "ttf-apr-2023")
                .forEach(s -> {
                    HashMap<Side, PriceOrderMap> askBidMap = new HashMap<>();
                    askBidMap.put(Side.ASK, new PriceOrderMap(Side.ASK));
                    askBidMap.put(Side.BID, new PriceOrderMap(Side.BID));
                    orderBook.put(s, askBidMap);
                });
        this.ordersMap = new HashMap<>();
    }

    public void processOrder(Order order) {
        switch(order.getOperation()) {
            case SET -> setOrder(order);
            case DELETE -> deleteOrder(order);
        }
    }

    private void setOrder(Order order) {
        try {
            Order existingOrder = ordersMap.get(order.getId());
            if(existingOrder.getPrice() != order.getPrice()) {
                PriceOrderMap priceOrderMap = orderBook.get(order.getProduct()).get(order.getSide());
                priceOrderMap.remove(existingOrder);
                existingOrder.setPrice(order.getPrice());
                priceOrderMap.insert(existingOrder);
            }
        }
        catch (NullPointerException e) {
            ordersMap.put(order.getId(), order);
            PriceOrderMap orderTree = orderBook.get(order.getProduct()).get(order.getSide());
            orderTree.insert(order);
        }
    }

    private void deleteOrder(Order order) {
        PriceOrderMap orderTree = orderBook.get(order.getProduct()).get(order.getSide());
        orderTree.remove(order);
        ordersMap.remove(order.getId());
    }

    @Override
    public Stream<Order> getBestOrdersFor(String product, Side side) {
        return orderBook.get(product).get(side).getRoot().values().stream();
    }


    @Async
    public CompletableFuture<Stream<Order>> getBestOrdersForAsync(String product, Side side) {
        LOG.info("Getting orders for {} {}", product, side);
        return CompletableFuture.completedFuture(getBestOrdersFor(product, side));
    }

    // get best bid and ask for product
    @Async
    public Order[] topLevel(String product) {
        Order topAsk = orderBook.get(product).get(Side.ASK).getRoot().values().iterator().next();
        Order topBid = orderBook.get(product).get(Side.BID).getRoot().values().iterator().next();

        return new Order[]{topAsk, topBid};
    }

    // difference between best bid and best ask
    @Async
    public double getSpread(String product) { // to avoid call async from another async method
        Order topAsk = orderBook.get(product).get(Side.ASK).getRoot().values().iterator().next();
        Order topBid = orderBook.get(product).get(Side.BID).getRoot().values().iterator().next();

        return topAsk.getPrice()-topBid.getPrice();
    }

    // return map with price as key and number of offers as value
    @Async
    public HashMap<Double, Integer> getBookDepth(String product, Side side) {
        return orderBook.get(product).get(side).getDepth();
    }

    // volume is a number of completed trades (example: seller sel 1 stock and buyer buy it -> volume of market is 1)
}
