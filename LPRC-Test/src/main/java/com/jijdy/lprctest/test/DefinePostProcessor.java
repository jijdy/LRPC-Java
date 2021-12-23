package com.jijdy.lprctest.test;

import com.jijdy.lrpcjava.annotation.LRPCReference;
import com.jijdy.lrpcjava.annotation.LRPCService;
import com.jijdy.lrpcjava.client.proxy.ClientProxy;
import com.jijdy.lrpcjava.integration.ConfigService;
import com.jijdy.lrpcjava.integration.postprocessor.ConfigComponent;
import com.jijdy.lrpcjava.server.Server;
import com.jijdy.lrpcjava.server.netty.NettyServer;
import com.jijdy.lrpcjava.service.registry.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;

/* 使用BeanPostProcessors接口，来实现对bean对象的全体扫描操作
 * @Author jijdy
 * @Date 2021/12/23 13:11
 */
@Configuration
@EnableConfigurationProperties(ConfigComponent.class)
public class DefinePostProcessor implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private final Server server = new NettyServer();

    ServiceRegistry registry = new ServiceRegistry();

    @Autowired
    ConfigComponent configComponent;

    /* beanDefinition之后，拿到definition中的前置和后置操作后，
    完成bean对象的初始化操作之前执行针对一次全部bean对象拦截
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        /* 根据注解，将service注册到注册中心，并将其存入到缓存中使用 */
        if (bean.getClass().isAnnotationPresent(LRPCService.class)) {
            /* 加载配置 */
            setProperties();
            LRPCService annotation = bean.getClass().getAnnotation(LRPCService.class);
            String interfaceName = bean.getClass().getInterfaces()[0].getName();
            String version = annotation.version();
            String serviceName = interfaceName+version;
            try {
                registry.registerService(serviceName,bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

    /* 在bean完成属性初始化之后，但是被外部getBean()之前，执行的后置操作 */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        /* 遍历所有与bean中的字段，并将其中的字段进行代理替换 */
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(LRPCReference.class)) {
                /* 加载配置 */
                setProperties();
                LRPCReference annotation = field.getAnnotation(LRPCReference.class);
                String version = annotation.version();
                String addr = annotation.addr();
                Object proxy = new ClientProxy(version, addr).getProxy(field.getType());
                field.setAccessible(true);
                try {
                    field.set(bean,proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }


    @Override
    public void destroy() throws Exception {

    }

    /* 容器刷新事件，会在bean全部加载完成之后调用，
    通过该接口能够在bean被加载完毕之后执行一个监听器事件 */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (registry.isStart()) {
            /* 和spring的容器进行整和，在注入容器之后，启动服务器
             * 若没有需要抛出的服务，则不会启动服务 */
            server.start();
        }

    }

    public void setProperties() {
        if (!ConfigService.isSet()) {
            ConfigService.setNamespace(configComponent.getNamespace());
            ConfigService.setMaxRetries(configComponent.getMaxRetries());
            ConfigService.setPort(configComponent.getPort());
            ConfigService.setZkSleepTimeout(configComponent.getZkSleepTimeout());
            ConfigService.setRegistryAddr(configComponent.getRegistryAddr());
        }

    }

}
