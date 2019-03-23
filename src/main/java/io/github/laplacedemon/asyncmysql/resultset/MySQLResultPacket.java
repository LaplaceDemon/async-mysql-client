package io.github.laplacedemon.asyncmysql.resultset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.github.laplacedemon.mysql.protocol.packet.response.resultset.FieldPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.resultset.ResultSetHeaderPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.resultset.RowPacket;

public class MySQLResultPacket {
	private ResultSetHeaderPacket resultSetHeaderPacket;
	private List<FieldPacket> fieldPacketList;
	private List<RowPacket> rowPacketList;
	
	public MySQLResultPacket() {
		this.fieldPacketList = new ArrayList<>();
		this.rowPacketList = new LinkedList<>();
	}
	
	public void setResultSetHeaderPacket(ResultSetHeaderPacket resultSetHeaderPacket) {
		this.resultSetHeaderPacket = resultSetHeaderPacket;
	}

	public ResultSetHeaderPacket resultSetHeaderPacket() {
		return resultSetHeaderPacket;
	}

	public List<FieldPacket> fieldPacketList() {
		return fieldPacketList;
	}

	public List<RowPacket> rowPacketList() {
		return rowPacketList;
	}

}
