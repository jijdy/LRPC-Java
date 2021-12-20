package com.jijdy.lrpcjava.exception;

import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;

/* 业务异常的封装
 * @Author jijdy
 * @Date 2021/12/18 15:10
 */
public class RPCException extends RuntimeException{

    public RPCException(RPCErrorEnum RPCErrorEnum, Throwable throwable) {
        super(RPCErrorEnum.getMessage()+":"+throwable.getMessage());
    }

    public RPCException(RPCErrorEnum RPCErrorEnum) {
        super(RPCErrorEnum.getMessage());
    }

    public RPCException(RPCErrorEnum rpcErrorEnum,String message) {
        super(rpcErrorEnum.getMessage()+"  "+message);
    }


}
