package com.jijdy.lrpcjava.client.proxy;

import com.jijdy.lrpcjava.client.FutureHandler;
import com.jijdy.lrpcjava.codec.RPCRequest;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class ClientProxy implements InvocationHandler {

    private String version;

    private final FutureHandler futureHandler;

    public ClientProxy() {
        this.futureHandler = FutureHandler.getFutureHandler();
    }

    ClientProxy(String version) {
        this.version = version;
        this.futureHandler = FutureHandler.getFutureHandler();
    }


    public <T> Object getProxy(Class<T> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this::invoke);
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
}
