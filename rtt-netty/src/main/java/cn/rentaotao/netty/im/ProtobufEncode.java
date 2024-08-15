package cn.rentaotao.netty.im;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author rtt
 * @create 2021/3/26 09:34
 */
public class ProtobufEncode extends MessageToByteEncoder<ImOuterClass.Im.Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ImOuterClass.Im.Message msg, ByteBuf out) throws Exception {
        // 魔数，做安全校验
        out.writeShort(1);
        // 版本号，可以根据不同的版本号做不同的解析策略
        out.writeShort(2);
        byte[] bytes = msg.toByteArray();
        // 写入消息长度
        out.writeShort(bytes.length);
        // 写入消息内容
        out.writeBytes(bytes);
    }
}
