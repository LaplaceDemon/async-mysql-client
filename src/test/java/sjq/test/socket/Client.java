package sjq.test.socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client {

	private static final String IP = "192.168.43.214";
	private static final int PORT = 3306;

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = null;
		try {
			socket = new Socket(IP, PORT);
			InputStream inputStream = socket.getInputStream();
			BufferedInputStream ins = new BufferedInputStream(inputStream);

			byte[] readAllBytes = ins.readAllBytes();
			
			System.out.println(readAllBytes.length  + "," + Arrays.toString(readAllBytes) );
		} finally {
			socket.close();
		}

	}
}
