package com.jijdy.lrpcjava.client;

import com.jijdy.lrpcjava.codec.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.CompleteFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* client channelHandler
 * @Author jijdy
 * @Date 2021/12/19 22:27
 */
public class NettyClientChannelHandler extends SimpleChannelInboundHandler<RPCResponse> {
    private static final Logger log = LoggerFactory.getLogger(NettyClientChannelHandler.class);

    private final FutureHandler futureHandler;

    public NettyClientChannelHandler() {
        futureHandler = FutureHandler.getFutureHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCResponse rpcResponse) throws Exception {
        /* TODO 设置心跳机制的响应过滤 */
//        System.out.println("客户端中channelRead0的得到的响应数据："+rpcResponse.toString());
        log.info("read channel from server :[{}]",rpcResponse);
        futureHandler.complete(rpcResponse);
    }

}
