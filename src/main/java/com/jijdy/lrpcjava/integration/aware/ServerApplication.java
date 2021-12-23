package com.jijdy.lrpcjava.integration.aware;

import com.jijdy.lrpcjava.annotation.LRPCService;
import com.jijdy.lrpcjava.server.Server;
import com.jijdy.lrpcjava.server.netty.NettyServer;
import com.jijdy.lrpcjava.service.registry.ServiceRegistry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

//@Component
public class ServerApplication implements ApplicationContextAware, InitializingBean, DisposableBean {

    private final Server server = new NettyServer();

    ServiceRegistry registry = new ServiceRegistry();


    /* 会在最后结束Spring时，将其注入到jvm虚拟机钩中进行最终执行 */
    @Override
    public void destroy() throws Exception {
        /* 注销所有注册中心的数据 */
        registry.unRegisterService();
        registry.registryClose();
    }

    /* bean的初始化，在beanDefinition完成初始化之后，bean进入到ioc容器之前，进行一个初始化操作*/
    @Override
    public void afterPropertiesSet() throws Exception {
        if (registry.isStart()) {
            /* 和spring的容器进行整和，在注入容器之后，启动服务器
             * 若没有需要抛出的服务，则不会启动服务 */
            server.start();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(LRPCService.class);
        if (!beansWithAnnotation.isEmpty()) {
            for (Object value : beansWithAnnotation.values()) {
                LRPCService annotation = value.getClass().getAnnotation(LRPCService.class);
                String version = annotation.version();
                /* 得到默认实现的接口，若为多层继承的接口，则不能够保证其正常运行 */
                String interfaceName = value.getClass().getInterfaces()[0].getName();
                String serviceName = interfaceName+version;

                /* 注册服务到注册中心，同时将实现类注册到本地缓存中 */
                try {
                    registry.registerService(serviceName,value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
