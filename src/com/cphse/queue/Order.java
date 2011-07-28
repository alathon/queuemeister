package com.cphse.queue;

import java.io.Serializable;

import com.cphse.time.TimeDelay;
import com.hazelcast.core.Hazelcast;

public abstract class Order implements Serializable {
	private static final long serialVersionUID = -8409915427548157073L;
	private final long orderID = Hazelcast.getIdGenerator("OrderID").newId();

	public abstract TimeDelay getNextExecutionDelay();
	
	public abstract OrderType getOrderType();

	public final long getOrderID() {
		return orderID;
	}
}
