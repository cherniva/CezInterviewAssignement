package cz.cez.trading.algo.interview.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class OrderTree {
    private TreeMap<Double, HashMap<String, Order>> ordersTreeMap;

    public OrderTree(Side side) {
        switch(side) {
            case ASK -> ordersTreeMap = new TreeMap<>();
            case BID -> ordersTreeMap = new TreeMap<>(Collections.reverseOrder());
        }
    }

    public void insert(Order order) {
        if(ordersTreeMap.containsKey(order.getPrice())) {
            ordersTreeMap.get(order.getPrice()).put(order.getId(), order);
        }
        else {
            HashMap<String, Order> ordersMap = new HashMap<>();
            ordersMap.put(order.getId(), order);
            ordersTreeMap.put(order.getPrice(), ordersMap);
        }
    }

    public HashMap<String, Order> getRoot() {
        return ordersTreeMap.get(ordersTreeMap.firstKey());
    }

    public Order remove(Order order) {
        HashMap<String, Order> ordersForThatPrice = ordersTreeMap.get(order.getPrice());
        Order orderToRemove = ordersForThatPrice.remove(order.getId());
        if(ordersForThatPrice.size() == 0) {
            ordersTreeMap.remove(order.getPrice());
        }
        return orderToRemove;
    }

    public int getSize() {
        return this.ordersTreeMap.size();
    }
}
