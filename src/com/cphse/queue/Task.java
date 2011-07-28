package com.cphse.queue;

public abstract class Task<T extends Order> implements Runnable {
	protected final T order;
	
	public Task(final T order) {
		this.order = order;
	}
}
