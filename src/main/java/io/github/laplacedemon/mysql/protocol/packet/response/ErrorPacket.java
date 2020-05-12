package io.github.laplacedemon.mysql.protocol.packet.response;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

public class ErrorPacket extends MySQLPacket {
	/**
     * 类型值，恒为0xff
     * 1 Byte
     */
    private final byte TypeFlag = (byte)0xff;
    
    /**
     * 错误编号
     * 2 Bytes
     */
    private int errorCode;
    
    /**
     * SQL状态标志，恒为'#'字符（0x23 ）
     * 1 Bytes
     */
    private final byte sqlStateMarker = '#';
    /**
     * SQL状态标志
     * 5 Bytes
     */
    private byte[] sqlState;
    /**
     * 服务器消息（无结束符）
     */
    private String errorMesssage;
    
    public int getErrorCode() {
		return errorCode;
	}

	public byte[] getSqlState() {
		return sqlState;
	}

	public String getErrorMesssage() {
		return errorMesssage;
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
    	
		this.errorCode = buffer.readUShort();
		
		byte flagByte = buffer.readByte();
		if(this.sqlStateMarker != flagByte) {
			throw new RuntimeException();
		}
		
		this.sqlState = buffer.readNBytes(5);
		
		// 读取剩余所有字节
		int infoLength = super.packetBodyLength - (1 + 2 + 1 + 5);
		if(infoLength > 0) {
			byte[] serverInfoBytes = buffer.readNBytes(infoLength);
			this.errorMesssage = new String(serverInfoBytes);
		}
	}
    
}
