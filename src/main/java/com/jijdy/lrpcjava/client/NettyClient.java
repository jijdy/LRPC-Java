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

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/* client based on Netty，only provide channel for service
 * @Author jijdy
 * @Date 2021/12/19 18:48
 */
public class NettyClient {
    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    /* 根据IP地址来保存channel连接的map集合 */
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private final Serializer serializer = new ProtostuffSerializer();


    public Channel getChannel(String address) throws InterruptedException {
        if (channelMap.containsKey(address)) {
            return channelMap.get(address);
        }
        /* 创建连接并缓存， */
        int index = address.indexOf(":");
        String host = address.substring(0,index);
        int post = Integer.parseInt(address.substring(index+1));

        Channel connection = connection(new InetSocketAddress(host, post));
        channelMap.put(address,connection);
        return connection;
    }

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
