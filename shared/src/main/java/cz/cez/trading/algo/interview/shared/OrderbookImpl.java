package cz.cez.trading.algo.interview.shared;

import java.util.TreeMap;
import java.util.stream.Stream;

public class OrderbookImpl implements Orderbook {

    //TODO: implement data structure to maintain orders
        //TODO: binary tree for bid and for ask (mb for lowest sell and highest buy)

    //TODO: add querying tools to add, delete and update order (add and update could be the same function)

    //TODO: synchronization

    private LimitTree ttfJan23Asks;
    private LimitTree ttfJan23Bids;
    private LimitTree ttfFeb23Asks;
    private LimitTree ttfFeb23Bids;
    private LimitTree ttfMar23Asks;
    private LimitTree ttfMar23Bids;
    private LimitTree ttfApr23Asks;
    private LimitTree ttfApr23Bids;
    private TreeMap<Pair<Double, String>, Limit> limitsMap;
    private TreeMap<String, Order> ordersMap;


    public OrderbookImpl() {
        this.ttfJan23Asks = new LimitTree(Side.ASK);
        this.ttfJan23Bids = new LimitTree(Side.BID);
        this.ttfFeb23Asks = new LimitTree(Side.ASK);
        this.ttfFeb23Bids = new LimitTree(Side.BID);
        this.ttfMar23Asks = new LimitTree(Side.ASK);
        this.ttfMar23Bids = new LimitTree(Side.BID);
        this.ttfApr23Asks = new LimitTree(Side.ASK);
        this.ttfApr23Bids = new LimitTree(Side.BID);
        this.limitsMap = new TreeMap<>();
        this.ordersMap = new TreeMap<>();
    }

    public void processOrder(Order order) {
        switch(order.getOperation()) {
            case SET -> setOrder(order);
            case DELETE -> deleteOrder(order);
        }
    }

    private void setOrder(Order order) {
        switch(order.getProduct()) { //"ttf-jan-2023", "ttf-feb-2023", "ttf-mar-2023", "ttf-apr-2023"
            case "ttf-jan-2023" -> setOrder(order, order.getSide() == Side.ASK ? ttfJan23Asks : ttfJan23Bids);
            case "ttf-feb-2023" -> setOrder(order, order.getSide() == Side.ASK ? ttfFeb23Asks : ttfFeb23Bids);
            case "ttf-mar-2023" -> setOrder(order, order.getSide() == Side.ASK ? ttfMar23Asks : ttfMar23Bids);
            case "ttf-apr-2023" -> setOrder(order, order.getSide() == Side.ASK ? ttfApr23Asks : ttfApr23Bids);
        }
    }

    private void setOrder(Order order, LimitTree limitTree) {
        try {
            Order existingOrder = ordersMap.get(order.getId());
            existingOrder.setPrice(order.getPrice()); //TODO: change that limit depends on order's price
        }
        catch (NullPointerException e) {
            if(!limitsMap.containsKey(order.getPrice())) {
                Limit limit = new Limit(order);
                ordersMap.put(order.getId(), order);
                limitsMap.put(limit.getLimitKey(), limit);
                limitTree.insert(limit);
            }
            else {

            }
        }
    }

    private void deleteOrder(Order order) {

    }

//    private LimitTree bids;
//    private LimitTree asks;
//    private TreeMap<Double, Limit> limitsMap;
//    private TreeMap<String, Order> ordersMap;
//
//    public OrderbookImpl() {
//        this.bids = new LimitTree(Side.BID);
//        this.asks = new LimitTree(Side.ASK);
//        this.limitsMap = new TreeMap<>();
//        this.ordersMap = new TreeMap<>();
//    }
//
//    public Limit getBestBid() {
//        return bids.getRoot();
//    }
//
//    public Limit getBestAsk() {
//        return asks.getRoot();
//    }
//
//    public void processOrder(Order order) {
//        switch(order.getOperation()) {
//            case SET -> setOrder(order);
//            case DELETE -> deleteOrder(order);
//        }
//    }
//
//    // create or update order
//    private void setOrder(Order order) {
//        try { // update existing
//            Order existingOrder = ordersMap.get(order.getId());
//            existingOrder.setPrice(order.getPrice());
//        }
//        catch(NullPointerException e) { // add new
//            if(!limitsMap.containsKey(order.getPrice())) {
//                Limit limit = new Limit(order);
//                ordersMap.put(order.getId(), order);
//                limitsMap.put(order.getPrice(), limit);
//
//                switch (order.getSide()) {
//                    case ASK -> asks.insert(limit);
//                    case BID -> bids.insert(limit);
//                }
//            }
//            else {
//
//            }
//        }
//    }
//
//    private void deleteOrder(Order order) {
//
//    }

    @Override
    public Stream<Order> getBestOrdersFor(String product, Side side) {
        return null;
    }
}
