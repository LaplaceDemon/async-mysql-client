package io.github.laplacedemon.asyncmysql;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.IOReactor;
import io.netty.channel.Channel;

public class AsyncMySQL {
	private IOReactor ioReactor;
	
	/**
	 * get a tcp connection then handshake to mysql.
	 */
	public void connect(final Config config, Consumer<Connection> co) {
		ioReactor.connect(config , (Channel channel) -> {
//			System.out.println("TCP连接成功");
			AttributeMap.ioSession(channel).setHandshakeSuccessCallback(co);
		});
	}
	
	public static AsyncMySQL create() {
		AsyncMySQL asyncMySQL = new AsyncMySQL();
		
		IOReactor ioReactor = new IOReactor();
		asyncMySQL.ioReactor = ioReactor;
		
		return asyncMySQL;
	}
	
	public Config makeConfig(String addr, int port, String username, String password, String database) {
		Config config = new Config();config.setServerAddress(addr);
		config.setPort(port);
		config.setUsername(username);
		config.setPassword(password);
		config.setDatabase(database);
		
		return config;
	}
	
	public Config makeConfig(String addr, int port, String username, String password) {
		return makeConfig(addr, port, username, password, null);
	}

	public static AsyncMySQL create(String addr, int port, String username, String password) throws IOException {
		return create();
	}

	public void start() {
		this.ioReactor.run();
	}

	public ConnectionPool createPool(final Config config, final int cap) throws InterruptedException {
		ConnectionPool cp = new ConnectionPool(cap);
		CountDownLatch cdl = new CountDownLatch(cap);
		for(int i = 0; i < cap; i++) {
			this.connect(config, con -> {
				cp.put(con);
				cdl.countDown();
			});
		}
		cdl.await();
		return cp;
	}

	public void createPool(final Config config ,final int cap, final Consumer<ConnectionPool> co) {
		final ConnectionPool cp = new ConnectionPool(cap);
		AtomicInteger ai = new AtomicInteger(cap);
		for(int i = 0; i < cap; i++) {
			this.connect(config, con -> {
//				System.out.println("新连接已创建");
				cp.put(con);
				int count = ai.decrementAndGet();
				if(count == 0) {
					// 开启异步任务，执行回调
					ioReactor.execute(()->{
						co.accept(cp);
					});
				}
			});
		}
	}
	
}
