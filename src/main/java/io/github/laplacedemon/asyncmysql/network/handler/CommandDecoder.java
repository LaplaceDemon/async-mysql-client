package io.github.laplacedemon.asyncmysql.network.handler;

import java.sql.ResultSet;
import java.util.List;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.ByteBufAdapter;
import io.github.laplacedemon.asyncmysql.resultset.AsyncResultSet;
import io.github.laplacedemon.asyncmysql.resultset.MySQLResultPacket;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.response.ErrorPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.OKayPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.resultset.FieldPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.resultset.ResultSetHeaderPacket;
import io.github.laplacedemon.mysql.protocol.packet.response.resultset.RowPacket;
import io.github.laplacedemon.mysql.protocol.util.MySQLByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class CommandDecoder extends ByteToMessageDecoder {
	private final int packetHeadLength = 4;
	private InputMySQLBuffer inputMySQLBuffer;

	public CommandDecoder() {
		this.inputMySQLBuffer = new ByteBufAdapter();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inputBuffer, List<Object> out) throws Exception {
		inputMySQLBuffer.setBuffer(inputBuffer);
		
		if (packetHeadLength <= inputBuffer.readableBytes()) {
			byte[] headHeadPacket = new byte[packetHeadLength];
			inputBuffer.getBytes(inputBuffer.readerIndex(), headHeadPacket);

			int packetBodyLength = MySQLByteUtils.getPacketLength(headHeadPacket);
			byte sequenceId = headHeadPacket[3];

			if ((packetBodyLength + packetHeadLength) <= inputBuffer.readableBytes()) {
				// 消息足够长，可以解码。读取，但不需要。
				inputBuffer.skipBytes(packetHeadLength);
				
				MySQLResultPacket resultPacketList = AttributeMap.ioSession(ctx).commandResultPacket();
				// 读取一个，不消费掉数据。
				byte responseType = inputBuffer.getByte(inputBuffer.readerIndex());
				if (responseType == 0) {
					OKayPacket okayPacket = new OKayPacket();
					okayPacket.setPacketBodyLength(packetBodyLength);
					okayPacket.setSequenceId(sequenceId);
					okayPacket.read(inputMySQLBuffer, true);
					out.add(okayPacket);
					return ;
				} else if (responseType == (byte)0xff) {
					// 有问题也要把缓冲区消费低
					ErrorPacket errorPacket = new ErrorPacket();
					errorPacket.setPacketBodyLength(packetBodyLength);
					errorPacket.setSequenceId(sequenceId);
					errorPacket.read(inputMySQLBuffer, true);
					out.add(errorPacket);
					return ;
				} else {
					// 其他数据
					if (resultPacketList.resultSetHeaderPacket() == null) {
						// 掉过一个字节。
						inputBuffer.skipBytes(1);

						ResultSetHeaderPacket resultSetHeaderPacket = new ResultSetHeaderPacket();
						resultSetHeaderPacket.setPacketBodyLength(packetBodyLength);
						resultSetHeaderPacket.setSequenceId(sequenceId);

						long fieldCount = MySQLByteUtils.readLengthEncodedInteger(responseType, inputMySQLBuffer);
						resultSetHeaderPacket.setFiledCount(fieldCount);

						// 设置Result
						resultPacketList.setResultSetHeaderPacket(resultSetHeaderPacket);
						return;
					}

					if (resultPacketList.fieldPacketList().size() < resultPacketList.resultSetHeaderPacket().getFiledCount()) {
						FieldPacket fieldPacket = new FieldPacket();
						fieldPacket.read(inputMySQLBuffer);
						resultPacketList.fieldPacketList().add(fieldPacket);
//						System.out.println("读了N个列：" + resultPacketList.fieldPacketList().size());
						return;
					}

					// EOF
					if (responseType == (byte) 254) {
						byte[] bs = new byte[packetBodyLength];
						inputBuffer.readBytes(bs);
//						System.out.println("读完了！");
						// 所有数据都读完，重新开始消费数据。
						ResultSet resultSet = new AsyncResultSet(resultPacketList);
						out.add(resultSet);
						return ;
					}

					// Row
					RowPacket rowPacket = new RowPacket((int) (resultPacketList.resultSetHeaderPacket().getFiledCount()));
					rowPacket.read(inputMySQLBuffer);
					resultPacketList.rowPacketList().add(rowPacket);
//					System.out.println("读取到的行：" + resultPacketList.rowPacketList().size());
					return;
				}
			}
		}
	}

}
