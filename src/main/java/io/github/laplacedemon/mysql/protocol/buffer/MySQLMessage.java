package io.github.laplacedemon.mysql.protocol.buffer;

public interface MySQLMessage {
	
	void writeShort(short data);

	void writeUInt(long data);

	void writeByte(byte data);

	void writeBytes(byte[] data);

	void writeBytes(byte data1, byte[] data2);

	void writeUShort(int data);

	void writeLenencString(String value);

	void writeLEShort(short data);

	void writeLEUInt(long data);

	/**
	 * 写字符串，结尾有0x00。
	 * https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::NulTerminatedString
	 * 
	 * @param data
	 *         write data
	 */
	void writeStringNul(String data);

	/**
	 * https://dev.mysql.com/doc/internals/en/string.html#packet-Protocol::RestOfPacketString
	/**
	 * @param sql
	 *         write string
	 */
	void writeStringEOF(String sql);

}
