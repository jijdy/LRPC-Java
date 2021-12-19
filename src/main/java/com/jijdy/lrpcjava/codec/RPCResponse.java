package com.jijdy.lrpcjava.codec;

import lombok.ToString;

import java.io.Serializable;

/* 从服务器返回的对象，只需要做简单封装即可
 * @Author jijdy
 * @Date 2021/12/18 16:31
 */
@ToString
public class RPCResponse implements Serializable {
    public static final long serialVersionUID = 1823821843821L;

    private String requestId;

    /* 若有错误，返回的错误信息 */
    private String error;

    /* 调用的远程方法返回的对象 */
    private Object message;

    public RPCResponse(){}

    public RPCResponse(String requestId) {
        this.requestId = requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getError() {
        return error;
    }

    public Object getMessage() {
        return message;
    }
}
