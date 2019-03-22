package io.github.laplacedemon.asyncmysql.network.handler;

import java.util.List;

import io.github.laplacedemon.asyncmysql.Status;
import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.ByteBufAdapter;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.auth.HandShakeV10Packet;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class HandshakeDecoder extends ByteToMessageDecoder {
	private final int headPacketLength = 4;
	private InputMySQLBuffer inputMySQLBuffer;
	
	public HandshakeDecoder() {
		this.inputMySQLBuffer = new ByteBufAdapter();
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inputBuffer, List<Object> out) throws Exception {
		inputMySQLBuffer.setBuffer(inputBuffer);
		
		if (headPacketLength <= inputBuffer.readableBytes()) {
			byte[] headPacket = new byte[headPacketLength];
			inputBuffer.getBytes(inputBuffer.readerIndex(), headPacket);
			
			int packetLength = MySQLByteUtils.getPacketLength(headPacket);
			byte sequenceId = headPacket[3];
			
			if ((packetLength + headPacketLength) <= inputBuffer.readableBytes()) {
				// 消息足够长，可以解码。读取，但不需要。
				inputBuffer.skipBytes(headPacketLength);
				
				if (AttributeMap.ioSession(ctx).getStatus().equals(Status.HandShakeing)) {
					HandShakeV10Packet handShakeV10Packet = new HandShakeV10Packet(packetLength, sequenceId);
					handShakeV10Packet.read(inputMySQLBuffer);
					handShakeV10Packet.setSequenceId(sequenceId);
					out.add(handShakeV10Packet);
				}
			}
		}
	}

}
