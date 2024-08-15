package cn.rentaotao.netty.example.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author rtt
 * @create 2021/3/25 09:00
 */
public class Integer2ByteEncoder extends MessageToByteEncoder<Integer> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Integer integer, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(integer);
    }

}
