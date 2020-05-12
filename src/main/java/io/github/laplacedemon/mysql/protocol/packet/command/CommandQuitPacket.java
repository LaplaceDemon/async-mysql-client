package io.github.laplacedemon.mysql.protocol.packet.command;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

public class CommandQuitPacket extends MySQLPacket {
	private final byte TypeFlag = 0x01;
	
	public CommandQuitPacket() {
	    super.packetBodyLength = 1;
	}
	
	@Override
    public void read(InputMySQLBuffer buffer) throws IOException {
        read(buffer, false);
    }

    public void read(InputMySQLBuffer buffer, boolean needReadTypeByte) throws IOException {
        if (needReadTypeByte) {
            byte typeFlagByte = buffer.readByte();
            if(this.TypeFlag != typeFlagByte) {
                throw new RuntimeException();
            }
        }
    }
    
    @Override
    public void write(MySQLMessage message, OutputMySQLBuffer output) {
        super.write(message, null);
        message.writeByte(this.TypeFlag);
        
        if(output != null) {
            output.write(message);
        }
    }
    
    public int getPacketBodyLength() {
        return packetBodyLength;
    }
}
