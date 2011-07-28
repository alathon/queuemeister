package com.cphse.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.cphse.time.TimeDelay;
import com.hazelcast.core.Hazelcast;

public abstract class OrderProcessor<T extends Order> extends Thread {
	private final BlockingQueue<T> 			pendingOrders;
	private boolean 						processTasks = true;
	private final ScheduledExecutorService  threadPool;
	
	public OrderProcessor(final OrderType orderType, final int mininumSize) {
		pendingOrders 	= Hazelcast.getQueue(orderType.name());
		threadPool		= new ScheduledThreadPoolExecutor(mininumSize);
	}
	
	protected abstract Task<T> createTask(final T order);
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public final void run() {
		while (processTasks) {
			try {
				final T order         = pendingOrders.take();
				final TimeDelay delay = order.getNextExecutionDelay();
				threadPool.schedule(createTask(order), delay.getDuration(), delay.getUnits());
			} catch (InterruptedException e) {}
		}
	}
	
	public void shutdown(final TimeDelay delay) {
		stopPolling();
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(delay.getDuration(), delay.getUnits());
		} catch (InterruptedException e) {
			// TODO: Warning reporting about interruption while shutdown here.
		}
	}

	public void shutdownNow() {
		stopPolling();
		threadPool.shutdownNow();
	}
	
	private void stopPolling() {
		processTasks = false;
		this.interrupt();
	}
}
