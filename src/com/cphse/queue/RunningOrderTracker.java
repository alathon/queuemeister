package com.cphse.queue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.MultiMap;

public final class RunningOrderTracker {
	private final MultiMap<Integer, Long> ordersPerPeasant 	= Hazelcast.getMultiMap(QueueNames.ORDERS_PER_PEASANT.name());
	private final Map<Long, Order> runningOrders 	   		= Hazelcast.getMap(QueueNames.RUNNING_ORDERS.name());
	
	public void startTask(final int peasantID, final Order order) {
		ordersPerPeasant.put(peasantID, order.getOrderID());
		runningOrders.put(order.getOrderID(), order);
	}
	
	public void completeTask(final int peasantID, final Order order) {
		ordersPerPeasant.remove(peasantID, order.getOrderID());
		runningOrders.remove(order.getOrderID());
	}
	
	public Set<Order> recoverPeasant(final int peasantID) {
		final Set<Order> orders = new HashSet<Order>();
		final Collection<Long> orderIDs = ordersPerPeasant.remove(peasantID);
		if ( orderIDs != null ) {
			for ( Long orderID : orderIDs ) {
				orders.add(runningOrders.remove(orderID));
			}
		}
		return orders;
	}
}
