package io.github.laplacedemon.asyncmysql.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class AttributeMap {
	public final static AttributeKey<IOSession> IOSESSION_KEY = AttributeKey.valueOf("iosession");
	
	public final static IOSession ioSession(Channel channel) {
		Attribute<IOSession> attr = channel.attr(IOSESSION_KEY);
		return attr.get();
	}

	public static IOSession ioSession(ChannelHandlerContext ctx) {
		Attribute<IOSession> attr = ctx.channel().attr(IOSESSION_KEY);
		return attr.get();
	}
}
