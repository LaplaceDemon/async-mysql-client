package io.github.laplacedemon.mysql.protocol.packet.auth;

import java.io.IOException;
import java.nio.charset.Charset;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

/**
 * https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::AuthSwitchResponse
 * @author jackie.sjq
 *
 */
public class AuthSwitchResponsePacket extends MySQLPacket {
	/**
	 * string[EOF]
	 */
	private String authPluginResponse;

	@Override
    public void read(InputMySQLBuffer buffer) throws IOException {
		byte[] readNBytes = buffer.readNBytes(packetBodyLength);
		String authPluginResponseString = new String(readNBytes);
		this.authPluginResponse = authPluginResponseString;
    }
    
    @Override
    public void write(MySQLMessage message, OutputMySQLBuffer output) {
    	super.write(message, null);
    	message.writeBytes(authPluginResponse.getBytes(Charset.forName("ISO8859_1")));
    	output.write(message);
    }
	
	public String getAuthPluginResponse() {
		return authPluginResponse;
	}

	public void setAuthPluginResponse(String authPluginResponse) {
		this.authPluginResponse = authPluginResponse;
	}

	@Override
	public void autoSetLength() {
		if(this.authPluginResponse!=null) {
			this.packetBodyLength = this.authPluginResponse.length();
		}
	}
	
}
	
	
