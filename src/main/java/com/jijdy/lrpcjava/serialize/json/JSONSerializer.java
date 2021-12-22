package com.jijdy.lrpcjava.serialize.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jijdy.lrpcjava.serialize.Serializer;

import java.io.IOException;

/* serialize request and response with JSON
 * @Author jijdy
 * @Date 2021/12/22 15:12
 */
public class JSONSerializer implements Serializer {

    /* 依靠objectMapper对象，使用静态代码块对其进行初始化设置，再使用其来完成序列化操作 */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        //为null值时，不进行序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 忽略未知字段
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public byte[] serialize(Object message) {
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return bytes;
    }


    @Override
    public <T> T deSerialize(byte[] buf, Class<T> t) {
        T t1 = null;
        try {
            t1= objectMapper.readValue(buf, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t1;
    }
}
