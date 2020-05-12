package io.github.laplacedemon.mysql.protocol.packet.auth;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

public class HandShakeV10Packet extends MySQLPacket {
	
    public HandShakeV10Packet() {
		super();
	}

	public HandShakeV10Packet(int length, byte sequenceId) {
		super(length, sequenceId);
	}

	/**
     *  协议版本号
     *  1 byte
     */
    private byte ProtocolVersion;
    /**
     *  服务器版本信息
     *  N Bytes（Null-Termimated String）
     */
    private String serverVersion;
    
    /**
     * 服务器线程ID
     * 4 Bytes
     */
    private int threadId;  // connection_id

    /**
     * scramble的前8个字节
     * 8 Bytes
     */
    private byte[] seed0;

    /**
     * scramble的终止（始终为0x00）让scramble看起来是一个以0结尾的字符串
     * 1 bytes
     */
    private final byte EndByte0 = 0;
    
    /**
     * 服务器权能标志1（底16位）
     * 2 Bytes
     */
    private short capabilityFlag1;
    
    /**
     * 字节编码
     * 1 Bytes
     */
    private byte charsetFlag;
    /**
     * 服务器状态
     * 2 Bytes
     */
    private short serverStatus;
    /**
     * 服务器权能标志2（高16位）
     * 2 Bytes
     */
    private short capabilityFlag2;
    
    /**
     *  scramble的长度
     *  1 Bytes
     */
    private byte seedLength;
    
    /**
     * 填充值。（必须为都为0）
     * 10 Bytes
     */
    private final static byte[] ReservedBytes = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    /**
     * scramble的剩余字节。不包含'\0'
     * 至少12 Bytes。
     */
    private byte[] seed1;

    /**
     * scramble的终止（始终为0x00）让scramble看起来是一个以0结尾的字符串
     */
    private final byte EndByte1 = 0;
    /**
         * 验证插件的名称。
         * 结尾为0x00。
     */
    private String authPlugin;
    
    public byte getProtocolVersion() {
		return ProtocolVersion;
	}

	public void setProtocolVersion(byte protocolVersion) {
		ProtocolVersion = protocolVersion;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public byte[] getSeed0() {
		return seed0;
	}

	public void setSeed0(byte[] seed0) {
		this.seed0 = seed0;
	}

	public short getCapabilityFlag1() {
		return capabilityFlag1;
	}

	public void setCapabilityFlag1(short capabilityFlag1) {
		this.capabilityFlag1 = capabilityFlag1;
	}

	public byte getCharsetFlag() {
		return charsetFlag;
	}

	public void setCharsetFlag(byte charsetFlag) {
		this.charsetFlag = charsetFlag;
	}

	public short getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(short serverStatus) {
		this.serverStatus = serverStatus;
	}

	public short getCapabilityFlag2() {
		return capabilityFlag2;
	}

	public void setCapabilityFlag2(short capabilityFlag2) {
		this.capabilityFlag2 = capabilityFlag2;
	}

	public byte getSeedLength() {
		return seedLength;
	}

	public void setSeedLength(byte seedLength) {
		this.seedLength = seedLength;
	}

	public byte[] getSeed1() {
		return seed1;
	}

	public void setSeed1(byte[] seed1) {
		this.seed1 = seed1;
	}

	public String getAuthPlugin() {
		return authPlugin;
	}

	public void setAuthPlugin(String authPlugin) {
		this.authPlugin = authPlugin;
	}

	public byte getEndByte0() {
		return EndByte0;
	}

	public byte getEndByte1() {
		return EndByte1;
	}

	@Override
	public void read(InputMySQLBuffer buffer) throws IOException {
//    	super.read(buffer);
    	
		byte readByte = buffer.readByte();
		this.ProtocolVersion = readByte;
    	
    	byte[] readUtils = buffer.readUtils((byte)0, 16);
		byte[] serverVersionBytes = readUtils;
		this.serverVersion = new String(serverVersionBytes);
    	
    	this.threadId = buffer.readInt();
    	
    	this.seed0 = buffer.readNBytes(8);
    	
    	buffer.skip(1);
    	
		this.capabilityFlag1 = buffer.readShort();
		
    	this.charsetFlag = buffer.readByte();
    	
    	this.serverStatus = buffer.readShort();
    	
    	this.capabilityFlag2 = buffer.readShort();
    	
    	this.seedLength = buffer.readByte();
    	
    	buffer.skip(10);
    	
    	this.seed1 = buffer.readNBytes(seedLength-1 - this.seed0.length);
    	
    	buffer.skip(1);
    	
    	byte[] authPluginBytes = buffer.readUtils((byte)0);
    	this.authPlugin = new String(authPluginBytes);
    }
    
//    @Override
//    public void write(ChannelHandlerContext ctx) {
//        ByteBuf buffer = ctx.alloc().buffer();
//        buffer.writeByte(this.sequenceId);
//
//        buffer.writeByte(this.protocolVersion);
//
//        buffer.writeBytes(this.serverVersion.getBytes());
//        buffer.writeByte((byte) 0);
//
//        buffer.writeInt(LitteEndianNumberUtils.toLitteEndian(this.threadId));
//
//        if(this.seed==null || this.seed.length!= 8){
//            throw new RuntimeException("the seed is error. the seed of length is too short.");
//        } else {
//            buffer.writeBytes(this.seed);
//        }
//        
//        buffer.writeByte(PaddingByte);
//
//        buffer.writeShort(LitteEndianNumberUtils.toLitteEndian(this.capabilityFlag1));
//
//        buffer.writeByte(this.charsetFlag);
//
//        buffer.writeShort(LitteEndianNumberUtils.toLitteEndian(this.serverStatus));
//
//        buffer.writeShort(LitteEndianNumberUtils.toLitteEndian(this.capabilityFlag2));
//
//        if ((this.capabilityFlag2 & CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH) != 0) {
//            // length
//            // authPluginData2 以 NULL 结尾。 seed2 在这里至少要12 bytes ,所以authPluginData2实际上是13 bytes
//
//            // seed1.length + seed2.length + 1 的长度。
//            int authPluginDataLength = ((seed2 != null) ? seed2.length + 1 + 8 : 1 + 8);
//            buffer.writeByte((byte) authPluginDataLength);
//
//            buffer.writeBytes(ReservedBytes);
//
//            if ((this.capabilityFlag1 & CapabilityFlag.CLIENT_SECURE_CONNECTION) != 0) {
//                if (this.seed2 == null ||  this.seed2.length < 12) {
//                    throw new RuntimeException("the seed2 is error. the seed2 of length is too short.");
//                } else {
//                    buffer.writeBytes(this.seed2);
//                }
//            }
//
//            // 以 authPluginData2 以 NULL 结尾。
//            buffer.writeByte((byte) 0);
//        } else {
//            buffer.writeByte((byte) 0);
//            buffer.writeBytes(ReservedBytes);
//        }
//
//        if ((this.capabilityFlag2 & CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH) != 0) {
//            if (StringUtils.isEmpty(this.authPluginName)) {
//                buffer.writeByte((byte) 0);
//            } else {
//                buffer.writeBytes(this.authPluginName.getBytes());
//                buffer.writeByte((byte) 0);
//            }
//        }
//        
//        ctx.write(buffer);
//    }
}
