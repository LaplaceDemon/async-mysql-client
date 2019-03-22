package io.github.laplacedemon.asyncmysql;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.resultset.AsyncPreparedStatement;
import io.github.laplacedemon.mysql.protocol.packet.command.CommandQueryPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import sjq.light.async.mysql.reactor_tmp.ByteBufferMySQLMessage;
import sjq.light.async.mysql.reactor_tmp.MySQLResultPacket;

public class Connection {
	private Channel channel;
	
	public Connection(Channel channel) {
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}

	public AsyncPreparedStatement prepareStatement(final String sql) {
		AsyncPreparedStatement asyncPreparedStatement = new AsyncPreparedStatement(sql);
		return asyncPreparedStatement;
	}

	public void executeQuery(final AsyncPreparedStatement asyncPS, final Consumer<ResultSet> co) {
		this.executeQuery(asyncPS.getStatement(), co);
	}
	
	public void executeQuery(final String sql, final Consumer<ResultSet> co) {
		AttributeMap.ioSession(channel).setCommandResultCallback(co);
		AttributeMap.ioSession(channel).setResultPacketList(new MySQLResultPacket());
		
		CommandQueryPacket command = new CommandQueryPacket();
		
		command.setSql(sql);
		command.setSequenceId((byte)0);
		command.autoSetLength();
		
		ByteBufferMySQLMessage mySQLMessage = new ByteBufferMySQLMessage(command.getLength() + 4);
		command.write(mySQLMessage, null);
		
		ByteBuffer message = mySQLMessage.getMessage();
		ByteBuf buf = Unpooled.wrappedBuffer(message.array());
		
		this.channel.writeAndFlush(buf);
	}
	
}
