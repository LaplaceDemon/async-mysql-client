package io.github.laplacedemon.asyncmysql.network.handler;

import java.nio.charset.Charset;
import java.util.Arrays;

import io.github.laplacedemon.asyncmysql.Config;
import io.github.laplacedemon.asyncmysql.ServerInfo;
import io.github.laplacedemon.asyncmysql.Status;
import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.OutputMySQLBufferAdapter;
import io.github.laplacedemon.asyncmysql.network.buffer.ByteBufferMySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthMoreDataPacket;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthSwitchRequestPacket;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthSwitchResponsePacket;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AuthSwitchHandler extends ChannelInboundHandlerAdapter {
	private final int headPacketLength = 4;
	private Config config;
	private ServerInfo serverInfo;
	private OutputMySQLBufferAdapter outputMySQLBuffer;
	
	public AuthSwitchHandler() {
		this.outputMySQLBuffer = new OutputMySQLBufferAdapter();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		outputMySQLBuffer.setContext(ctx);
		
		AuthSwitchRequestPacket authSwitchRequestPacket = (AuthSwitchRequestPacket)msg;
		byte[] pluginNameData = authSwitchRequestPacket.getPluginName().getBytes();
		if(Arrays.equals(pluginNameData, new byte[] {(byte)3})) {
//			System.out.println("第二阶段验证，3");
			return ;
		}
		
		if(Arrays.equals(pluginNameData, new byte[] {(byte)4})) {
			System.out.println("第二阶段验证，4");
			// 如果是4
			// 返回2
			AuthMoreDataPacket responseAuthMoreDataPacket = new AuthMoreDataPacket();
			responseAuthMoreDataPacket.setSequenceId((byte)(authSwitchRequestPacket.getSequenceId() + 1));
			responseAuthMoreDataPacket.setStatus((byte)2);
			responseAuthMoreDataPacket.autoSetLength();
			System.out.println("将要写出数据：" + (responseAuthMoreDataPacket.getLength() + headPacketLength));
			MySQLMessage message = new ByteBufferMySQLMessage(responseAuthMoreDataPacket.getLength() + headPacketLength);
			responseAuthMoreDataPacket.write(message, outputMySQLBuffer);
			
			// 不再处理当前连接的io，但继续需要写数据。
			AttributeMap.ioSession(ctx).gotoStatus(Status.MoreAuthing);
			return ;
		} 
		
		
		AuthSwitchResponsePacket authSwitchResponsePacket0 = (AuthSwitchResponsePacket)msg;
		
		String publicKeyString = authSwitchResponsePacket0.getAuthPluginResponse();
		// 读到数据
		// 返回响应
		byte[] seed = serverInfo.getSeed();
		byte[] serverVersion = serverInfo.getServerVersion();
		
		String transformation;
		if(Arrays.compare(serverVersion, new byte[] {8,0,5}) > 0) {
			transformation = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
		} else {
			transformation = "RSA/ECB/PKCS1Padding";
		}
		
		byte[] encryptPassword = MySQLByteUtils.encryptPassword(config.getPassword(), seed, publicKeyString, transformation);
		
		// 返回数据
		System.out.println("返回数据");
		
		AuthSwitchResponsePacket authSwitchResponsePacket1 = new AuthSwitchResponsePacket();
		authSwitchResponsePacket1.setSequenceId((byte)(authSwitchResponsePacket0.getSequenceId()+1));
		authSwitchResponsePacket1.setAuthPluginResponse(new String(encryptPassword,Charset.forName("ISO8859_1")));
		authSwitchResponsePacket1.autoSetLength();
		
		MySQLMessage message = new ByteBufferMySQLMessage(authSwitchResponsePacket1.getLength() + headPacketLength);
		authSwitchResponsePacket1.write(message, outputMySQLBuffer);
		
		// 状态转换！
//		eventData.setStatus(Status.Authing);
	}
}
