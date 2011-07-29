package com.cphse.queue;

import java.util.concurrent.BlockingQueue;

import com.hazelcast.core.Hazelcast;

/**
 * The order submitter is a small utility class to submit orders to the queuemeister system.
 * 
 * @author Stephen Badger
 */
public final class OrderSubmitter {
	/**
	 * Submits an order to the queuemeister system. This method shall handle interrupts while
	 * submitting to the queue.
	 * 
	 * @param order The order to submit to the queuemeister system.
	 */
	public static final void submitOrder(final Order order) {
		BlockingQueue<Object> queue = Hazelcast.getQueue(order.getOrderType().name());
		boolean submitted = false;
		do {
			try {
				queue.put(order);
				submitted = true;
			} catch (InterruptedException e) {}
		} while (!submitted);
	}
}
