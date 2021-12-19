package com.jijdy.lrpcjava.client;

import com.jijdy.lrpcjava.codec.RPCDecoder;
import com.jijdy.lrpcjava.codec.RPCEncoder;
import com.jijdy.lrpcjava.codec.RPCResponse;
import com.jijdy.lrpcjava.serialize.Serializer;
import com.jijdy.lrpcjava.serialize.protostuff.ProtostuffSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/* client based on Netty
 * @Author jijdy
 * @Date 2021/12/19 18:48
 */
public class NettyClient {
    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private final Serializer serializer = new ProtostuffSerializer();

    public Channel connection(InetSocketAddress address) throws InterruptedException {
        String hostAddress = address.getAddress().getHostAddress();

        EventLoopGroup worker = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(worker).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();

                        pipeline.addLast(new IdleStateHandler(0,0,60, TimeUnit.SECONDS))
                                .addLast(new RPCDecoder(serializer, RPCResponse.class))
                                .addLast(new RPCEncoder(serializer))
                                .addLast(new NettyClientChannelHandler());
                    }
                });
        ChannelFuture future = b.connect(address).sync();
        log.info("client create connection to [{}:{}]",hostAddress,address.getPort());
        return future.channel();
    }
}
