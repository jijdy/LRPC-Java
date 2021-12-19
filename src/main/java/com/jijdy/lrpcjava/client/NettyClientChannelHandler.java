package com.jijdy.lrpcjava.client;

import com.jijdy.lrpcjava.codec.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientChannelHandler extends SimpleChannelInboundHandler<RPCResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCResponse rpcResponse) throws Exception {
        System.out.println("客户端中channelRead0的得到的响应数据："+rpcResponse.toString());
    }

}
