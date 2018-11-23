package sjq.light.async.mysql.reactor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sjq.light.mysql.protocol.packet.response.resultset.FieldPacket;
import sjq.light.mysql.protocol.packet.response.resultset.ResultSetHeaderPacket;
import sjq.light.mysql.protocol.packet.response.resultset.RowPacket;

public class ResultMySQLPacket {
	private ResultSetHeaderPacket resultSetHeaderPacket;
	private List<FieldPacket> fieldPacketList;
	private List<RowPacket> rowPacketList;
	
	public ResultMySQLPacket() {
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
