package io.github.laplacedemon.asyncmysql;

import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.laplacedemon.AsyncPool;

public class ConnectionPool {
	private int capacity;
	private final AsyncPool<Connection> asyncPool;
	
	public int getFreeConnectionCount() {
		return this.asyncPool.size();
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public ConnectionPool(int capacity, Supplier<Connection> generator) {
        this.asyncPool = new AsyncPool<>(capacity, generator);
    }
	
	ConnectionPool(int capacity, AsyncPool<Connection> asyncPool) {
	    this.capacity = capacity;
	    this.asyncPool = asyncPool;
    }

    public void asyncGet(Consumer<Connection> consumer) {
		this.asyncPool.asyncGet(consumer);
	}

	public void returnBack(Connection connection) {
	    this.asyncPool.returnBack(connection);
	}

}
