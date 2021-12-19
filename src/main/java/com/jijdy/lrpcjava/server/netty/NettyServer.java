package com.jijdy.lrpcjava.server.netty;

import com.jijdy.lrpcjava.codec.RPCDecoder;
import com.jijdy.lrpcjava.codec.RPCEncoder;
import com.jijdy.lrpcjava.codec.RPCRequest;
import com.jijdy.lrpcjava.serialize.Serializer;
import com.jijdy.lrpcjava.serialize.protostuff.ProtostuffSerializer;
import com.jijdy.lrpcjava.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.spec.ECField;
import java.util.concurrent.TimeUnit;

/* server based on Netty */
public class NettyServer implements Server {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);


    /* todo 使用多线程的方式来创建服务器，来保证有多个连接能够被创建 */
    @SneakyThrows
    @Override
    public void start() {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class)
                    /* nodelay算法，用于尽可能的发送大数据块，减少网络传输次数 */
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    /* keepAlive，保持底层TCP的心跳机制 */
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    /* 用于临时存放已完成3此握手的连接请求的最大队列数， */
                    .option(ChannelOption.SO_BACKLOG, 128)
                    /* 开启Netty底层的日志输出 */
                    .handler(new LoggingHandler(LogLevel.INFO))
                    /* 当客户端第一次建立连接时进行的初始化操作,使用SocketChannel进行初始化操作 */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            /* todo 得到一个序列化器，应该从配置中心中选择不同的序列化方式 */
                            Serializer serializer = new ProtostuffSerializer();

                            /* 得到一个ChannelPipeline */
                            ChannelPipeline pipeline = channel.pipeline();

                            /* 将编解码器传入到pipeline中，用作编解码使用 */
                            pipeline.addLast(new RPCEncoder(serializer))
                                    .addLast(new RPCDecoder(serializer, RPCRequest.class));
                            /* 空闲超时触发器的空闲时间设置,一分钟没有收到消息，就会将该和客户端的连接关闭 */
                            pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
                            /* ChannelHandler，业务处理方式 */
                            pipeline.addLast(new NettyServerChannelHandler());
                        }
                    });

            String host = InetAddress.getLocalHost().getHostAddress();
            /* 从配置中获取port，默认为13578 */
            int port = 13578;
            ChannelFuture future = bootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
            log.info("server start on port: {}", port);
        } catch (Exception e) {
            log.info("server start failed!");
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void close() {

    }
}
