package com.jijdy.lrpcjava.server;

import com.jijdy.lrpcjava.bean.UserInfoImp;
import com.jijdy.lrpcjava.client.NettyClient;
import com.jijdy.lrpcjava.codec.RPCRequest;
import com.jijdy.lrpcjava.server.netty.NettyServer;
import com.jijdy.lrpcjava.service.provider.ServiceProvider;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

/* server run test
 * @Author jijdy
 * @Date 2021/12/20 12:50
 */
public class ServerTest {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServiceProvider singleton = ServiceProvider.getSingleton();
                UserInfoImp user = new UserInfoImp();
                String interfaceNmae = user.getClass().getInterfaces()[0].getName();
                System.out.println(interfaceNmae);
                singleton.addServiceMap(interfaceNmae,user);
                Server server = new NettyServer();
                server.start();
                System.out.println("server start!");
            }
        }).start();


        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println("client running");
                InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.27.1",13578);
                Channel channel = new NettyClient().connection(inetSocketAddress);
                RPCRequest request = RPCRequest.builder().interfaceName("com.jijdy.lrpcjava.bean.UserInfo")
                        .methodName("getInfo")
                        .parameterTypes(null)
                        .parameters(null)
                        .build();
                System.out.println("get channel:"+channel);
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        System.out.println("客户端发送请求成功!");
                    }
                });
            }
        }).start();
    }
}
