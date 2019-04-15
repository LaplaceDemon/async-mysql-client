package io.github.laplacedemon.asyncmysql.network;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.github.laplacedemon.asyncmysql.Config;
import io.github.laplacedemon.asyncmysql.network.handler.HandshakeDecoder;
import io.github.laplacedemon.asyncmysql.network.handler.HandshakeHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;

public class IOReactor {
	final EventLoopGroup group = new NioEventLoopGroup();
	final Bootstrap bootstrap = new Bootstrap();
	
	public IOReactor() {
		this.bootstrap
			.group(group)
			.channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
            .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
            	
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new HandshakeDecoder()); //编码request
                    pipeline.addLast(new HandshakeHandler()); //客户端处理类
                }
            });
	}

	public void connect(final Config config, final Consumer<Channel> okConsumer) {
        this.connect(config, okConsumer, config.getConnectThrowableConsumer());
	}
	
	public void connect(final Config config, final Consumer<Channel> okConsumer, final Consumer<Throwable> failConsumer) {
		Objects.requireNonNull(okConsumer);
		Objects.requireNonNull(failConsumer);
		
        final ChannelFuture future = this.bootstrap.connect(config.getServerAddress(), config.getPort());
        future.addListener(new ChannelFutureListener() {
 
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (future.isSuccess()) {
                    Channel channel = future.channel();
                    Attribute<IOSession> attr = channel.attr(AttributeMap.IOSESSION_KEY);
                    attr.setIfAbsent(new IOSession(channel, config));
                    okConsumer.accept(channel);
                } else {
                    Throwable cause = future.cause();
                    failConsumer.accept(cause);
                }
            }
        });
	}
	
	public void run() {
		while(true) {
			try {
				this.bootstrap.config().group().awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void execute(Runnable task) {
		this.bootstrap.config().group().submit(task);
	}
	
	public void close() {
		this.group.shutdownGracefully();  //关闭线程组
	}
}
