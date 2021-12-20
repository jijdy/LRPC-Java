package com.jijdy.lrpcjava.service.registry;

import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;
import com.jijdy.lrpcjava.integration.ConfigService;
import com.jijdy.lrpcjava.service.provider.ServiceProvider;
import com.jijdy.lrpcjava.utils.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/* register service to zookeeper by curator
 * @Author jijdy
 * @Date 2021/12/20 13:53
 */
public class ServiceRegistry {
    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

    private final CuratorFramework curator;

    private final ServiceProvider provider;

    public ServiceRegistry() {
        curator = CuratorUtil.getCuratorFramework();
        provider = ServiceProvider.getSingleton();
    }

    /* 根据服务名称(接口名称+版本号)来定义注册中心的路径， */
    public void registerService(String serviceName) throws Exception{
        if (null == serviceName) {
            throw new RPCException(RPCErrorEnum.REGISTER_FAILED);
        }

        String data = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), ConfigService.getPort()).toString();
        log.info("向zk中写入数据，serviceName：[{}],data:[{}]",serviceName,data);
        CuratorUtil.registerService(curator,serviceName,data);
    }

    /* 将确定的服务接口的实现类，存放在服务器本地的映射中 */
    public void registerServiceImpl(String serviceName, Object serviceImpl) {
        provider.addServiceMap(serviceName,serviceImpl);
        log.info("添加服务成功: [{}]",serviceName);
    }
}
