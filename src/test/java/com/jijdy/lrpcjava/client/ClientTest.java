package com.jijdy.lrpcjava.client;

import com.jijdy.lrpcjava.bean.UserInfo;
import com.jijdy.lrpcjava.bean.UserInfoImp;
import com.jijdy.lrpcjava.client.proxy.ClientProxy;
import com.jijdy.lrpcjava.server.Server;
import com.jijdy.lrpcjava.server.netty.NettyServer;
import com.jijdy.lrpcjava.service.registry.ServiceRegistry;
import org.junit.jupiter.api.Test;

public class ClientTest {

    @Test
    public void proxyTest() throws Exception {
        UserInfo proxy = (UserInfo) new ClientProxy().getProxy(UserInfo.class);

        new Thread(() -> {
            Server server = new NettyServer();
            server.start();
        }).start();
        Thread.sleep(1000);

        /* 先将服务注册到注册中心，和服务器中的具体对象映射中 */
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        serviceRegistry.registerService(UserInfo.class.getName());
        serviceRegistry.registerServiceImpl(UserInfo.class.getName(),new UserInfoImp());
        /* 由本地客户端，进行代理调用，通过ZK来查找服务 */
        System.out.println(proxy.getInfo());
    }
}
