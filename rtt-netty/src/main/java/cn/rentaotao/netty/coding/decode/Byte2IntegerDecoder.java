package cn.rentaotao.netty.coding.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author rtt
 * @create 2021/3/24 14:09
 */
public class Byte2IntegerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() >= 4) {
            // 只会读取 byteBuf 的前四个字节
            int i = byteBuf.readInt();
            System.out.println("解码数据: " + i);
            list.add(i);
        }
    }
}
