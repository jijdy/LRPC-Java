package com.jijdy.lrpcjava.codec;

import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;
import com.jijdy.lrpcjava.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/* RPC向Netty传输消息时使用的解码器
   将byteBuf中的字节数据解码为message对象
 * @Author jijdy
 * @Date 2021/12/18 13:40
 */
public class RPCDecoder extends ByteToMessageDecoder {

    private final Class<?> clazz;

    private final Serializer serializer;

    /* 需要在初始化时传入类，作为反编码的依据 */
    public RPCDecoder(Serializer serializer,Class<?> clazz) {
        this.serializer = serializer;
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        byte[] array = new byte[byteBuf.readInt()];
        byteBuf.readBytes(array);
        Object o ;
        try {
            o = serializer.deSerialize(array, clazz);
            if (o == null) {
                throw new Exception();
            }
            list.add(o);
        } catch (Exception e) {
            throw new RPCException(RPCErrorEnum.DESERIALIZE_FAILED,e);
        } finally {
            /* 清除缓冲区，防止内存泄漏 */
          byteBuf.clear();
        }
    }
}
