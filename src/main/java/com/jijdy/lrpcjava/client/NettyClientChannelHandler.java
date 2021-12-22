package com.jijdy.lrpcjava.client;

import com.jijdy.lrpcjava.codec.RPCRequest;
import com.jijdy.lrpcjava.codec.RPCResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* client channelHandler，receive and handler response
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

    /* 用于检测心跳机制，并进行心跳连接 */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            RPCRequest request = RPCRequest.builder().requestId("heart-beat:ping").build();
            ctx.channel().writeAndFlush(request).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("send ping for server! addr:[{}}",ctx.channel().remoteAddress());
                }
            });
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("client channel happened exception , {}",cause.getMessage());
        ctx.close();
    }
}
