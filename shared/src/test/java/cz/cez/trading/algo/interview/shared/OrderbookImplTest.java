package cz.cez.trading.algo.interview.shared;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class OrderbookImplTest {

    private static final String[] PRODUCTS = new String[] {"ttf-jan-2023", "ttf-feb-2023", "ttf-mar-2023", "ttf-apr-2023"};
    private static final double[] BID_PRICES = new double[] {99.9, 99.925, 99.95, 99.975};
    private static final double[] ASK_PRICES = new double[] {100, 100.025, 100.05, 100.075, 100.1};
    private final Map<String, Order> orders;
    private final Random random;

    OrderbookImpl orderBook;

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

    private Order createRandom(String product) {
        final Side side = this.random.nextBoolean() ? BID : ASK;
        final double[] prices = this.getPrices(side);
        final Order order = new Order(randomUUID().toString(),
                product,
                side, 10, prices[this.random.nextInt(prices.length)], SET);
        this.orders.put(order.getId(), order);
        return order;
    }

    @BeforeAll
    void testStructuresInit() {
        this.orders = new HashMap<>();
        this.random = new Random(123456);
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        orderBook = new OrderbookImpl();
    }

    @org.junit.jupiter.api.Test
    void orderbookTestSetOneOrder() {
        String product = "ttf-jan-2023";
        Side side = Side.ASK;
        Order order = new Order(
                randomUUID().toString(),
                product,
                side,
                10,
                100,
                Operation.SET);
        orderBook.processOrder(order);

        Stream<Order> bestOrdersStream = orderBook.getBestOrdersFor(product, side);
        Order[] bestOrdersArray = bestOrdersStream.toArray();

        AssertEquals(1, bestOrdersArray.length);
        AssertEquals(100, bestOrdersArray[0].getPrice());
        AssertEquals(product, bestOrdersArray[0].getProduct());

    }

    @org.junit.jupiter.api.Test
    void orderbookTestSetMultipleOrders() {
        int numOrders = 10;
        Order[] orders = new Order[numOrders];
        String product = "ttf-jan-2023";
        int bestPriceBid = 0;
        int bestPriceAsk = 200;
        int bestBidsCnt = 0;
        int bestAsksCnt = 0;
        for(int i = 0; i < numOrders; i++) {
            Order newOrder = createRandom(product);
            switch(newOrder.getSide()) {
                case ASK:
                    if(newOrder.getPrice < bestPriceAsk) {
                        bestPriceAsk = newOrder.getPrice();
                        bestAsksCnt++;
                    }
                    else if(newOrder.getPrice() == bestPriceAsk) {
                        bestAsksCnt++;
                    }
                case BID:
                    if(newOrder.getPrice > bestPriceBid) {
                        bestPriceBid = newOrder.getPrice();
                        bestBidsCnt++;
                    }
                    else if(newOrder.getPrice() == bestPriceBid) {
                        bestBidsCnt++;
                    }
            }
            orders[i] = newOrder;
            orderBook.processOrder(newOrder);
        }

        Stream<Order> bestOrdersAskStream = orderBook.getBestOrdersFor(product, Side.ASK);
        Stream<Order> bestOrdersBidStream = orderBook.getBestOrdersFor(product, Side.BID);
        Order[] bestOrdersAskArray = bestOrdersAskStream.toArray();
        Order[] bestOrdersBidArray = bestOrdersBidStream.toArray();


        AssertEquals(bestPriceAsk, bestOrdersAskArray[0].getPrice());
        AssertEquals(bestPriceBid, bestOrdersBidArray[0].getPrice());

        AssertEquals(bestAsksCnt, bestOrdersAskArray.length);
        AssertEquals(bestBidsCnt, bestOrdersBidArray.length);
    }

    @org.junit.jupiter.api.Test
    void orderbookTestDeleteOneOrder() {
        Order order = createRandom();
        orderBook.processOrder(order);
        order.setOperation(Operation.DELETE);
        orderBook.processOrder(order);

        Stream<Order> bestOrdersStream = orderBook.getBestOrdersFor(order.getProduct(), order.getSide());
        AssertEquals(null, bestOrdersStream); // TODO: does TreeMap return null if empty?
    }

    @org.junit.jupiter.api.Test
    void orderbookTestDeleteMultipleOrders() {
        int numOrders = 10;
        Order[] orders = new Order[numOrders];
        for(int i = 0; i < numOrders; i++) {
            orders[i] = createRandom();
            orderBook.processOrder(orders[i]);
        }
        Arrays.stream(orders).forEach(o -> {
            o.setOperation(Operation.DELETE);
            orderBook.processOrder(o);
        });

        for(Order order : orders) {
            Stream<Order> bestOrdersStream = orderBook.getBestOrdersFor(order.getProduct(), order.getSide());
            AssertEquals(null, bestOrdersStream);
        }
    }
}