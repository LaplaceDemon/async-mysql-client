package io.github.laplacedemon.asyncmysql.network;

import java.sql.ResultSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.Config;
import io.github.laplacedemon.asyncmysql.Connection;
import io.github.laplacedemon.asyncmysql.ServerInfo;
import io.github.laplacedemon.asyncmysql.Status;
import io.github.laplacedemon.asyncmysql.network.handler.AuthSwitchDecoder;
import io.github.laplacedemon.asyncmysql.network.handler.AuthSwitchHandler;
import io.github.laplacedemon.asyncmysql.network.handler.CommandDecoder;
import io.github.laplacedemon.asyncmysql.network.handler.CommandHandler;
import io.github.laplacedemon.asyncmysql.network.handler.HandshakeDecoder;
import io.github.laplacedemon.asyncmysql.network.handler.HandshakeHandler;
import io.github.laplacedemon.asyncmysql.network.handler.MoreAuthDecoder;
import io.github.laplacedemon.asyncmysql.network.handler.MoreAuthHandler;
import io.github.laplacedemon.asyncmysql.resultset.MySQLResultPacket;
import io.github.laplacedemon.asyncmysql.util.BiLongLongConsumer;
import io.netty.channel.Channel;

public class IOSession {
	private Status status;
	private final Channel channel;
	private final Config config;
	private final ServerInfo serverInfo;
	
	private Consumer<Connection> handshakeSuccessCallback;
	private BiLongLongConsumer updateResultCallback;
	private Consumer<ResultSet> queryResultCallback;
	
	private MySQLResultPacket commandResultPacket;
	
	public IOSession(final Channel channel, final Config config) {
		this.channel = channel;
		this.status = Status.HandShakeing;
		this.config = config;
		this.serverInfo = new ServerInfo();
	}

	public Status getStatus() {
		return status;
	}
	
	public void gotoStatus(Status newStatus) {
		if(this.status.equals(Status.HandShakeing) && newStatus.equals(Status.AuthSwitch)) {
			channel.pipeline().remove(HandshakeDecoder.class);
			channel.pipeline().remove(HandshakeHandler.class);
			channel.pipeline().addLast(new AuthSwitchDecoder(), new AuthSwitchHandler());
		} else if(this.status.equals(Status.AuthSwitch) && newStatus.equals(Status.MoreAuthing)) {
			channel.pipeline().remove(AuthSwitchDecoder.class);
			channel.pipeline().remove(AuthSwitchHandler.class);
			channel.pipeline().addLast(new MoreAuthDecoder(), new MoreAuthHandler());
		} else if(this.status.equals(Status.AuthSwitch) && newStatus.equals(Status.Commanding)) {
			channel.pipeline().remove(AuthSwitchDecoder.class);
			channel.pipeline().remove(AuthSwitchHandler.class);
			channel.pipeline().addLast(new CommandDecoder(), new CommandHandler());
		} else if(this.status.equals(Status.MoreAuthing) && newStatus.equals(Status.Commanding)) {
			channel.pipeline().remove(MoreAuthDecoder.class);
			channel.pipeline().remove(MoreAuthHandler.class);
			channel.pipeline().addLast(new CommandDecoder(), new CommandHandler());
		}
		
		this.status = newStatus;
	}

	public ServerInfo serverInfo() {
		return serverInfo;
	}

	public void setHandshakeSuccessCallback(Consumer<Connection> co) {
		this.handshakeSuccessCallback = co;
	}

	public Consumer<Connection> getHandshakeSuccessCallback() {
		return handshakeSuccessCallback;
	}

	public Config getConfig() {
		return config;
	}

	public void setQueryResultCallback(Consumer<ResultSet> co) {
		this.queryResultCallback = co;
	}

	public Consumer<ResultSet> getQueryResultCallback() {
		return queryResultCallback;
	}

	public void setResultPacketList(MySQLResultPacket commandResultPacket) {
		this.commandResultPacket = commandResultPacket;
	}

	public MySQLResultPacket commandResultPacket() {
		return commandResultPacket;
	}
	
	public void setUpdateResultCallback(BiLongLongConsumer co) {
		this.updateResultCallback = co;
	}
	
	public BiLongLongConsumer getUpdateResultCallback() {
		return this.updateResultCallback;
	}
	
}
