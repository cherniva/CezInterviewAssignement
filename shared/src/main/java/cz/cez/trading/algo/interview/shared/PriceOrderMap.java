package cz.cez.trading.algo.interview.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class PriceOrderMap {
    private ConcurrentSkipListMap<Double, HashMap<String, Order>> priceOrdersSortedMap;

    public PriceOrderMap(Side side) {
        switch(side) {
            case ASK -> priceOrdersSortedMap = new ConcurrentSkipListMap<>();
            case BID -> priceOrdersSortedMap = new ConcurrentSkipListMap<>(Collections.reverseOrder());
        }
    }

    public void insert(Order order) {
        if(priceOrdersSortedMap.containsKey(order.getPrice())) {
            priceOrdersSortedMap.get(order.getPrice()).put(order.getId(), order);
        }
        else {
            HashMap<String, Order> ordersMap = new HashMap<>();
            ordersMap.put(order.getId(), order);
            priceOrdersSortedMap.put(order.getPrice(), ordersMap);
        }
    }

    public HashMap<String, Order> getRoot() {
        try {
            return priceOrdersSortedMap.get(priceOrdersSortedMap.firstKey());
        }
        catch(NoSuchElementException e) {
            return new HashMap<>();
        }
    }

    public HashMap<Double, Integer> getDepth() {
        HashMap<Double, Integer> depthMap = priceOrdersSortedMap.entrySet().stream()
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().size(), (prev, next) -> next, HashMap::new));

        return depthMap;
    }

    public Order remove(Order order) {
        HashMap<String, Order> ordersForThatPrice = priceOrdersSortedMap.get(order.getPrice());
        Order orderToRemove = ordersForThatPrice.remove(order.getId());
        if(ordersForThatPrice.size() == 0) {
            priceOrdersSortedMap.remove(order.getPrice());
        }
        return orderToRemove;
    }

    public int getSize() {
        return this.priceOrdersSortedMap.size();
    }
}
