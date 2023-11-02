package cz.cez.trading.algo.interview.shared;

import java.util.LinkedList;

public class Limit {
    private double price;
    private String product;
    private Pair<Double, String> limitKey;
    private long totalQuantity;
    private LinkedList<Order> orders;

    public Limit(double price, String product, long totalQuantity) {
        this.price = price;
        this.product = product;
        this.limitKey = new Pair<Double, String>(price, product);
        this.totalQuantity = totalQuantity;
        this.orders = new LinkedList<>();
    }

    public Limit(Order order) {
        this(order.getPrice(), order.getProduct(), order.getQuantity());
        this.append(order);
    }

    public double getPrice() {
        return price;
    }

    public String getProduct() {
        return product;
    }

    public Pair<Double, String> getLimitKey() {
        return limitKey;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public LinkedList<Order> getOrders() {
        return orders;
    }

    public void append(Order order) {
        orders.add(order);
//        this.totalQuantity += order.getQuantity();
    }

    public double getVolume() {
        return price*totalQuantity;
    }
}
