package com.jijdy.lrpcjava.serialize.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.jijdy.lrpcjava.serialize.Serializer;

/* use protostuff accomplish serialize
 * @Author jijdy
 * @Date 2021/12/18 14:09
 */
public class ProtostuffSerializer implements Serializer {

    private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object message) {
        Class<?> clazz = message.getClass();
        /* 在RuntimeSchema相关联的RuntimeEnv中，
        有一个静态对象IdStrategy,其默认实现类中就用用ConcurrentHashMap缓存了使用过的Schema模板 */
        Schema schema = RuntimeSchema.getSchema(clazz);
        try {
            return ProtostuffIOUtil.toByteArray(message, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deSerialize(byte[] buf, Class<T> t) {
        Schema<T> schema = RuntimeSchema.getSchema(t);
        T o = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(buf,o,schema);
        return o;
    }
}
