package io.github.laplacedemon.mysql.protocol.buffer;

public interface OutputMySQLBuffer {
	public void write(MySQLMessage message);
}
