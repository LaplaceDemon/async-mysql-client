package io.github.laplacedemon.asyncmysql;

public class ServerInfo {
	private byte[] seed;
	private byte[] serverVersion;

	public byte[] getSeed() {
		return seed;
	}

	public void setSeed(byte[] seed) {
		this.seed = seed;
	}

	public byte[] getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(byte[] serverVersion) {
		this.serverVersion = serverVersion;
	}
	
}
