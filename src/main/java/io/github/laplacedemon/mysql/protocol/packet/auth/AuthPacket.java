package io.github.laplacedemon.mysql.protocol.packet.auth;

import java.util.LinkedList;
import java.util.List;

import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.commons.CapabilityFlag;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;


/**
 * 握手响应包。 <br>
 * https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse
 * 
 * @author shijiaqi
 */
public class AuthPacket extends MySQLPacket {
    /**
     * 客户端的权能标志0。（不是服务器）
     * 2 Bytes
     */
    private short capabilityFlag0;
    /**
         * 客户端的权能标志1。
     * 2 Bytes
     */
    private short capabilityFlag1;
    
    /**
         * 最大包长度
     * 4 Bytes
     */
    private long maxPacketSize;

    /**
         * 字符集
     * 1 Byte
     */
    private byte charsetFlag;

    /**
         * 保留字节
     * 23 Bytes
     */
    private byte[] ReservedBytes = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };;
    
    /**
         * 用户名（0结尾）
     */
    private String username;

    /**
     * hash后的密码 （length (1 byte) coded）
     */
    private byte[] authResponse;

    /**
	 * 数据库名称（0结尾）<br>
	 * 只有client capabilities中有CLIENT_CONNECT_WITH_DB时，此字段才有效。
     */
    private String database;

    /**
     * 客户端验证插件名称（0结尾）<br>
     * 只有client capabilities中有CLIENT_PLUGIN_AUTH时，此字段才有效。
     * 若使用mysql默认的auth机制，此处应该为mysql_native_password
     */
    private String authPluginName;

    private byte connectionAttributesLength = 0;

    private byte[] otherBytes;

    private List<ConnectionAttribute> connectionAttributesList = new LinkedList<>();

    public static class ConnectionAttribute {
        public int nameLength;
        public String name;
        public int valueLength;
        public String value;
    }
    
	@Override
	public void write(MySQLMessage message,OutputMySQLBuffer output) {
		super.write(message, null);
		message.writeLEShort(this.capabilityFlag0);
		message.writeLEShort(this.capabilityFlag1);
		message.writeLEUInt(this.maxPacketSize);
		message.writeByte(this.charsetFlag);
		message.writeBytes(this.ReservedBytes);
		message.writeStringNul(this.username);
		message.writeBytes((byte)this.authResponse.length, this.authResponse);
		
		if((short) (this.capabilityFlag0 & CapabilityFlag.CLIENT_CONNECT_WITH_DB) != 0) {
			message.writeStringNul(this.database);
		}
		
		if((short) (this.capabilityFlag1 & CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH) != 0) {
			message.writeStringNul(this.authPluginName);
		}
		
		output.write(message);
	}

	public short getCapabilityFlag0() {
		return capabilityFlag0;
	}

	public void setCapabilityFlag0(short capabilityFlag0) {
		this.capabilityFlag0 = capabilityFlag0;
	}

	public short getCapabilityFlag1() {
		return capabilityFlag1;
	}

	public void setCapabilityFlag1(short capabilityFlag1) {
		this.capabilityFlag1 = capabilityFlag1;
	}

	public long getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(long maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	public byte getCharsetFlag() {
		return charsetFlag;
	}

	public void setCharsetFlag(byte charsetFlag) {
		this.charsetFlag = charsetFlag;
	}

	public byte[] getReservedBytes() {
		return ReservedBytes;
	}

	public void setReservedBytes(byte[] reservedBytes) {
		ReservedBytes = reservedBytes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getAuthPluginName() {
		return authPluginName;
	}

	public void setAuthPluginName(String authPluginName) {
		this.authPluginName = authPluginName;
	}

	public byte getConnectionAttributesLength() {
		return connectionAttributesLength;
	}

	public void setConnectionAttributesLength(byte connectionAttributesLength) {
		this.connectionAttributesLength = connectionAttributesLength;
	}

	public byte[] getOtherBytes() {
		return otherBytes;
	}

	public void setOtherBytes(byte[] otherBytes) {
		this.otherBytes = otherBytes;
	}

	public List<ConnectionAttribute> getConnectionAttributesList() {
		return connectionAttributesList;
	}

	public void setConnectionAttributesList(List<ConnectionAttribute> connectionAttributesList) {
		this.connectionAttributesList = connectionAttributesList;
	}

	public byte[] getAuthResponse() {
		return authResponse;
	}

	public void setAuthResponse(byte[] authResponse) {
		this.authResponse = authResponse;
	}

	@Override
	public void autoSetLength() {
		int packetLength = 2 + 2 + 4 + 1 + 23
    			+ (username.length() + 1)
    			+ (authResponse.length + 1) ;
    			
		if((short) (this.capabilityFlag0 & CapabilityFlag.CLIENT_CONNECT_WITH_DB) != 0) {
			packetLength += (database.length() + 1);
		}
		
		if((short) (this.capabilityFlag1 & CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH) != 0) {
			packetLength += (authPluginName.length() + 1);
		}
		
		this.packetBodyLength = packetLength;
	}
	
    
//    @Override
//    public void read(ByteBuf buffer) {
//        byte[] packetLengthBytes = new byte[3];
//        buffer.readBytes(packetLengthBytes);
//        super.length = LitteEndianNumberUtils.toIntFrom3Bytes(packetLengthBytes);
//        super.sequenceId = buffer.readByte();
//        
////        this.capabilityFlag = buffer.readInt();
//        this.capabilityFlag1 = BigEndianNumberUtils.toBigEndian(buffer.readShort()); 
//        this.capabilityFlag2 = BigEndianNumberUtils.toBigEndian(buffer.readShort()); 
//        
//        this.maxPacketSize = buffer.readUnsignedIntLE();
//        this.charsetFlag = buffer.readByte();
//        
//        buffer.readBytes(this.reservedBytes);// 23个0x00
//        
//        byte[] usernameBytes = NettyByteBufUtils.readBytesUnitlNULL(buffer);
//        this.username = new String(usernameBytes);
//        buffer.skipBytes(1);
//
//        if((this.capabilityFlag2 & CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA) != 0){
//            BigInteger readLenencInt = NettyByteBufUtils.readLenencInt(buffer);
//            byte[] authResponseBytes = new byte[readLenencInt.intValue()];
//            buffer.readBytes(authResponseBytes);
//            this.authResponse = new String(authResponseBytes);
//        }
////        buffer.skipBytes(1);
//
//        if((this.capabilityFlag1 & CapabilityFlag.CLIENT_CONNECT_WITH_DB) != 0){
//            byte[] databaseBytes = NettyByteBufUtils.readBytesUnitlNULL(buffer);
//            this.database = new String(databaseBytes);
//            buffer.skipBytes(1);
//        }
//
//        if((this.capabilityFlag2 & CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH) != 0 ){
//            byte[] authPluginNameBytes = NettyByteBufUtils.readBytesUnitlNULL(buffer);
//            this.authPluginName = new String(authPluginNameBytes);
//            buffer.skipBytes(1);
//        }
//        
//        if((this.capabilityFlag2 & CapabilityFlag.Upper.CLIENT_CONNECT_ATTRS) != 0){
//            long connectionAttributesLength = NettyByteBufUtils.readLenencInt(buffer).longValue();
//            int readBytesLength = 0;
//            while(connectionAttributesLength - readBytesLength > 0) {
//                ConnectionAttribute ca = new ConnectionAttribute();
//                
//                // read name;
//                ca.nameLength = NettyByteBufUtils.readLenencInt(buffer).intValue();
//                byte[] nameBytes = new byte[ca.nameLength];
//                buffer.readBytes(nameBytes);
//                ca.name = new String(nameBytes);
//                
//                // read value;
//                ca.valueLength = NettyByteBufUtils.readLenencInt(buffer).intValue();
//                byte[] valueBytes = new byte[ca.valueLength];
//                buffer.readBytes(valueBytes);
//                ca.value = new String(valueBytes);
//                
//                this.connectionAttributesList.add(ca);
//                
//                readBytesLength += (ca.nameLength + 1 + ca.valueLength + 1);
//            }
//        }
//        
//    }

    

}
