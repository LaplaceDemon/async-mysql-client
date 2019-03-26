package io.github.laplacedemon.asyncmysql.network.handler;

import java.util.List;
import io.github.laplacedemon.asyncmysql.network.ByteBufAdapter;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.auth.AuthSwitchRequestPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.ErrorPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.OKayPacket;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class AuthSwitchDecoder extends ByteToMessageDecoder {
	private final int packetHeadLength = 4;
	private InputMySQLBuffer inputMySQLBuffer;
	
	public AuthSwitchDecoder() {
		this.inputMySQLBuffer = new ByteBufAdapter();
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inputBuffer, List<Object> out) throws Exception {
		inputMySQLBuffer.setBuffer(inputBuffer);
		
		if (packetHeadLength <= inputBuffer.readableBytes()) {
			byte[] headPacket = new byte[packetHeadLength];
			inputBuffer.getBytes(inputBuffer.readerIndex(), headPacket);

			int packetBodyLength = MySQLByteUtils.getPacketLength(headPacket);
			byte sequenceId = headPacket[3];
			
			if ((packetHeadLength + packetBodyLength) <= inputBuffer.readableBytes()) {
				// 消息足够长，可以解码。读取，但不需要。
				inputBuffer.skipBytes(packetHeadLength);
				
				if (packetBodyLength == 2) {
					AuthSwitchRequestPacket authSwitchRequestPacket = new AuthSwitchRequestPacket(packetBodyLength, sequenceId);
					authSwitchRequestPacket.read(inputMySQLBuffer);
					authSwitchRequestPacket.setSequenceId(sequenceId);
					out.add(authSwitchRequestPacket);
				} else {
					int responseType = inputMySQLBuffer.read();
					if (responseType == 0) {
						OKayPacket okPacket = new OKayPacket();
						okPacket.setPacketBodyLength(packetBodyLength);
						okPacket.setSequenceId(sequenceId);
						okPacket.read(inputMySQLBuffer);
						out.add(okPacket);
						return ;
					} else if (responseType == (byte)0xff) {
						ErrorPacket errorPacket = new ErrorPacket();
						errorPacket.setPacketBodyLength(packetBodyLength);
						errorPacket.setSequenceId(sequenceId);
						errorPacket.read(inputMySQLBuffer);
						out.add(errorPacket);
						return ;
					} else {
						System.out.println("错误，握手失败，可能是协议解析问题");
						inputMySQLBuffer.skip(packetBodyLength - 1);
					}
				}
			}
		}
	}

}
