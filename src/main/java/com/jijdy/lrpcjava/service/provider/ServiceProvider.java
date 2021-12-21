package com.jijdy.lrpcjava.service.provider;

import com.jijdy.lrpcjava.codec.RPCRequest;
import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* get service object based on interfaceName and version,
 * @Author jijdy
 * @Date 2021/12/19 15:57
 */
public class ServiceProvider {

    private static volatile ServiceProvider singleton;

    private final Map<String,Object> serviceMap;

    ServiceProvider(){
        this.serviceMap = new ConcurrentHashMap<>();
    }

    /* 使用双锁机制来实现一个单例模式 */
    public static ServiceProvider getSingleton() {
        if (singleton == null) {
            synchronized (ServiceProvider.class) {
                if (singleton == null) {
                    singleton = new ServiceProvider();
                }
            }
        }
        return singleton;
    }

    /* 将从被注册到注册中心的服务，进行本地映射存储 */
    public void addServiceMap(String serviceName, Object bean) {
            serviceMap.put(serviceName,bean);
    }

    /* 得到缓存在本地的服务对象 */
    public Object getService(String serviceName) {
        if (serviceMap.containsKey(serviceName)) {
            return serviceMap.get(serviceName);
        } else {
            throw new RPCException(RPCErrorEnum.SERVICE_NOT_FOUND);
        }
    }

    public Set<String> getServiceNameSet() {
        return this.serviceMap.keySet();
    }

    public boolean emptyService() {
        return serviceMap.isEmpty();
    }

}
