package com.jijdy.lrpcjava.server.netty;

import com.jijdy.lrpcjava.codec.RPCRequest;
import com.jijdy.lrpcjava.codec.RPCResponse;
import com.jijdy.lrpcjava.service.provider.ServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import net.sf.cglib.reflect.FastClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* ChannelHandler,
   read channel request and invoke local function, then response to client
 * @Author jijdy
 * @Date 2021/12/19 15:02
 */
public class NettyServerChannelHandler extends SimpleChannelInboundHandler<RPCRequest> {

    private static final Logger log =  LoggerFactory.getLogger(NettyServerChannelHandler.class);

    /* 懒加载的获取到单例模式的服务提供者 */
    private final ServiceProvider serviceProvider = ServiceProvider.getSingleton();

    /* 读取到了请求体对象，对其进行解析，并进行本地代理调用和响应的返回 */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCRequest rpcRequest) throws Exception {

        if (rpcRequest.getRequestId().equals("heart-beat:ping")) {
            log.info("receive client ping! addr:[{}]",channelHandlerContext.channel().remoteAddress());
            return;
        }
        RPCResponse response = new RPCResponse(rpcRequest.getRequestId());
        Throwable throwable = null;
        Object invoke = null;
        try {
            invoke = invoke(rpcRequest);
            response.setMessage(invoke);
        } catch (Throwable e) {
            throwable = e;
        }
        if (null != throwable) {
            response.setError(throwable.getMessage());
        }
        /* 发送响应到客户端，并添加future监听器用于做回调 */
        Object finalInvoke = invoke;
        channelHandlerContext.channel().writeAndFlush(response).addListener(
                (ChannelFutureListener) channelFuture -> {
                    log.info("服务响应发送成功! 调用方地址：{}",channelHandlerContext.channel().remoteAddress());
                    log.info("本地服务返回数据为：{}", finalInvoke);
                }
        );

        // 在调用了该方法之后，有调用ReferenceCountUtil.release(msg);完成对ByteBuf的空间释放
    }

    /* 根据请求来完成本地服务的调用 */
    private Object invoke(RPCRequest request) throws Throwable {
        String serviceName = request.getInterfaceName() + request.getVersion();
        Object service = serviceProvider.getService(serviceName);
        Class<?>[] parameterTypes = request.getParameterTypes();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();

        try {
            /* JDK代理 */
            Method method =service.getClass().getMethod(methodName,parameterTypes);
            return method.invoke(service,parameters);
//
//            /* cglib的代理调用 */
//            FastClass fc = FastClass.create(service.getClass());
//            int index = fc.getIndex(methodName, parameterTypes);
//
//            return fc.invoke(index, service, parameters);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* 对于服务端，若客户端不再发送请求(心跳请求也可)，则会关闭连接，防止资源的过度浪费 */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("client {} connect timeout, close connection!",ctx.channel().remoteAddress());
            /* 超时连接，客户端网络波动过大，或者已经下线，断开连接，减少资源占有量 */
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server receive a exception, close the connection: address[{}]",ctx.channel().remoteAddress());
        ctx.close();
        cause.printStackTrace();
    }


}
