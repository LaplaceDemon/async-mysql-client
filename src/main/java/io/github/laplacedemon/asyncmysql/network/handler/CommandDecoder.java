package io.github.laplacedemon.asyncmysql.network.handler;

import java.sql.ResultSet;
import java.util.List;

import io.github.laplacedemon.asyncmysql.network.AttributeMap;
import io.github.laplacedemon.asyncmysql.network.ByteBufAdapter;
import io.github.laplacedemon.asyncmysql.resultset.AsyncResultSet;
import io.github.laplacedemon.asyncmysql.resultset.MySQLResultPacket;
import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
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
//	private MySQLResultPacket resultPacketList;

	public CommandDecoder() {
		this.inputMySQLBuffer = new ByteBufAdapter();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inputBuffer, List<Object> out) throws Exception {
		inputMySQLBuffer.setBuffer(inputBuffer);
		
		if (packetHeadLength <= inputBuffer.readableBytes()) {
			byte[] headPacket = new byte[packetHeadLength];
			inputBuffer.getBytes(inputBuffer.readerIndex(), headPacket);

			int packetLength = MySQLByteUtils.getPacketLength(headPacket);
			byte sequenceId = headPacket[3];

			if ((packetLength + packetHeadLength) <= inputBuffer.readableBytes()) {
				// 消息足够长，可以解码。读取，但不需要。
				inputBuffer.skipBytes(packetHeadLength);
				
				MySQLResultPacket resultPacketList = AttributeMap.ioSession(ctx).commandResultPacket();
				// 读取一个，不消费掉数据。
				byte responseType = inputBuffer.getByte(inputBuffer.readerIndex());
				if (responseType == 0) {
					System.out.println("OK");
				} else if (responseType == 0xff) {
					System.out.println("有问题");
				} else {
					// 其他数据
					if (resultPacketList.resultSetHeaderPacket() == null) {
						// 掉过一个字节。
						inputBuffer.skipBytes(1);

						ResultSetHeaderPacket resultSetHeaderPacket = new ResultSetHeaderPacket();
						resultSetHeaderPacket.setLength(packetLength);
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
						byte[] bs = new byte[packetLength];
						inputBuffer.readBytes(bs);
//						System.out.println("读完了！");
						// 所有数据都读完，重新开始消费数据。

						ResultSet resultSet = new AsyncResultSet(resultPacketList);
						out.add(resultSet);

						return;
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
