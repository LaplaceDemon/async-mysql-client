package sjq.light.async.mysql.reactor;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import sjq.light.mysql.protocol.buffer.MySQLMessage;
import sjq.light.mysql.protocol.buffer.OutputMySQLBuffer;

public class OutputByteMySQLBuffer implements OutputMySQLBuffer {
	private LinkedList<ByteBuffer> writeableByteBufferQueue;
	
	public OutputByteMySQLBuffer(LinkedList<ByteBuffer> writeableByteBufferQueue) {
		this.writeableByteBufferQueue = writeableByteBufferQueue;
	}

	@Override
	public void write(MySQLMessage message) {
		ByteBuffer byteBuffer = ((ByteBufferMySQLMessage)message).getMessage();
		writeableByteBufferQueue.addLast(byteBuffer);
	}

}
