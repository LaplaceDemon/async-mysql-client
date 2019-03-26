package io.github.laplacedemon.asyncmysql.network;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import io.github.laplacedemon.asyncmysql.util.AutoByteBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.buffer.ByteBuf;

public class ByteBufAdapter implements InputMySQLBuffer {
	
	public ByteBuf byteBuf;
	
	@Override
	public void setBuffer(Object buffer) {
		if(this.byteBuf == buffer) {
			return ;
		}
		this.byteBuf = (ByteBuf)buffer;
	}
	

	@Override
	public int read() {
		return byteBuf.readByte();
	}

	@Override
	public int readNBytes(byte[] buf, int offset, int len) {
		byteBuf.readBytes(buf, offset, len);
		return len;
	}

	@Override
	public byte[] readUtils(byte b) {
		return readUtils(b, AutoByteBuffer.InitLen);
	}
	
	@Override
	public byte[] readUtils(byte b, int initBytes) {
		AutoByteBuffer sb = new AutoByteBuffer(initBytes);
		while(true) {
			byte readByte = this.readByte();
			sb.append(readByte);
			if(readByte == b) {
				return sb.getBytes();
			}
		}
	}

	@Override
	public void skip(int length) {
		this.byteBuf.skipBytes(length);
	}

	@Override
	public int readInt() {
		return byteBuf.readIntLE();
	}

	@Override
	public byte[] readNBytes(int length) {
		byte[] bs = new byte[length];
		byteBuf.readBytes(bs);
		return bs;
	}

	@Override
	public short readShort() {
		return byteBuf.readShortLE();
	}

	@Override
	public byte readByte() {
		return byteBuf.readByte();
	}

	@Override
	public BigInteger readULong() {
		return null;
	}

	@Override
	public int readUShort() {
		return byteBuf.readUnsignedShortLE();
	}

	@Override
	public BigInteger readLenencInteger() {
		byte firstByte = this.readByte();
		long lengthEncodedInteger = MySQLByteUtils.readLengthEncodedInteger(firstByte, this);
		return BigInteger.valueOf(lengthEncodedInteger);
	}

	@Override
	public String readLenencString() {
		byte lenencFirstByte = this.readByte();
		long readLengthEncodedInteger = MySQLByteUtils.readLengthEncodedInteger(lenencFirstByte, this);
		byte[] bs = this.readNBytes((int)readLengthEncodedInteger);
		try {
			return new String(bs,"ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public int readableBytes() {
		return this.byteBuf.readableBytes();
	}

}
