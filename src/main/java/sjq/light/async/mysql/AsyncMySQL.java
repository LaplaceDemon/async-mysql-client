package sjq.light.async.mysql;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sjq.light.async.mysql.execute.ExecuteCallback;
import sjq.light.async.mysql.execute.ExecuteTask;
import sjq.light.async.mysql.reactor.IOReactor;
import sjq.light.async.mysql.reactor.IOSession;
import sjq.light.async.mysql.resultset.AsyncPreparedStatement;

public class AsyncMySQL {
	private IOReactor ioReactor;
	private Config config;
	private IOSession eventData;
	private BlockingQueue<ExecuteTask> sqlExecuteQueue;
	
	public void connect() throws UnknownHostException, IOException {
		SocketAddress saddr = new InetSocketAddress(config.getServerAddress(), config.getPort());
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(saddr);
		socketChannel.finishConnect();
		this.eventData = this.ioReactor.register(socketChannel);
	}

	public static AsyncMySQL create(String addr, int port, String username, String password) throws IOException {
		AsyncMySQL asyncMySQL = new AsyncMySQL();
		
		asyncMySQL.config = new Config();
		asyncMySQL.config.setServerAddress(addr);
		asyncMySQL.config.setPort(port);
		asyncMySQL.config.setUsername(username);
		asyncMySQL.config.setPassword(password);
		asyncMySQL.sqlExecuteQueue = new ArrayBlockingQueue<>(10240);
		
		IOReactor ioReactor = new IOReactor(asyncMySQL.config, asyncMySQL.sqlExecuteQueue);
		asyncMySQL.ioReactor = ioReactor;
		
		return asyncMySQL;
	}
	
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
		
		/*
		this.eventData.setExecuteCallback(callback);
		this.eventData.setStatus(Status.Commanding);
		
		CommandQueryPacket command = new CommandQueryPacket();
		command.setSql(sql);
		command.setSequenceId((byte)0);
		command.autoSetLength();
		
		MySQLMessage mySQLMessage = new ByteBufferMySQLMessage(command.getLength() + 4);
		command.write(mySQLMessage, this.eventData.outputMySQLBuffer());
		*/
	}
	
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
}
