package sjq.light.async.mysql.reactor_tmp;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import io.github.laplacedemon.asyncmysql.util.AutoByteBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.util.LitteEndianNumberUtils;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;

public class InputByteMySQLBuffer implements InputMySQLBuffer {
	
	private InputBuffer inputBuffer;

	public InputByteMySQLBuffer(InputBuffer inputBuffer) {
		this.inputBuffer = inputBuffer;
	}

	@Override
	public int read() {
		return inputBuffer.read();
	}

	@Override
	public int readNBytes(byte[] buf, int offset, int len) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public byte[] readUtils(byte b) {
		AutoByteBuffer sb = new AutoByteBuffer();
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
		for(int i = 0; i<length; i++) {
			inputBuffer.read();
		}
	}

	@Override
	public int readInt() {
		byte[] readNBytes = inputBuffer.readNBytes(4);
		return LitteEndianNumberUtils.toInt(readNBytes);
	}

	@Override
	public byte[] readNBytes(int length) {
		return inputBuffer.readNBytes(length);
	}
	
	@Override
	public BigInteger readLenencInteger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readLenencString() {
		byte lenencFirstByte = this.readByte();
		long readLengthEncodedInteger = MySQLByteUtils.readLengthEncodedInteger(lenencFirstByte, this);
		byte[] bs = this.inputBuffer.readNBytes((int)readLengthEncodedInteger);
		try {
			return new String(bs,"ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public short readShort() {
		byte[] readNBytes = inputBuffer.readNBytes(2);
		return LitteEndianNumberUtils.toShort(readNBytes);
	}

	@Override
	public byte readByte() {
		return (byte)this.read();
	}
	
	@Override
	public BigInteger readULong() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int readUShort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBuffer(Object buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int readableBytes() {
		return this.inputBuffer.readableLength();
	}

}
