package io.github.laplacedemon.asyncmysql.network.handler;

import java.sql.ResultSet;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CommandHandler extends ChannelInboundHandlerAdapter {	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ResultSet resultSet = (ResultSet)msg;
		// 回调
		Consumer<ResultSet> commandResultCallback = AttributeMap.ioSession(ctx).getCommandResultCallback();
		if(commandResultCallback != null) {
			commandResultCallback.accept(resultSet);
		}
	}
}
