package com.jijdy.lrpcjava.exception.enums;

/* 使用enum来规定异常信息
 * @Author jijdy
 * @Date 2021/12/18 15:10
 */
public enum RPCErrorEnum {
    SERIALIZE_FAILED("序列化失败!"),
    DESERIALIZE_FAILED("反序列化失败!"),
    SERVICE_NOT_FOUND("未找到指定服务!"),
    REGISTER_FAILED("注册失败!"),
    SERVICE_NOT_REGISTER("服务未注册!"),
    ;

    private final String message;

    RPCErrorEnum(String s) {
        this.message = s;
    }

    public String getMessage() {
        return this.message;
    }
}
