package com.jijdy.lrpcjava.serialize;

/* a serializer interface for codec
 * @Author jijdy
 * @Date 2021/12/18 13:54
 */
public interface Serializer {

    /* 根据传入的对象，将其解析为字节数据 */
    byte[] serialize(Object message);

    /* 根据传入的字节数组和class类，返回解析后该类的对象 */
    <T> T deSerialize(byte[] buf, Class<T> t);
}
