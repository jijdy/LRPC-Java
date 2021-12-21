package com.jijdy.lrpcjava.client.proxy;

import com.jijdy.lrpcjava.client.FutureHandler;
import com.jijdy.lrpcjava.codec.RPCRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class ClientProxy implements InvocationHandler {

    private final String version;

    /* 用于跳过注册中心，直接连接到远端服务的地址 */
    private final String addr;

    private final FutureHandler futureHandler;

    public ClientProxy() {
        this.futureHandler = FutureHandler.getFutureHandler();
        version = null;
        addr = null;
    }

    public ClientProxy(String version) {
        this.version = version;
        addr = null;
        this.futureHandler = FutureHandler.getFutureHandler();
    }

    /* 在创建代理对象前，将改字段的属性进行读取，并直接写入到该代理对象中 */
    public ClientProxy(String version,String addr) {
        this.addr = addr;
        this.version = version;
        this.futureHandler = FutureHandler.getFutureHandler();
    }


    public <T> Object getProxy(Class<T> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(clazz);
//        enhancer.setInterfaces(new Class[]{clazz});
//        enhancer.setCallback(this);
//        return enhancer.create();
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Class<?>[] parameterTypes = getTypes(objects);
        String methodName = method.getName();
        RPCRequest request = RPCRequest.builder()
                .parameterTypes(parameterTypes)
                .parameters(objects)
                .methodName(methodName)
                .requestId(UUID.randomUUID().toString())
                /* 接口名/服务名，默认为第一个接口的名称 */
                .interfaceName(o.getClass().getInterfaces()[0].getName())
                .version(getVersion())
                /* addr可能为null值，但若不为null，则会直接连接到这个地址 */
                .addr(getAddr())
                .build();
        /* 调用client连接到server，并实现远程服务调用 */
        return futureHandler.remoteInvoke(request);
    }

    /* 根据传入参数得到参数的类数据数组 */
    public Class<?>[] getTypes(Object[] objects) {
        if (null == objects) return null;
        Class<?>[] classes = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Class<?> aClass = objects[i].getClass();
            classes[i] = aClass;
        }
        return classes;
    }

    public String getVersion() {
        if (null == version) {
            return "";
        }
        return version;
    }

    public String getAddr() {
        return addr;
    }
}
