package com.jijdy.lrpcjava.service.discovery;

import com.jijdy.lrpcjava.client.NettyClient;
import com.jijdy.lrpcjava.loadbalance.LoadBalance;
import com.jijdy.lrpcjava.loadbalance.impl.RandomLoadBalance;
import com.jijdy.lrpcjava.utils.CuratorUtil;
import io.netty.channel.Channel;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* discovery service for client
 * @Author jijdy
 * @Date 2021/12/20 15:45
 */
public class ServiceDiscovery {
    private static final Logger log = LoggerFactory.getLogger(ServiceDiscovery.class);

    private final CuratorFramework curator;

    private final LoadBalance loadBalance;

    public ServiceDiscovery() {
        this.curator = CuratorUtil.getCuratorFramework();
        this.loadBalance = new RandomLoadBalance();
    }

    /* 通过服务名称得到对应的channel连接，若本地缓存中没有连接，则会创建连接 */
    public String getAddress(String serviceName) throws Exception {
        byte[] serviceData = CuratorUtil.getServiceData(curator, serviceName);
        log.info("get service[{}] data: [{}]",serviceName,serviceData);
        /* todo 若无注册中心，则按照本地缓存的地址来进行映射 */
        return loadBalance.findService(serviceData);
    }
}
