package io.github.laplacedemon.mysql.protocol.packet;

import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;

public interface WritablePacket {
	void write(MySQLMessage mysqlMessage,OutputMySQLBuffer output);
}
