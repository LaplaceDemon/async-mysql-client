package io.github.laplacedemon.mysql.protocol.packet;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.util.LitteEndianNumberUtils;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;

public abstract class MySQLPacket implements ReadablePacket,WritablePacket {
    protected int packetBodyLength;
    protected byte sequenceId;
    
    public MySQLPacket() {
		super();
	}
    
    public MySQLPacket(int packetBodyLength, byte sequenceId) {
		super();
		this.packetBodyLength = packetBodyLength;
		this.sequenceId = sequenceId;
	}

	public int getPacketBodyLength() {
        return packetBodyLength;
    }

    public void setPacketBodyLength(int packetBodyLength) {
        this.packetBodyLength = packetBodyLength;
    }

    public byte getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(byte sequenceId) {
        this.sequenceId = sequenceId;
    }
    
    public void autoSetLength() {
    	this.packetBodyLength = 0;
    }
    
    @Override
    public void read(InputMySQLBuffer buffer) throws IOException {
    	byte[] buf = new byte[4];
    	buffer.readNBytes(buf, 0, 4);
    	this.packetBodyLength = MySQLByteUtils.getPacketLength(buf);
    	this.sequenceId = buf[3];
    }
    
    @Override
    public void write(MySQLMessage message, OutputMySQLBuffer output) {
    	byte[] packetLengthBytes = LitteEndianNumberUtils.to3Bytes(this.packetBodyLength);
    	message.writeBytes(packetLengthBytes);
    	message.writeByte(this.sequenceId);
    }

}
