package service;
import java.util.*;

import model.DeliveryAgent;
import model.Order;
import model.OrderStatus;
public class DeliveryService {
	 public static List<Order> orders = new ArrayList<>();
	    public static List<DeliveryAgent> agents = new ArrayList<>();

	    private static int orderCounter = 1;

	    public static Order createOrder(String name, String address, double amount) {
	        Order order = new Order(orderCounter++, name, address, amount);
	        orders.add(order);
	        return order;
	    }

	    public static void addAgent(String name) {
	        agents.add(new DeliveryAgent(name));
	    }

	    public static List<Order> getOrders() {
	        return orders;
	    }

	    public static List<DeliveryAgent> getAgents() {
	        return agents;
	    }

	    public static void assignAgent(Order order, String agentName) {
	        for (DeliveryAgent a : agents) {
	            if (a.getName().equals(agentName) && a.isAvailable()) {
	                order.setAssignedAgent(agentName);
	                a.setAvailable(false);
	                order.setStatus(OrderStatus.PREPARING);
	                return;
	            }
	        }
	    }

	    public static void updateStatus(Order order) {
	        switch (order.getStatus()) {
	            case PREPARING:
	                order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
	                break;
	            case OUT_FOR_DELIVERY:
	                order.setStatus(OrderStatus.DELIVERED);
	                break;
	            default:
	                break;
	        }
	    }

}
