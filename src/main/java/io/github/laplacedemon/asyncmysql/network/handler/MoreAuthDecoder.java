package io.github.laplacedemon.asyncmysql.network.handler;

import java.util.List;

import io.github.laplacedemon.asyncmysql.network.ByteBufAdapter;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthSwitchResponsePacket;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MoreAuthDecoder extends ByteToMessageDecoder {
	private final int headPacketLength = 4;
	private InputMySQLBuffer inputMySQLBuffer;
	
	public MoreAuthDecoder() {
		this.inputMySQLBuffer = new ByteBufAdapter();
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inputBuffer, List<Object> out) throws Exception {
		inputMySQLBuffer.setBuffer(inputBuffer);
		
		if (headPacketLength <= inputBuffer.readableBytes()) {
			byte[] headPacket = new byte[headPacketLength];
			inputBuffer.getBytes(inputBuffer.readerIndex(), headPacket);
			
			int packetBodyLength = MySQLByteUtils.getPacketLength(headPacket);
			byte sequenceId = headPacket[3];
			
			if ((packetBodyLength + headPacketLength) <= inputBuffer.readableBytes()) {
				// 消息足够长，可以解码。读取，但不需要。
				inputBuffer.skipBytes(headPacketLength);
				
				AuthSwitchResponsePacket authSwitchResponsePacket0 = new AuthSwitchResponsePacket();
				authSwitchResponsePacket0.setPacketBodyLength(packetBodyLength);
				authSwitchResponsePacket0.setSequenceId(sequenceId);
				authSwitchResponsePacket0.read(inputMySQLBuffer);
				
				out.add(authSwitchResponsePacket0);
			}
		}
	}

}
