package io.github.laplacedemon.asyncmysql.network.handler;

import java.nio.charset.Charset;
import java.util.Arrays;

import io.github.laplacedemon.asyncmysql.Config;
import io.github.laplacedemon.asyncmysql.Status;
import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.OutputMySQLBufferAdapter;
import io.github.laplacedemon.asyncmysql.network.buffer.ByteBufferMySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthSwitchResponsePacket;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MoreAuthHandler extends ChannelInboundHandlerAdapter {
	private final int packetHeadLength = 4;
	private OutputMySQLBufferAdapter outputMySQLBuffer;
	
	public MoreAuthHandler() {
		this.outputMySQLBuffer = new OutputMySQLBufferAdapter();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		outputMySQLBuffer.setContext(ctx);
		
		// handle message
		AuthSwitchResponsePacket authSwitchResponsePacket0 = (AuthSwitchResponsePacket)msg;
		
		String publicKeyString = authSwitchResponsePacket0.getAuthPluginResponse();
		byte[] seed = AttributeMap.ioSession(ctx).serverInfo().getSeed();
		byte[] serverVersion = AttributeMap.ioSession(ctx).serverInfo().getServerVersion();
		
		String transformation;
		if(Arrays.compare(serverVersion, new byte[] {8,0,5}) > 0) {
			transformation = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
		} else {
			transformation = "RSA/ECB/PKCS1Padding";
		}
		
		Config config = AttributeMap.ioSession(ctx).getConfig();
		byte[] encryptPassword = MySQLByteUtils.encryptPassword(config.getPassword(), seed, publicKeyString, transformation);
		
		// 返回响应事件
		AuthSwitchResponsePacket authSwitchResponsePacket1 = new AuthSwitchResponsePacket();
		authSwitchResponsePacket1.setSequenceId((byte)(authSwitchResponsePacket0.getSequenceId()+1));
		authSwitchResponsePacket1.setAuthPluginResponse(new String(encryptPassword,Charset.forName("ISO8859_1")));
		authSwitchResponsePacket1.autoSetLength();
		
		MySQLMessage message = new ByteBufferMySQLMessage(authSwitchResponsePacket1.getPacketBodyLength() + packetHeadLength);
		authSwitchResponsePacket1.write(message, outputMySQLBuffer);
				
		// 重新进入验证阶段
		AttributeMap.ioSession(ctx).gotoStatus(Status.AuthSwitch);
	}
}
