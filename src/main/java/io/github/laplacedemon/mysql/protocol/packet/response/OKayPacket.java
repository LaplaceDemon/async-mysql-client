package io.github.laplacedemon.mysql.protocol.packet.response;

import java.io.IOException;
import java.math.BigInteger;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

public class OKayPacket extends MySQLPacket {
    /**
     * 类型值，恒为0x00
     * 1 Byte
     */
    private final byte TypeFlag = (byte)0x00;
    
    /**
     * 影响的行数
     * lenenc编码
     */
    private BigInteger affectedRows = BigInteger.ZERO;
    
    /**
     * 最后插入的ID值
     * lenenc编码
     */
    private BigInteger lastInsertId = BigInteger.ZERO;
    /**
     * 服务器状态
     * 2 Bytes
     */
    private short statusFlags = 0;
    /**
     * 告警数
     * 2 字节
     */
    private int warnings = 0;
    /**
     * 服务器消息（无结束符）
     */
    private String info;
    
	public byte getTypeFlag() {
		return TypeFlag;
	}

	public BigInteger getAffectedRows() {
		return affectedRows;
	}

	public BigInteger getLastInsertId() {
		return lastInsertId;
	}

	public short getStatusFlags() {
		return statusFlags;
	}

	public int getWarnings() {
		return warnings;
	}
	
	public String getInfo() {
		return info;
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
		
		this.affectedRows = buffer.readLenencInteger();
		this.lastInsertId = buffer.readLenencInteger();
		this.statusFlags = buffer.readShort();
		this.warnings = buffer.readUShort();
		
		// 读取剩余所有字节
		// TODO 这样处理是错误的。剩余字节可能还包含其他信息。
		int readableBytes = buffer.readableBytes();
		byte[] serverInfoBytes = buffer.readNBytes(readableBytes);
		this.info = new String(serverInfoBytes);
	}
	
	@Override
	public void write(MySQLMessage message, OutputMySQLBuffer output) {
		super.write(message, null);
	}
    
//    public OKayPacket(byte sequenceId0, short capabilityFlag1, short capabilityFlag2) {
//        super();
//        this.sequenceId0 = sequenceId0;
//        this.capabilityFlag1 = capabilityFlag1;
//        this.capabilityFlag2 = capabilityFlag2;
//    }
//
//    @Override
//    public void write(ChannelHandlerContext ctx) {
//        ByteBuf buffer = ctx.alloc().buffer();
//        super.sequenceId = (byte) (this.sequenceId0 + 1);
//        buffer.writeByte(super.sequenceId);
//        buffer.writeByte(TYPE_FLAG);
//        NettyByteBufUtils.writeLenencInt(buffer, affectedRows);
//        NettyByteBufUtils.writeLenencInt(buffer, lastInsertId);
//        if((this.capabilityFlag1 & CapabilityFlag.CLIENT_PROTOCOL_41)!=0){
//            buffer.writeShort(statusFlags);
//            buffer.writeShortLE(warnings);
//        } else if((this.capabilityFlag1 & CapabilityFlag.CLIENT_TRANSACTIONS)!=0){
//            buffer.writeShort(statusFlags);
//        }
//        
//        if ((this.capabilityFlag2 & CapabilityFlag.Upper.CLIENT_SESSION_TRACK)!=0) {
//            if(this.info != null && this.info.length() > 0) {
//                int strLength = this.info.length();
//                NettyByteBufUtils.writeLenencInt(buffer, BigInteger.valueOf(strLength));
//                buffer.writeBytes(this.info.getBytes());
//            }
//            
//            if((this.statusFlags & ServerStatusFlag.SERVER_SESSION_STATE_CHANGED)!=0){
//                if(this.sessionStateChanges != null && this.sessionStateChanges.length() > 0) {
//                    int strLength = this.sessionStateChanges.length();
//                    NettyByteBufUtils.writeLenencInt(buffer, BigInteger.valueOf(strLength));
//                    buffer.writeBytes(this.sessionStateChanges.getBytes());
//                }
//            }
//        } else {
//            if(this.info != null && this.info.length() > 0) {
//                buffer.writeBytes(this.info.getBytes());
//            }
//        }
//        
//        ctx.channel().write(buffer);
//    }
}
