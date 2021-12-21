package com.jijdy.lrpcjava.client;

import com.jijdy.lrpcjava.codec.RPCRequest;
import com.jijdy.lrpcjava.codec.RPCResponse;
import com.jijdy.lrpcjava.service.discovery.ServiceDiscovery;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/* admin future and receive response
 * @Author jijdy
 * @Date 2021/12/20 18:15
 */
public class FutureHandler {

    private static final Logger log = LoggerFactory.getLogger(FutureHandler.class);

    private static volatile FutureHandler futureHandler;

    private final ServiceDiscovery discovery;

    private final NettyClient client;

    private static final Map<String, CompletableFuture<RPCResponse>> futureMap = new ConcurrentHashMap<>();

    public FutureHandler() {
        this.discovery = new ServiceDiscovery();
        this.client = new NettyClient();
    }

    public CompletableFuture<RPCResponse> putFuture(String id, CompletableFuture<RPCResponse> future) {
        return futureMap.put(id, future);
    }


    public CompletableFuture<RPCResponse> getFuture(String id) {
        return futureMap.get(id);
    }

    /* 单例获取 */
    public static FutureHandler getFutureHandler() {
        if (null == futureHandler) {
            synchronized (FutureHandler.class) {
                if (null == futureHandler) {
                    futureHandler = new FutureHandler();
                }
            }
        }
        return futureHandler;
    }

    /* 完成远程调用的主要逻辑，并返回响应的数据 */
    public Object remoteInvoke(RPCRequest request) throws Exception {
        String serviceName = request.getInterfaceName() + request.getVersion();


        String address ;

        /* 若有指定ip地址，则直接通过该ip来进行连接，否者在注册中心进行查找 */
        if (null != request.getAddr()) {
            address = request.getAddr();
        } else {
            /* 从注册中心通过负载均衡算法得到ip地址，并通过client来获得连接 */
            address= discovery.getAddress(serviceName);
        }

        Channel channel = client.getChannel(address);

        /* 添加future，得到响应时的数据 */
        CompletableFuture<RPCResponse> future = new CompletableFuture<>();
        putFuture(request.getRequestId(), future);

        channel.writeAndFlush(request).sync().addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                /* 日志打印，发送消息成功 */
                log.info("client -> server: send request successful, [ {} ]",request);
            } else {
                /* 关闭channel通道? */
                log.info("client send message failed ",channelFuture.cause());
            }
        });
        /* 此时future已经得到了数据，并将其置为了完成状态 */
        RPCResponse rpcResponse = future.get();
        return rpcResponse.getMessage();
    }

    public void complete(RPCResponse response) {
        String requestId = response.getRequestId();
        CompletableFuture<RPCResponse> future = futureMap.remove(requestId);
        if (future.complete(response)) {
            log.info("receive server response:[{}]",response);
        }
    }

}
