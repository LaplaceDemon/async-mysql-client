package io.github.laplacedemon.asyncmysql.network;

import java.nio.ByteBuffer;

import io.github.laplacedemon.asyncmysql.network.buffer.ByteBufferMySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class OutputMySQLBufferAdapter implements OutputMySQLBuffer {
	private ChannelHandlerContext ctx;
	
	public OutputMySQLBufferAdapter() {}
	
	@Override
	public void write(MySQLMessage message) {
		ByteBuffer byteBuffer = ((ByteBufferMySQLMessage)message).getMessage();
		ByteBuf nettyBuf = Unpooled.wrappedBuffer(byteBuffer); 
		ctx.writeAndFlush(nettyBuf);
	}

	public void setContext(final ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

}
