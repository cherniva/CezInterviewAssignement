package cz.cez.trading.algo.interview.shared;

/**
 * Represents a change of trading order (a limit order to be exact)
 * on the market. All the data of order is always present regardless of whether it's
 * needed to evaluate respective {@link #operation}.
 */
public class Order {

	/**
	 * Primary identificator of this entity.
	 */
	private String id;

	/**
	 * Primary identificator of product which this order bids or asks.
	 */
	private String product;

	/**
	 * Flag whether initiator of the order wants to buy or sell.
	 */
	private Side side;

	/**
	 * Quantity of the respective product
	 */
	private long quantity;

	/**
	 * Limit price of unit of quantity for which this order can be traded.
	 */
	private double price;

	/**
	 * What is happening to this order in this change.
	 * If Operation is SET, then data in this object
	 * fully replaces any previous data for order
	 * identified by #id of this order.
	 */
	private Operation operation;

	public Order() {

	}

	public Order(Order other) {
		this.id = other.id;
		this.product = other.product;
		this.side = other.side;
		this.quantity = other.quantity;
		this.price = other.price;
		this.operation = other.operation;
	}

	public Order(String id, String product, Side side, long quantity, double price, Operation operation) {
		this.id = id;
		this.product = product;
		this.side = side;
		this.quantity = quantity;
		this.price = price;
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "Order [id=" + this.id + ", product=" + this.product + ", side=" + this.side + ", quantity=" + this.quantity + ", price="
				+ this.price + ", operation=" + this.operation + "]";
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Operation getOperation() {
		return this.operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getProduct() {
		return this.product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public Side getSide() {
		return this.side;
	}
	public void setSide(Side side) {
		this.side = side;
	}
	public long getQuantity() {
		return this.quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return this.price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

}
