package io.github.laplacedemon.mysql.protocol.packet;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;

public interface ReadablePacket {
	void read(InputMySQLBuffer buffer) throws IOException;
}