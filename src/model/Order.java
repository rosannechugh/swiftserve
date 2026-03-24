package model;
import java.time.LocalDateTime;
public class Order {
	 private int orderId;
	    private String customerName;
	    private String address;
	    private double amount;
	    private OrderStatus status;
	    private String assignedAgent;
	    private LocalDateTime orderTime;
	    private boolean counted = false;

	    public boolean isCounted() { return counted; }
	    public void setCounted(boolean c) { counted = c; }
	    public Order(int orderId, String customerName, String address, double amount) {
	        this.orderId = orderId;
	        this.customerName = customerName;
	        this.address = address;
	        this.amount = amount;
	        this.status = OrderStatus.PENDING;
	        this.setOrderTime(LocalDateTime.now());
	    }

	    // Getters & Setters
	    public int getOrderId() { return orderId; }
	    public String getCustomerName() { return customerName; }
	    public String getAddress() { return address; }
	    public double getAmount() { return amount; }
	    public OrderStatus getStatus() { return status; }
	    public String getAssignedAgent() { return assignedAgent; }

	    public void setStatus(OrderStatus status) { this.status = status; }
	    public void setAssignedAgent(String agent) { this.assignedAgent = agent; }

		public LocalDateTime getOrderTime() {
			return orderTime;
		}

		public void setOrderTime(LocalDateTime orderTime) {
			this.orderTime = orderTime;
		}

}
