package sjq.light.async.mysql.reactor_tmp;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import io.github.laplacedemon.asyncmysql.ServerInfo;
import io.github.laplacedemon.asyncmysql.Status;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import sjq.light.async.mysql.execute.ExecuteCallback;

public class IOSession {
	private SocketChannel socketChannel;
	private LinkedList<ByteBuffer> writeableByteBufferQueue;
	private InputBuffer readInputBuffer;
	private InputMySQLBuffer inputMySQLBuffer;
	private OutputMySQLBuffer outputMySQLBuffer;
	private Status status;
	private ExecuteCallback executeCallback;
	private SelectionKey selectionKey;
	private ServerInfo serverInfo;
	private MySQLResultPacket resultPacketList;

	public IOSession(SelectionKey selectionKey) {
		this.writeableByteBufferQueue = new LinkedList<>();
		this.status = Status.HandShakeing;
		this.readInputBuffer = new InputBuffer();
		this.inputMySQLBuffer = new InputByteMySQLBuffer(this.readInputBuffer);
		this.selectionKey = selectionKey;
		this.outputMySQLBuffer = new OutputByteMySQLBuffer(this.writeableByteBufferQueue);
		this.serverInfo = new ServerInfo();
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public List<ByteBuffer> getWriteQueue() {
		return writeableByteBufferQueue;
	}
	
	public ByteBuffer getLastWriteableBuffer() {
		return this.writeableByteBufferQueue.getLast();
	}
	
	public void removeLastWriteableBuffer() {
		this.writeableByteBufferQueue.removeLast();
	}

	public int writeableBufferSize() {
		return writeableByteBufferQueue.size();
	}

	public void pushInputBuffer(ByteBuffer buffer) {
		readInputBuffer.push(buffer);
	}

	public InputBuffer inputBuffer() {
		return this.readInputBuffer;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public InputMySQLBuffer inputMySQLBuffer() {
		return inputMySQLBuffer;
	}

	public OutputMySQLBuffer outputMySQLBuffer() {
		return this.outputMySQLBuffer;
	}

	public void close() {
		this.writeableByteBufferQueue.clear();
		this.readInputBuffer.clear();
	}

	public void setExecuteCallback(ExecuteCallback callback) {
		this.executeCallback = callback;
	}
	
	public ExecuteCallback getExecuteCallback() {
		return executeCallback;
	}

	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	public ServerInfo serverInfo() {
		return serverInfo;
	}

	public MySQLResultPacket resultPacketList() {
		return resultPacketList;
	}

	public void setResultPacketList(MySQLResultPacket resultPacketList) {
		this.resultPacketList = resultPacketList;
	}
	
}
