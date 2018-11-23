package sjq.light.async.mysql;

import java.net.Socket;
import java.nio.channels.SocketChannel;

public class Connection {
	private Socket socket;
	private SocketChannel socketChannel;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
//	public void handshake(Socket socket) {
//		this.channel = socket.getChannel();
//		this.channel.configureBlocking(false);
//		
//		try {
//			InputStream inputStream = socket.getInputStream();
//			inputStream.read();
//			HandShakeV10Packet handShakeV10Packet = new HandShakeV10Packet();
//			handShakeV10Packet.read(buffer);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	
	
}
