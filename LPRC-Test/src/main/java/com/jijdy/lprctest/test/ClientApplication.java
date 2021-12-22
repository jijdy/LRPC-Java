package com.jijdy.lprctest.test;

import com.jijdy.lrpcjava.annotation.LRPCReference;
import com.jijdy.lrpcjava.client.proxy.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class ClientApplication implements ApplicationContextAware, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);

    /* 关闭curator客户端，和结束任务 */
    @Override
    public void destroy() throws Exception {
    }

    /* 根据field字段和其上的注解，将其所对应的引用字段进行注入 */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            /* 需要排除自己这个bean对象，以防止出现依赖循环的错误 */
            if (beanDefinitionName.equals("clientApplication")) {
                continue;
            }
//            log.info("====");
            Object bean = applicationContext.getBean(beanDefinitionName);
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
//                log.info("-----");
                LRPCReference annotation = field.getAnnotation(LRPCReference.class);
                if (null != annotation) {
                    Object proxy = new ClientProxy(annotation.version(), annotation.addr()).getProxy(field.getType());
                    try {
                        log.info("设置代理引用的字段对象为代理对象：{}",field.getName());
                        /* 将当前字段属性更换为代理对象 */
                        field.setAccessible(true);
                        field.set(bean,proxy);
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
    }
}
