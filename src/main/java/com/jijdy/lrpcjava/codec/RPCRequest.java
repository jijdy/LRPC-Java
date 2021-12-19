package com.jijdy.lrpcjava.codec;

import java.io.Serializable;

/* 请求对象的封装，需要携带有，请求接口名称，请求方法，请求参数的类型和具体参数
   使用建造者模式完成该类的创建
 * @Author jijdy
 * @Date 2021/12/18 16:01
 */
public class RPCRequest implements Serializable {
    private static final long serialVersionUID = 4561348856456L;

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    private String version;

    public RPCRequest(){}

    public RPCRequest(String requestId, String interfaceName, String methodName, Class<?>[] parameterTypes, Object[] parameters, String version) {
        this.requestId = requestId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.version = version;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getVersion() {
        if (null == version) {
            return "";
        }
        return version;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /* 建造者模式 */
    public static RPCRequestBuilder builder() {
        return new RPCRequestBuilder();
    }

    /* 建造者模式 */
    public static class RPCRequestBuilder {
        private String requestId;

        private String interfaceName;

        private String methodName;

        private Class<?>[] parameterTypes;

        private Object[] parameters;

        private String version;

        public RPCRequestBuilder requestId(String id) {
            this.requestId = id;
            return this;
        }

        public RPCRequestBuilder interfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
            return this;
        }

        public RPCRequestBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public RPCRequestBuilder parameterTypes(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }

        public RPCRequestBuilder parameters(Object[] parameters) {
            this.parameters = parameters;
            return this;
        }

        public RPCRequestBuilder version(String version) {
            this.version = version;
            return this;
        }

        public RPCRequest build() {
            return new RPCRequest(this.requestId,this.interfaceName,this.methodName,this.parameterTypes,this.parameters,this.version);
        }
    }

}
