package com.jijdy.lrpcjava.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jijdy.lrpcjava.codec.RPCRequest;
import com.jijdy.lrpcjava.codec.RPCResponse;
import com.jijdy.lrpcjava.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/* kryo ---
 * @Author jijdy
 * @Date 2021/12/22 15:52
 */
public class KryoSerializer implements Serializer {

    /* kryo 不是线程安全的，所有使用单线程变量来使用 */
    private static final ThreadLocal<Kryo> kryo = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo1 = new Kryo();
            kryo1.register(RPCRequest.class);
            kryo1.register(RPCResponse.class);
            return kryo1;
        }
    };


    @Override
    public byte[] serialize(Object message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        Kryo kryo1 = kryo.get();
        // Object->byte:将对象序列化为byte数组
        kryo1.writeObject(output, message);
        kryo.remove();
        return output.toBytes();
    }

    @Override
    public <T> T deSerialize(byte[] buf, Class<T> t) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo1 = kryo.get();
        // byte->Object:从byte数组中反序列化出对对象
        Object o = kryo1.readObject(input, t);
        kryo.remove();
        return t.cast(o);
    }
}
