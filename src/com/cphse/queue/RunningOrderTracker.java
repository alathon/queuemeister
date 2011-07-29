package com.cphse.queue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.MultiMap;

/**
 * {@link RunningOrderTracker} will keep track of currently executing tasks, and the peasants they are
 * running on. The tracker provides functionality to recover orders from peasants that may have died,
 * need shutting down etc. 
 * 
 * @author Stephen Badger
 */
public final class RunningOrderTracker {
	private final MultiMap<Integer, Long> ordersPerPeasant 	= Hazelcast.getMultiMap(QueueNames.ORDERS_PER_PEASANT.name());
	private final Map<Long, Order> runningOrders 	   		= Hazelcast.getMap(QueueNames.RUNNING_ORDERS.name());
	
	/**
	 * Marks a given order as having been queued with a given peasant.
	 * 
	 * @param peasantID The peasant that has responsibility for executing the task.
	 * @param order The order that was queued.
	 */
	void queueOrder(final int peasantID, final Order order) {
		ordersPerPeasant.put(peasantID, order.getOrderID());
		runningOrders.put(order.getOrderID(), order);
	}
	
	/**
	 * Marks a task as completed, removing it from internal tracking.
	 * 
	 * @param peasantID The peasant that has been executing the task.
	 * @param order The order that was completed.
	 */
	void completeTask(final int peasantID, final Order order) {
		ordersPerPeasant.remove(peasantID, order.getOrderID());
		runningOrders.remove(order.getOrderID());
	}
	
	/**
	 * Removes all tracking for orders being handled by a given peasant and returns them
	 * to be handled again.
	 * 
	 * @param peasantID The peasant that needs it's orders recovering.
	 * @return A potentially empty set of orders the peasant had taken responsibility of.
	 */
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
