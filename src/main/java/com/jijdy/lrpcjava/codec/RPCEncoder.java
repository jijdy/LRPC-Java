package com.jijdy.lrpcjava.codec;

import com.jijdy.lrpcjava.exception.RPCException;
import com.jijdy.lrpcjava.exception.enums.RPCErrorEnum;
import com.jijdy.lrpcjava.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/* Netty中使用的编码器，将对应的对象编码为指定的数据流
 * @Author jijdy
 * @Date 2021/12/18 13:41
 */
public class RPCEncoder extends MessageToByteEncoder<Object> {

    /* 改对象在读取配置之后进行写入，便于灵活选择序列化方案 */
    private final Serializer serializer;

    public RPCEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] bytes;
        try {
            bytes = serializer.serialize(o);

            /* 将转换完成的字节数组写入到byteBuf中，以便channel对象将其对外进行传输 */
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        } catch (Exception e) {
            throw new RPCException(RPCErrorEnum.SERIALIZE_FAILED,e);
        }
    }
}
