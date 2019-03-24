package io.github.laplacedemon.asyncmysql;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.IOReactor;
import io.netty.channel.Channel;

public class AsyncMySQL {
	private IOReactor ioReactor;
	private Config config;
//	private IOSession ioSession;
//	private BlockingQueue<ExecuteTask> sqlExecuteQueue;
	
	/**
	 * get a tcp connection then handshake to mysql.
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Connection connect() {
//		SocketAddress saddr = new InetSocketAddress(config.getServerAddress(), config.getPort());
//		SocketChannel socketChannel = SocketChannel.open();
//		socketChannel.connect(saddr);
//		socketChannel.finishConnect();
//		this.eventData = this.ioReactor.register(socketChannel);
		
		// connect
//		final SocketChannel sc = SocketChannelFactory.createNewConnection("www.baidu.com", 80);
//		ioReactor.registerConnect(sc);
		
//		ioReactor.connect();
		return null;
	}
	
	public void connect(Consumer<Connection> co) {
		ioReactor.connect(this.config , (Channel channel) -> {
			System.out.println("TCP连接成功");
			AttributeMap.ioSession(channel).setHandshakeSuccessCallback(co);
		});
	}

	public static AsyncMySQL create(String addr, int port, String username, String password) throws IOException {
		AsyncMySQL asyncMySQL = new AsyncMySQL();
		
		asyncMySQL.config = new Config();
		asyncMySQL.config.setServerAddress(addr);
		asyncMySQL.config.setPort(port);
		asyncMySQL.config.setUsername(username);
		asyncMySQL.config.setPassword(password);
//		asyncMySQL.sqlExecuteQueue = new ArrayBlockingQueue<>(10240);
		
		IOReactor ioReactor = new IOReactor();
		asyncMySQL.ioReactor = ioReactor;
		
		return asyncMySQL;
	}

	public void start() {
		this.ioReactor.run();
	}

	public ConnectionPool createPool(final int cap) throws InterruptedException {
		ConnectionPool cp = new ConnectionPool(cap);
		CountDownLatch cdl = new CountDownLatch(cap);
		for(int i = 0; i < cap; i++) {
			this.connect(con -> {
				System.out.println("新连接已创建");
				cp.put(con);
				cdl.countDown();
			});
		}
		cdl.await();
		return cp;
	}

	public void createPool(final int cap, final Consumer<ConnectionPool> co) {
		final ConnectionPool cp = new ConnectionPool(cap);
		AtomicInteger ai = new AtomicInteger(cap);
		for(int i = 0; i < cap; i++) {
			this.connect(con -> {
				System.out.println("新连接已创建");
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
	
	/*
	public void execute(String sql, ExecuteCallback callback) {
		if(sql == null || sql.length() == 0) {
			throw new IllegalArgumentException();
		}
		
		System.out.println("发送数据");
		
//		if(this.eventData.getStatus()!= Status.HandShakeing || this.eventData.getStatus()!=Status.Authing) {
//			SelectionKey selectionKey = this.eventData.getSelectionKey();
//			if((selectionKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
//				selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//			}
//		}
		
		SelectionKey selectionKey = this.eventData.getSelectionKey();
		if((selectionKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
			selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
		
		try {
			sqlExecuteQueue.put(new ExecuteTask(sql, callback));
		} catch (InterruptedException e) {
			// put阻塞时候，若阶段则抛出异常。
			e.printStackTrace();
		}
		
		this.eventData.setExecuteCallback(callback);
		this.eventData.setStatus(Status.Commanding);
		
		CommandQueryPacket command = new CommandQueryPacket();
		command.setSql(sql);
		command.setSequenceId((byte)0);
		command.autoSetLength();
		
		MySQLMessage mySQLMessage = new ByteBufferMySQLMessage(command.getLength() + 4);
		command.write(mySQLMessage, this.eventData.outputMySQLBuffer());
	}
	 */	

	/*
	public void execute(AsyncPreparedStatement preparedStatement, ExecuteCallback callback) {
		String statementSQL = preparedStatement.getStatement();
		this.execute(statementSQL, callback);
	}

	public void start() throws IOException {
		ioReactor.run();
	}

	public void startInNewThread() {
		Thread t = new Thread(()-> {
			try {
				ioReactor.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		t.start();
	}
	*/

	
}
