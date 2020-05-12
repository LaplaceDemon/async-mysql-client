package io.github.laplacedemon.mysql.protocol.packet.auth;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

/**
 * https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::AuthSwitchRequest
 * 
 * @author jackie.sjq
 *
 */
public class AuthSwitchRequestPacket extends MySQLPacket {
	private byte stauts;
	private String pluginName;
	private String authPluginData;
	
    public AuthSwitchRequestPacket() {
		super();
	}

	public AuthSwitchRequestPacket(int length, byte sequenceId) {
		super(length, sequenceId);
	}

	public byte getStauts() {
		return stauts;
	}

	public void setStauts(byte stauts) {
		this.stauts = stauts;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getAuthPluginData() {
		return authPluginData;
	}

	public void setAuthPluginData(String authPluginData) {
		this.authPluginData = authPluginData;
	}

	@Override
	public void read(InputMySQLBuffer buffer) throws IOException {
		this.stauts = buffer.readByte();
		byte[] bs = buffer.readNBytes(1);
		this.pluginName = new String(bs);
	}

}
