package io.github.laplacedemon.asyncmysql;

import java.util.function.Consumer;

public class Config {
	private String host;
	private int port;
	private String username;
	private String password;
	private String database;
	private Consumer<Throwable> connectThrowableConsumer;
	
	public Config() {
		this.connectThrowableConsumer = (Throwable t)->{
			t.printStackTrace();
		};
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Consumer<Throwable> getConnectThrowableConsumer() {
		return connectThrowableConsumer;
	}

	public void setConnectThrowableConsumer(Consumer<Throwable> connectThrowableConsumer) {
		this.connectThrowableConsumer = connectThrowableConsumer;
	}
	
}