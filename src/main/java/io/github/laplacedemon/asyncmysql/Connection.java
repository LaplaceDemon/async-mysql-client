package io.github.laplacedemon.asyncmysql;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.buffer.ByteBufferMySQLMessage;
import io.github.laplacedemon.asyncmysql.resultset.AsyncPreparedStatement;
import io.github.laplacedemon.asyncmysql.resultset.MySQLResultPacket;
import io.github.laplacedemon.mysql.protocol.packet.command.CommandQueryPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

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
		AttributeMap.ioSession(channel).setUpdateResultCallback(null);
		AttributeMap.ioSession(channel).setQueryResultCallback(co);
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

	public void executeUpdate(final AsyncPreparedStatement asyncPS, final BiConsumer<Long, Long> co) {
		this.executeUpdate(asyncPS.getStatement(), co);
	}
	
	public void executeUpdate(final String sql, final BiConsumer<Long, Long> co) {
		AttributeMap.ioSession(channel).setUpdateResultCallback(co);
		AttributeMap.ioSession(channel).setQueryResultCallback(null);
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
