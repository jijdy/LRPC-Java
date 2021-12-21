package com.jijdy.lrpcjava.service.registry;

import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;
import com.jijdy.lrpcjava.integration.ConfigService;
import com.jijdy.lrpcjava.service.provider.ServiceProvider;
import com.jijdy.lrpcjava.utils.CuratorUtil;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

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
    public void registerService(String serviceName,Object serviceImpl) throws Exception{
        if (null == serviceName) {
            throw new RPCException(RPCErrorEnum.REGISTER_FAILED);
        }

        String data = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), ConfigService.getPort()).toString();
        log.info("向zk中写入数据，serviceName：[{}],data:[{}]",serviceName,data);
        CuratorUtil.registerService(curator,serviceName,data);
        /* 将注册的服务保存在服务端本地的缓存中 */
        provider.addServiceMap(serviceName,serviceImpl);
        log.info("添加服务成功: [{}]",serviceName);
    }



    public void unRegisterService() {
        Set<String> serviceNameSet = provider.getServiceNameSet();
        try {
            for (String s : serviceNameSet) {
                CuratorUtil.unRegisterService(curator,s);
            }
        } catch (Exception e) {
            log.info("service unRegister failed! {}",e.getMessage());
        }
    }

    public boolean isStart() {
        return provider.emptyService();
    }

}
