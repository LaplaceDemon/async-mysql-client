package io.github.laplacedemon.asyncmysql.network.handler;

import io.github.laplacedemon.asyncmysql.Config;
import io.github.laplacedemon.asyncmysql.Status;
import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.OutputMySQLBufferAdapter;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.commons.CapabilityFlag;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthPacket;
import io.github.laplacedemon.mysql.protocol.packet.auth.HandShakeV10Packet;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import sjq.light.async.mysql.reactor_tmp.ByteBufferMySQLMessage;

public class HandshakeHandler extends ChannelInboundHandlerAdapter {
	private final int headPacketLength = 4;
	private OutputMySQLBufferAdapter outputMySQLBuffer;
	
	public HandshakeHandler() {
		this.outputMySQLBuffer = new OutputMySQLBufferAdapter();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		outputMySQLBuffer.setContext(ctx);
		HandShakeV10Packet handShakeV10Packet = (HandShakeV10Packet)msg;
		
		Config config = AttributeMap.ioSession(ctx).getConfig();
		
		// 读取握手数据完毕。发送校验数据。
		String database = config.getDatabase();
		String username = config.getUsername();
		String password = config.getPassword();

		AuthPacket authPack = new AuthPacket();
		
		short capabilityFlag0 = CapabilityFlag.CLIENT_LONG_PASSWORD
				| CapabilityFlag.CLIENT_FOUND_ROWS | CapabilityFlag.CLIENT_LONG_FLAG
				| CapabilityFlag.CLIENT_PROTOCOL_41 | CapabilityFlag.CLIENT_INTERACTIVE
				| CapabilityFlag.CLIENT_TRANSACTIONS
				| CapabilityFlag.CLIENT_SECURE_CONNECTION;

		if (database != null) {
			capabilityFlag0 |= CapabilityFlag.CLIENT_CONNECT_WITH_DB;
		}

		short capabilityFlag1 = CapabilityFlag.Upper.CLIENT_MULTI_RESULTS
				| CapabilityFlag.Upper.CLIENT_PS_MULTI_RESULTS
				| CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH |
//        				CapabilityFlag.Upper.CLIENT_CONNECT_ATTRS |
				CapabilityFlag.Upper.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA
				| CapabilityFlag.Upper.CLIENT_DEPRECATE_EOF;

		long maxPacketSize = 16 * 1024 * 1024 - 1;
		byte charsetFlag = (byte) 0xff;
		byte seedLength = handShakeV10Packet.getSeedLength();
		byte[] seedBytes = new byte[seedLength - 1];
		byte[] seed0 = handShakeV10Packet.getSeed0();
		byte[] seed1 = handShakeV10Packet.getSeed1();
		System.arraycopy(seed0, 0, seedBytes, 0, seed0.length);
		System.arraycopy(seed1, 0, seedBytes, seed0.length, seed1.length);
		byte[] authResponse = MySQLByteUtils.cachingSHA2Password(password.getBytes(), seedBytes);

		authPack.setCapabilityFlag0(capabilityFlag0);
		authPack.setCapabilityFlag1(capabilityFlag1);
		authPack.setMaxPacketSize(maxPacketSize);
		authPack.setCharsetFlag(charsetFlag);
		authPack.setUsername(username);
		authPack.setAuthResponse(authResponse);
		authPack.setDatabase(database);
		authPack.setAuthPluginName("caching_sha2_password");
		authPack.setSequenceId((byte) (handShakeV10Packet.getSequenceId() + 1));
		authPack.autoSetLength();
		
		// 设置服务器信息。
		AttributeMap.ioSession(ctx).serverInfo().setSeed(seedBytes);
		
		String serverVersion = handShakeV10Packet.getServerVersion();
		String[] splitServerVersion = serverVersion.split(".");
		
		byte[] bytesServerVersion = new byte[splitServerVersion.length];
		for(int i = 0;i<splitServerVersion.length; i++) {
			String strItem = splitServerVersion[i];
			bytesServerVersion[i] = Byte.valueOf(strItem).byteValue();
		}
		
		// 设置服务器信息。
		AttributeMap.ioSession(ctx).serverInfo().setServerVersion(bytesServerVersion);

		// write out
		MySQLMessage message = new ByteBufferMySQLMessage(authPack.getLength() + headPacketLength);
		authPack.write(message, outputMySQLBuffer);
		
		// 进入验证阶段
		AttributeMap.ioSession(ctx).gotoStatus(Status.AuthSwitch);
	}
	
}