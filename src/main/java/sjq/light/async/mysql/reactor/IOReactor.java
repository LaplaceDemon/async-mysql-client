package sjq.light.async.mysql.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import sjq.light.async.mysql.Config;
import sjq.light.async.mysql.Status;
import sjq.light.async.mysql.execute.ExecuteTask;
import sjq.light.async.mysql.resultset.AsyncResultSet;
import sjq.light.mysql.protocol.buffer.MySQLMessage;
import sjq.light.mysql.protocol.commons.CapabilityFlag;
import sjq.light.mysql.protocol.packet.auth.AuthMoreDataPacket;
import sjq.light.mysql.protocol.packet.auth.AuthPacket;
import sjq.light.mysql.protocol.packet.auth.AuthSwitchResponsePacket;
import sjq.light.mysql.protocol.packet.auth.HandShakeV10Packet;
import sjq.light.mysql.protocol.packet.command.CommandQueryPacket;
import sjq.light.mysql.protocol.packet.response.resultset.FieldPacket;
import sjq.light.mysql.protocol.packet.response.resultset.ResultSetHeaderPacket;
import sjq.light.mysql.protocol.packet.response.resultset.RowPacket;
import sjq.light.mysql.protocol.util.MySQLByteUtils;

public class IOReactor {
	private Selector selector;
	private Config config;
	private BlockingQueue<ExecuteTask> sqlExecuteQueue;

	public IOReactor(Config config, BlockingQueue<ExecuteTask> sqlExecuteQueue) throws IOException {
		this.selector = Selector.open();
		this.config = config;
		this.sqlExecuteQueue = sqlExecuteQueue;
	}

	public IOSession register(SocketChannel socektChannel) throws IOException {
		socektChannel.configureBlocking(false);
		SelectionKey key = socektChannel.register(selector, SelectionKey.OP_WRITE);
		IOSession eventData = new IOSession(key);
		key.attach(eventData);
		return eventData;
	}
	
