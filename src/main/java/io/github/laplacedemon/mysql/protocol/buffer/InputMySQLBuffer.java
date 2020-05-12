package io.github.laplacedemon.mysql.protocol.buffer;

import java.math.BigInteger;

public interface InputMySQLBuffer {
	int read();

	int readNBytes(byte[] buf, int offset, int len);

	/**
	 * 包括最后一个字节
	 * @param b
	 *         byte
	 * @return
	 *         a byte array
	 */
	byte[] readUtils(byte b);
	
	/**
	 * 包括最后一个字节
	 * @param b
	 *         byte
	 * @param initBytes
	 *         init Bytes
	 * @return
	 *         a byte array
	 */
	byte[] readUtils(byte b, int initBytes);

	void skip(int length);

	int readInt();

	byte[] readNBytes(int length);

	short readShort();

	byte readByte();

	BigInteger readULong();

	int readUShort();

	BigInteger readLenencInteger();

	String readLenencString();

	void setBuffer(Object buffer);

	int readableBytes();

}
