package com.jijdy.lrpcjava.service.discovery;

import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;
import com.jijdy.lrpcjava.loadbalance.LoadBalance;
import com.jijdy.lrpcjava.loadbalance.impl.RandomLoadBalance;
import com.jijdy.lrpcjava.utils.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/* discovery service for client
 * @Author jijdy
 * @Date 2021/12/20 15:45
 */
public class ServiceDiscovery {
    private static final Logger log = LoggerFactory.getLogger(ServiceDiscovery.class);

    private final CuratorFramework curator;

    private final LoadBalance loadBalance;

    private final Map<String, String[]> serviceCacheMap;

    public ServiceDiscovery() {
        this.curator = CuratorUtil.getCuratorFramework();
        this.loadBalance = new RandomLoadBalance();
        serviceCacheMap = new ConcurrentHashMap<>();
    }

    /* 通过服务名称得到对应的channel连接，若本地缓存中没有连接，则会创建连接 */
    public String getAddress(String serviceName) throws Exception {
        byte[] serviceData;
        String addresses = null;
        try {
            serviceData = CuratorUtil.getServiceData(curator, serviceName);

            if (null == serviceData) throw new Exception();

            log.info("get service[{}] data: [{}]",serviceName,serviceData);
            String s = new String(serviceData);
            String[] split = s.substring(1).split("/");
//            log.info("得到的所有服务地址为：{}", Arrays.toString(split));
            addresses = loadBalance.findService(split);
            /* 存入缓存中，以便之后进行查找，但是只会缓存上一次成功的服务 */
            serviceCacheMap.put(serviceName,split);
        } catch (Exception e) {
            /* 注册中心发现服务失败，或未找到服务，则会在本地缓存中的服务中进行一次查找，
            尽最大可能的找到服务 */
            log.warn("service registry is not found : {} and cause: {}",serviceName,e.getCause());
            String[] strings = serviceCacheMap.get(serviceName);
            if (null != strings) {
                log.info("find service from cache: {}", Arrays.toString(strings));
                addresses = loadBalance.findService(strings);
            }
        }

        if (addresses == null || "".equals(addresses)) throw new RPCException(RPCErrorEnum.SERVICE_NOT_FOUND);
        return addresses;
    }
}
