package cz.cez.trading.algo.interview.shared;

import java.util.Collections;
import java.util.TreeMap;

public class LimitTree {
    private TreeMap<Double, Limit> treeMap; // TODO: mb change to <Double, Order> structure

    public LimitTree(Side side) {
        switch(side) {
            case ASK -> treeMap = new TreeMap<>();
            case BID -> treeMap = new TreeMap<>(Collections.reverseOrder());
        }
    }

    public void insert(Limit limit) {
        treeMap.put(limit.getPrice(), limit);
    }

    public Limit getRoot() {
        return treeMap.get(treeMap.firstKey());
    }

    public Limit removeLimitLevel(double price) {
        return treeMap.remove(price);
    }

    public Limit getLevel(double price) {
        return treeMap.get(price);
    }

    public int getSize() {
        return this.treeMap.size();
    }
}
