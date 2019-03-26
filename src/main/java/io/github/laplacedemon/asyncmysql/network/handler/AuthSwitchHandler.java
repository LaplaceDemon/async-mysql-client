package io.github.laplacedemon.asyncmysql.network.handler;

import java.util.Arrays;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.Connection;
import io.github.laplacedemon.asyncmysql.Status;
import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.OutputMySQLBufferAdapter;
import io.github.laplacedemon.asyncmysql.network.buffer.ByteBufferMySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthMoreDataPacket;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthSwitchRequestPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.ErrorPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.OKayPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthSwitchHandler extends ChannelInboundHandlerAdapter {
	private final int headPacketLength = 4;
	private OutputMySQLBufferAdapter outputMySQLBuffer;
	
	public AuthSwitchHandler() {
		this.outputMySQLBuffer = new OutputMySQLBufferAdapter();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		outputMySQLBuffer.setContext(ctx);
		
		if(msg instanceof AuthSwitchRequestPacket) {
			AuthSwitchRequestPacket authSwitchRequestPacket = (AuthSwitchRequestPacket)msg;
			byte[] pluginNameData = authSwitchRequestPacket.getPluginName().getBytes();
			if(Arrays.equals(pluginNameData, new byte[] {(byte)3})) {
				System.out.println("第二阶段验证，3");
				return ;
			} else if(Arrays.equals(pluginNameData, new byte[] {(byte)4})) {
				/**
				 * mysql 重启后，机器第一次连接mysql就会发生该种校验
				 */
				System.out.println("第二阶段验证，4");
				AuthMoreDataPacket responseAuthMoreDataPacket = new AuthMoreDataPacket();
				responseAuthMoreDataPacket.setSequenceId((byte)(authSwitchRequestPacket.getSequenceId() + 1));
				responseAuthMoreDataPacket.setStatus((byte)2);
				responseAuthMoreDataPacket.autoSetLength();
				System.out.println("将要写出数据：" + (responseAuthMoreDataPacket.getPacketBodyLength() + headPacketLength));
				MySQLMessage message = new ByteBufferMySQLMessage(responseAuthMoreDataPacket.getPacketBodyLength() + headPacketLength);
				responseAuthMoreDataPacket.write(message, outputMySQLBuffer);
				
				// 不再处理当前连接的io，但继续需要写数据。
				AttributeMap.ioSession(ctx).gotoStatus(Status.MoreAuthing);
				return ;
			}
		} else if (msg instanceof ErrorPacket) {
			ErrorPacket error = (ErrorPacket)msg;
			System.out.println("MySQL 执行错误. " + error.getErrorMesssage());
			return ;
		} else if (msg instanceof OKayPacket) {
//			OKayPacket ok = (OKayPacket)msg;
			Consumer<Connection> handshakeSuccessCallback = AttributeMap.ioSession(ctx).getHandshakeSuccessCallback();
			final Connection connection = new Connection(ctx.channel());
			handshakeSuccessCallback.accept(connection);
			AttributeMap.ioSession(ctx).gotoStatus(Status.Commanding);
			return ;
		}
		
	}
}
