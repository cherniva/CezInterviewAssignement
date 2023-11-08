package cz.cez.trading.algo.interview.shared;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class OrderbookImpl implements Orderbook {

    //TODO: tests

    //TODO: synchronization test

    //TODO: add querying for middle price, best price, volume, spread

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
    @Async
    public Stream<Order> getBestOrdersFor(String product, Side side) {
        return orderBook.get(product).get(side).getRoot().values().stream();
    }
}
