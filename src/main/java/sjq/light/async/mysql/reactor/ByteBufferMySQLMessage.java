package sjq.light.async.mysql.reactor;

import java.nio.ByteBuffer;

import sjq.light.mysql.protocol.buffer.MySQLMessage;
import sjq.light.mysql.protocol.util.LitteEndianNumberUtils;

public class ByteBufferMySQLMessage implements MySQLMessage {
	private ByteBuffer message;
	
	public ByteBufferMySQLMessage(int size) {
		message = ByteBuffer.allocate(size);
	}
	
	public ByteBuffer getMessage() {
		return message;
	}


	@Override
	public void writeShort(short data) {
		message.putShort(data);
	}
	
	@Override
	public void writeLEShort(short data) {
		byte[] bytes = LitteEndianNumberUtils.toBytes(data);
		message.put(bytes);
	}
	
	@Override
	public void writeUInt(long data) {
		message.putInt((int)data);
	}

	@Override
	public void writeLEUInt(long data) {
		byte[] bytes = LitteEndianNumberUtils.toBytes((int)data);
		message.put(bytes);
	}

	@Override
	public void writeByte(byte data) {
		message.put(data);
	}

	@Override
	public void writeBytes(byte[] data) {
		message.put(data);
	}

	@Override
	public void writeStringNul(String data) {
		message.put(data.getBytes());
		message.put((byte)0);
	}
	
	public void writeStringEOF(String data) {
		message.put(data.getBytes());
	}

	@Override
	public void writeBytes(byte data1, byte[] data2) {
		message.put(data1);
		message.put(data2);
	}

	@Override
	public void writeUShort(int data) {
		message.putShort((short)data);
	}

	@Override
	public void writeLenencString(String data) {
//		int strLen = data.length();
//		byte[] LenencStringLengthBytes = MySQLByteUtils.codecLenencStringLength(strLen);
//		message.put(data);
//		
	}
	
	

}