	public void consoumerCommand(IOSession eventData) {
		SelectionKey selectionKey = eventData.getSelectionKey();
		if((selectionKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
			selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
		
		ExecuteTask executeTask = this.sqlExecuteQueue.poll();
		if(executeTask == null) {
			return ;
		}
		
		eventData.setStatus(Status.Commanding);
		CommandQueryPacket command = new CommandQueryPacket();
		eventData.setExecuteCallback(executeTask.getExecuteCallback());
		eventData.setResultPacketList(new MySQLResultPacket());
		command.setSql(executeTask.getSql());
		command.setSequenceId((byte)0);
		command.autoSetLength();
		
		MySQLMessage mySQLMessage = new ByteBufferMySQLMessage(command.getLength() + 4);
		command.write(mySQLMessage, eventData.outputMySQLBuffer());
	}

	public void run() throws IOException {
		while (true) {
			int num = selector.select(1000);
			
			if (num == 0) {
				System.out.println("selector is running...");
				continue;
			}
			
			Set<SelectionKey> keySet = selector.selectedKeys();
			for (SelectionKey key : keySet) {
                if(key.isConnectable()) {
                    System.out.println("client can been connected");
//                    SocketChannel sc =  (SocketChannel) key.channel();  
//                    if (sc.isConnectionPending()) {
//                        sc.finishConnect();  
//                        System.out.println("完成连接!");  
//                        ByteBuffer buffer = ByteBuffer.allocate(1024);  
//                        buffer.put("Hello,Server".getBytes());  
//                        buffer.flip();
//                        sc.write(buffer);
//                    }
//                    sc.register(selector, SelectionKey.OP_READ);   
                } else if (key.isWritable()) {
					SocketChannel socketChannel = (SocketChannel) key.channel();
					IOSession eventData = (IOSession) key.attachment();
					
					this.consoumerCommand(eventData);
					
					while (true) {
						ByteBuffer buffer;
						try {
							buffer = eventData.getLastWriteableBuffer();
						} catch (NoSuchElementException e) {
							// 表示缓冲区没有数据了。
							// 只关注可读事件。不关注可写，以免造成CPU Busy
							key.interestOps(SelectionKey.OP_READ);
							break;
						}
						
						// 获取Buffer可写的长度。
						int writableBytesLength = buffer.position();
						buffer.flip();
						
						int writeBytesNum = socketChannel.write(buffer);
						if (writeBytesNum < 0) {
							// io不能再写。退出等待可写。
							key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
							break;
						} else {
							System.out.println("写出数据：" + writeBytesNum);
							// 检查是否全部写完
							if(writeBytesNum == writableBytesLength) {
								eventData.removeLastWriteableBuffer();
							}
						}
					}
				} else if (key.isReadable()) {
					SocketChannel socketChannel = (SocketChannel) key.channel();
					IOSession eventData = (IOSession) key.attachment();
					int readCount = 0;
					long readBytesNum = 0;
					while (true) {
						ByteBuffer buffer = ByteBuffer.allocate(128);
						int count = -1;
						try {
							count = socketChannel.read(buffer);
						} catch (IOException e) {
							e.printStackTrace();
						}
						readCount++;

						if (count > 0) {
							// 把读到的数据放入缓冲区。
							eventData.pushInputBuffer(buffer);
							readBytesNum += count;
							continue;
						} else if (count == 0) {
							// 在socketChannel中read返回0。表示读完了。
							System.out.println("从SocketChannel读取到0");
							// 无数据可读。
							if (readCount == 1) {
								// 第一次读就不能读数据，触发了JAVA NIO Bug
								System.out.println("第一次就不能读！");
							}

							break;
						} else {
							// -1，表示到达流末尾，即没有数据可以再被读。
							eventData.close();
							socketChannel.close();
							key.cancel();
							// 连接已关闭
							System.out.println("从SocketChannel读取到-1,连接已关闭");
							break;
						}
					}
					
					System.out.println("读到数据字节数：" + readBytesNum);

					if (!socketChannel.isOpen()) {
						break;
					}

					while (true) {
						// 数据解码。
						System.out.println("数据解码");
						int headPacketLength = 4;
						if (headPacketLength <= eventData.inputBuffer().readableLength()) {
//							byte[] headPacket = eventData.inputBuffer().readNBytes(headPacketLength);
//							int packetLength = MySQLByteUtils.getPacketLength(headPacket);
							byte[] headPacket = eventData.inputBuffer().getNBytes(headPacketLength);
							int packetLength = MySQLByteUtils.getPacketLength(headPacket);
							byte sequenceId = headPacket[3];
							
							if ((packetLength + headPacketLength) <= eventData.inputBuffer().readableLength()) {
								// 消息足够长，可以解码。
								// 读取，但不需要。
								eventData.inputBuffer().readNBytes(headPacketLength);
								
								if (eventData.getStatus() == Status.HandShakeing) {
									HandShakeV10Packet handShakeV10Packet = new HandShakeV10Packet(packetLength, sequenceId);
									handShakeV10Packet.read(eventData.inputMySQLBuffer());

									// 读取握手数据完毕。发送校验数据。
									String database = this.config.getDatabase();
									String username = this.config.getUsername();
									String password = this.config.getPassword();

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
//                                    				CapabilityFlag.Upper.CLIENT_CONNECT_ATTRS |
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
									authPack.setSequenceId((byte) (sequenceId + 1));
									authPack.autoSetLength();
									
									// 设置服务器信息。
									eventData.serverInfo().setSeed(seedBytes);
									
									String serverVersion = handShakeV10Packet.getServerVersion();
									String[] splitServerVersion = serverVersion.split(".");
									
									byte[] bytesServerVersion = new byte[splitServerVersion.length];
									for(int i = 0;i<splitServerVersion.length; i++) {
										String strItem = splitServerVersion[i];
										bytesServerVersion[i] = Byte.valueOf(strItem).byteValue();
									}
									eventData.serverInfo().setServerVersion(bytesServerVersion);

									// write out
									MySQLMessage message = new ByteBufferMySQLMessage(authPack.getLength() + headPacketLength);
									authPack.write(message, eventData.outputMySQLBuffer());
									eventData.setStatus(Status.Authing);

									key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
								} else if (eventData.getStatus() == Status.Authing) {
									if (packetLength == 2) {
										byte statusByte = eventData.inputBuffer().readByte();
										AuthMoreDataPacket authMoreDataPacket = new AuthMoreDataPacket();
										authMoreDataPacket.setStatus(statusByte);
										
										byte[] bs = eventData.inputMySQLBuffer().readNBytes(packetLength - 1);
										authMoreDataPacket.setAuthMethodData(new String(bs));
										
										if(Arrays.equals(bs, new byte[] {(byte)4})) {
											System.out.println("第二阶段验证，4");
											// 如果是4
											// 返回2
											AuthMoreDataPacket responseAuthMoreDataPacket = new AuthMoreDataPacket();
											responseAuthMoreDataPacket.setSequenceId((byte)(sequenceId + 1));
											responseAuthMoreDataPacket.setStatus((byte)2);
											responseAuthMoreDataPacket.autoSetLength();
											System.out.println("将要写出数据：" + (responseAuthMoreDataPacket.getLength() + headPacketLength));
											MySQLMessage message = new ByteBufferMySQLMessage(responseAuthMoreDataPacket.getLength() + headPacketLength);
											responseAuthMoreDataPacket.write(message, eventData.outputMySQLBuffer());
											
											// 不再处理当前连接的io，但继续需要写数据。
											key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
											eventData.setStatus(Status.MoreAuthing);
											break;
										} else if(Arrays.equals(bs, new byte[] {(byte)3})) {
											System.out.println("第二阶段验证，3");
											// 如果是3
											// 继续读包数据
											continue;
										}
									}

									int responseType = eventData.inputBuffer().read();
									if (responseType == 0) {
										// 握手成功！
										System.out.println("握手成功");
										eventData.setStatus(Status.HandShakeSuccess);
										this.consoumerCommand(eventData);
									} else if (responseType == 0xff) {
										System.out.println("有问题啊，握手没成功");
									}
									eventData.inputBuffer().readNBytes(packetLength - 1);
								} else if (eventData.getStatus() == Status.MoreAuthing) {
									AuthSwitchResponsePacket authSwitchResponsePacket0 = new AuthSwitchResponsePacket();
									authSwitchResponsePacket0.setLength(packetLength);
									authSwitchResponsePacket0.setSequenceId(sequenceId);
									authSwitchResponsePacket0.read(eventData.inputMySQLBuffer());
									String publicKeyString = authSwitchResponsePacket0.getAuthPluginResponse();
									// 读到数据
									// 返回响应
									byte[] seed = eventData.serverInfo().getSeed();
									
									byte[] serverVersion = eventData.serverInfo().getServerVersion();
									
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
									authSwitchResponsePacket1.setSequenceId((byte)(sequenceId+1));
									authSwitchResponsePacket1.setAuthPluginResponse(new String(encryptPassword,Charset.forName("ISO8859_1")));
									authSwitchResponsePacket1.autoSetLength();
									
									MySQLMessage message = new ByteBufferMySQLMessage(authSwitchResponsePacket1.getLength() + headPacketLength);
									authSwitchResponsePacket1.write(message, eventData.outputMySQLBuffer());
									
									key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
									eventData.setStatus(Status.Authing);
									break;
								} else if (eventData.getStatus() == Status.Commanding) {
									// 读取一个，但真正的读数据。
									byte responseType = eventData.inputBuffer().getByte();
									if (responseType == 0) {
										System.out.println("OK");
									} else if (responseType == 0xff) {
										System.out.println("有问题啊，握手没成功");
									} else {
										// 其他数据
										System.out.println("有数据返回");
										if(eventData.resultPacketList().resultSetHeaderPacket() == null) {
											// 掉过一个字节。
											eventData.inputBuffer().read();
											
											ResultSetHeaderPacket resultSetHeaderPacket = new ResultSetHeaderPacket();
											resultSetHeaderPacket.setLength(packetLength);
											resultSetHeaderPacket.setSequenceId(sequenceId);

											long fieldCount = MySQLByteUtils.readLengthEncodedInteger(responseType, eventData.inputMySQLBuffer());
											resultSetHeaderPacket.setFiledCount(fieldCount);
											
											// 设置Result
											eventData.resultPacketList().setResultSetHeaderPacket(resultSetHeaderPacket);
											continue;
										}
										
										if(eventData.resultPacketList().fieldPacketList().size() < eventData.resultPacketList().resultSetHeaderPacket().getFiledCount()) {
											FieldPacket fieldPacket = new FieldPacket();
											fieldPacket.read(eventData.inputMySQLBuffer());
											eventData.resultPacketList().fieldPacketList().add(fieldPacket);
											System.out.println("读了N个列："+eventData.resultPacketList().fieldPacketList().size());
											continue;
										}
										
										//  EOF
										if (responseType == (byte)254) {
											eventData.inputBuffer().readNBytes(packetLength);
											System.out.println("读完了！");
											// 所 有数据都读完，重新开始消费数据。
											
											ResultSet resultSet = new AsyncResultSet(eventData.resultPacketList());
											eventData.getExecuteCallback().callback(resultSet);
											
											this.consoumerCommand(eventData);
											break;
										}
										
										// Row
										RowPacket rowPacket = new RowPacket((int)(eventData.resultPacketList().resultSetHeaderPacket().getFiledCount()));
										rowPacket.read(eventData.inputMySQLBuffer());
										eventData.resultPacketList().rowPacketList().add(rowPacket);
										System.out.println("读取到的行：" + eventData.resultPacketList().rowPacketList().size());
										continue;
									}
								}
							} else {
								break;
							}
						} else {
							break;
						}
					}
				}

			}
			keySet.clear();
		}

	}
}
