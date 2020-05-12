package io.github.laplacedemon.asyncmysql;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.IOReactor;
import io.github.laplacedemon.asyncwait.AsyncPool;
import io.netty.channel.Channel;

public class AsyncMySQL {
    
    private static ThreadLocal<Semaphore> blocker = ThreadLocal.withInitial(()->{
        return new Semaphore(1);
    });
    
	private IOReactor ioReactor;
	
	/**
	 * get a tcp connection then handshake to mysql.
	 */
	public void connect(final Config config, Consumer<Connection> co) {
		ioReactor.connect(config , (Channel channel) -> {
			AttributeMap.ioSession(channel).setHandshakeSuccessCallback(co);
		});
	}
	
	public void connect(final Config config, Consumer<Connection> co, Consumer<Throwable> throwableConsumer) {
		ioReactor.connect(config , (Channel channel) -> {
			AttributeMap.ioSession(channel).setHandshakeSuccessCallback(co);
		});
	}
	
	private Connection connect(Config config) {
	    final Semaphore semaphore = blocker.get();
	    Connection connection = null;
	    
	    this.connect(config, (Connection newCon)->{
	        semaphore.release();
	    });
	    
	    try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    
        return connection;
    }
	
	public static AsyncMySQL create() {
		AsyncMySQL asyncMySQL = new AsyncMySQL();
		
		IOReactor ioReactor = new IOReactor();
		asyncMySQL.ioReactor = ioReactor;
		
		return asyncMySQL;
	}
	
	public Config makeConfig(String host, int port, String username, String password, String database) {
		Config config = new Config();
		config.setHost(host);
		config.setPort(port);
		config.setUsername(username);
		config.setPassword(password);
		config.setDatabase(database);
		
		return config;
	}
	
	public Config makeConfig(String host, int port, String username, String password) {
		return makeConfig(host, port, username, password, null);
	}

	public static AsyncMySQL create(String host, int port, String username, String password) throws IOException {
		return create();
	}

	public void start() {
		this.ioReactor.run();
	}

	public ConnectionPool createPool(final Config config, final int cap) throws InterruptedException {
	    CountDownLatch cdl = new CountDownLatch(cap);
	    
	    ConnectionPool cp = new ConnectionPool(cap, ()-> {
	        try {
    		    Connection connection = this.connect(config);
    		    return connection;
	        } finally {
	            cdl.countDown();
	        }
		});
		
		cdl.await();
		return cp;
	}

    public void createPool(final Config config ,final int capacity, final Consumer<ConnectionPool> co) {
        AtomicInteger at = new AtomicInteger(capacity);
        AsyncPool<Connection> asyncPool = new AsyncPool<>(capacity);
        
        for(int i = 0; i < capacity; i++) {
            this.connect(config, (Connection con) -> {
                asyncPool.add(con);
                int n = at.decrementAndGet();
                if(n == 0) {
                    ConnectionPool connectionPool = new ConnectionPool(capacity, asyncPool);
                    co.accept(connectionPool);
                }
            });
        }
        
	}
	
}
