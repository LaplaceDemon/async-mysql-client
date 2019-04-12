package io.github.laplacedemon.asyncmysql.network.handler;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.util.BiLongLongConsumer;
import io.github.laplacedemon.mysql.protocol.packet.response.ErrorPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.OKayPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CommandHandler extends ChannelInboundHandlerAdapter {	
	
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // System.out.println("CommandHandler.channelInactive");
        // 连接正常关闭。
        Runnable closeRunnable = AttributeMap.ioSession(ctx).getCloseRunnable();
        closeRunnable.run();
    }
    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof OKayPacket) {
			OKayPacket ok = (OKayPacket)msg;
			// callback
			BiLongLongConsumer updateResultCallback = AttributeMap.ioSession(ctx).getUpdateResultCallback();
			if (updateResultCallback != null) {
				BigInteger affectedRows = ok.getAffectedRows();
				BigInteger lastInsertId = ok.getLastInsertId();
				updateResultCallback.accept(affectedRows.longValue(), lastInsertId.longValue());
			}
		} else if (msg instanceof ErrorPacket) {
			ErrorPacket error = (ErrorPacket)msg;
			// callback
			SQLException sqlException = new SQLException(error.getErrorMesssage());
			Consumer<Throwable> throwableConsumer = AttributeMap.ioSession(ctx).getThrowableConsumer();
			throwableConsumer.accept(sqlException);
		} else if (msg instanceof ResultSet) {
			ResultSet resultSet = (ResultSet)msg;
			// callback
			Consumer<ResultSet> commandResultCallback = AttributeMap.ioSession(ctx).getQueryResultCallback();
			if (commandResultCallback != null) {
				commandResultCallback.accept(resultSet);
			}
		}
	}
}
