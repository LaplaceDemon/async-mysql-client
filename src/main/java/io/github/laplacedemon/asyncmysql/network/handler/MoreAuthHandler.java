package io.github.laplacedemon.asyncmysql.network.handler;

import io.github.laplacedemon.asyncmysql.Config;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MoreAuthHandler extends ChannelInboundHandlerAdapter {
	private final int packetHeadLength = 4;
	
	private Config config;
	private OutputMySQLBuffer outputMySQLBuffer;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
	}
}
