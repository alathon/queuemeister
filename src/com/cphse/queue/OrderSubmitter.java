package com.cphse.queue;

import java.util.concurrent.BlockingQueue;

import com.hazelcast.core.Hazelcast;

public final class OrderSubmitter {
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
