package io.github.laplacedemon.mysql.protocol.packet.response.resultset;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

/**
 * https://dev.mysql.com/doc/internals/en/com-query-response.html#column-type
 * @author shijiaqi
 * 
 */
public class FieldPacket extends MySQLPacket {
    
    /**
     * 恒定为 "\3 d e f",
     * 4 Bytes。第一个字节为长度编码。
     * lenenc_str
     */
    private final String catalog = "def";
    
    /**
     * lenenc_str
     */
    private String database;  // 数据库名称
    /**
     * lenenc_str
     */
    private String table;    // 表名
    /**
     * lenenc_str
     */
    private String orgTable;  // 原始表名
    /**
     * lenenc_str
     */
    private String name;  // 列名称
    /**
     * lenenc_str
     */
    private String orgName;  // 列原始名称
    /**
     * length of fixed-length fields [0c] <br>
     * lenenc_int
     */
    private byte nextLength = 0x0c;
    /**
     * character set
     * 2 Bytes
     */
    private short charset;
    /**
     * column length
     * 4 Bytes
     */
    private int columnLength;
    /**
     * 1 Byte
     */
    private byte type;
    /**
     * 2 Bytes
     */
    private short flags;
    /**
     * 1 Byte
     */
    private byte decimals;
    
    /**
     * 始终为 0
     * 2 Bytes
     */
    private short filter;
    
	@Override
	public void read(InputMySQLBuffer buffer) throws IOException {
		String catalogStr = buffer.readLenencString();
		if(!this.catalog.equals(catalogStr)) {
			throw new RuntimeException();
		}
		this.database = buffer.readLenencString();
		this.table = buffer.readLenencString();
		this.orgTable = buffer.readLenencString();
		this.name = buffer.readLenencString();
		this.orgName = buffer.readLenencString();
		byte nextLength = buffer.readByte();
		if(this.nextLength != nextLength) {
			throw new RuntimeException();
		}
		this.charset = buffer.readShort();
		this.columnLength = buffer.readInt();
		this.type = buffer.readByte();
		this.flags = buffer.readShort();
		this.decimals = buffer.readByte();
		this.filter = buffer.readShort();
	}
	
	@Override
	public void write(MySQLMessage message, OutputMySQLBuffer output) {
		super.write(message, output);
	}
	
//  @Override
//  public void write(ChannelHandlerContext ctx) {
//      ByteBuf buffer = ctx.alloc().buffer();
//      buffer.writeByte(this.sequenceId);
//      
//      NettyByteBufUtils.writeLenencInt(buffer, BigInteger.valueOf(catalog.length()));
//      buffer.writeBytes(this.catalog.getBytes());
//      NettyByteBufUtils.writeLenencString(buffer,this.database);
//      NettyByteBufUtils.writeLenencString(buffer,this.table);
//      NettyByteBufUtils.writeLenencString(buffer,this.orgTable);
//      NettyByteBufUtils.writeLenencString(buffer,this.name);
//      NettyByteBufUtils.writeLenencString(buffer,this.orgName);
//      NettyByteBufUtils.writeLenencInt(buffer, BigInteger.valueOf(this.nextLength));
//      buffer.writeShortLE(this.charsetNumber);
//      buffer.writeIntLE(this.columnLength);
//      buffer.writeByte(this.columnType);
//      buffer.writeShort(this.flags);
//      buffer.writeByte(this.decimals);
//      buffer.writeByte(0x00);
//      buffer.writeByte(0x00);
//      
//      ctx.write(buffer);
//  }

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getOrgTable() {
		return orgTable;
	}

	public void setOrgTable(String orgTable) {
		this.orgTable = orgTable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public byte getNextLength() {
		return nextLength;
	}

	public void setNextLength(byte nextLength) {
		this.nextLength = nextLength;
	}

	public short getCharset() {
		return charset;
	}

	public void setCharset(short charset) {
		this.charset = charset;
	}

	public int getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(int columnLength) {
		this.columnLength = columnLength;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public short getFlags() {
		return flags;
	}

	public void setFlags(short flags) {
		this.flags = flags;
	}

	public byte getDecimals() {
		return decimals;
	}

	public void setDecimals(byte decimals) {
		this.decimals = decimals;
	}

	public String getCatalog() {
		return catalog;
	}
	
	public short getFilter() {
		return filter;
	}
	
}
