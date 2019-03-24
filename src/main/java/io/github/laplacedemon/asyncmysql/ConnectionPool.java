package io.github.laplacedemon.asyncmysql;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ConnectionPool {
	private int capacity;
	private final ConcurrentLinkedQueue<Connection> pool;
	private final ConcurrentLinkedQueue<Consumer<Connection>> waitingTask;
	
	public int getFreeConnectionCount() {
		return this.pool.size();
	}
	
	public int getCapacity() {
		return capacity;
	}

	ConnectionPool(int capacity) {
		this.capacity = capacity;
		this.pool = new ConcurrentLinkedQueue<>();
		this.waitingTask = new ConcurrentLinkedQueue<>();
	}
	
	public void get(Consumer<Connection> consumer) {
		final Connection con = this.pool.poll();
		if(con == null) {
			waitingTask.add(consumer);
			return ;
		}
		consumer.accept(con);
		this.returnBack(con);
	}

	private void returnBack(Connection connection) {
		boolean add = this.pool.add(connection);
		if(add && !waitingTask.isEmpty()) {
			Consumer<Connection> waitingConsumer = waitingTask.poll();
			this.get(waitingConsumer);
		}
	}

	boolean put(Connection con) {
		return this.pool.add(con);
	}
	
}
